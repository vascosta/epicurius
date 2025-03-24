package epicurius.services

import epicurius.domain.user.AuthenticatedUser
import epicurius.domain.CountriesDomain
import epicurius.domain.Diet
import epicurius.domain.FollowingStatus
import epicurius.domain.Intolerance
import epicurius.domain.user.UserDomain
import epicurius.domain.exceptions.InvalidCountry
import epicurius.domain.exceptions.IncorrectPassword
import epicurius.domain.exceptions.PasswordsDoNotMatch
import epicurius.domain.exceptions.UserAlreadyExists
import epicurius.domain.exceptions.UserAlreadyLoggedIn
import epicurius.domain.exceptions.UserNotFound
import epicurius.domain.user.FollowUser
import epicurius.domain.user.User
import epicurius.domain.user.UserProfile
import epicurius.http.user.models.input.UpdateUserInputModel
import epicurius.repository.transaction.TransactionManager
import epicurius.repository.transaction.cloudStorage.CloudStorageManager
import epicurius.repository.transaction.firestore.FirestoreManager
import epicurius.services.models.UpdateUserModel
import org.springframework.stereotype.Component
import java.lang.IllegalArgumentException

@Component
class UserService(
    private val tm: TransactionManager,
    private val fs: FirestoreManager,
    private val cs: CloudStorageManager,
    private val userDomain: UserDomain,
    private val countriesDomain: CountriesDomain
) {
    fun createUser(
        username: String,
        email: String,
        country: String,
        password: String,
        confirmPassword: String
    ): String {
        if (checkIfUserExists(username, email) != null) throw UserAlreadyExists()
        if (!countriesDomain.checkIfCodeIsValid(country)) throw InvalidCountry()
        checkIfPasswordsMatch(password, confirmPassword)
        val passwordHash = userDomain.encodePassword(password)

        tm.run { it.userRepository.createUser(username, email, country, passwordHash) }

        return createToken(username, email)
    }

    fun getAuthenticatedUser(token: String): AuthenticatedUser? {
        val tokenHash = userDomain.hashToken(token)
        val user = checkIfUserExists(tokenHash = tokenHash) ?: return null
        return AuthenticatedUser(user, token)
    }

    fun getUserProfile(username: String): UserProfile {
        val user = checkIfUserExists(username = username) ?: throw UserNotFound(username)
        return if (user.profilePictureName == null) {
            UserProfile(user.username, user.country, user.privacy, null)
        } else {
            val userProfilePicture = getProfilePicture(username)
            UserProfile(user.username, user.country, user.privacy, userProfilePicture)
        }
    }

    fun getFollowers(userId: Int) =
        tm.run { it.userRepository.getFollowers(userId).map { user -> FollowUser(user.username, getProfilePicture(user.username)) } }
    fun getFollowing(userId: Int) =
        tm.run { it.userRepository.getFollowing(userId).map { user -> FollowUser(user.username, getProfilePicture(user.username)) } }
    fun getFollowRequests(userId: Int) =
        tm.run {
            it.userRepository.getFollowRequests(userId)
                .map { user -> FollowUser(user.username, getProfilePicture(user.profilePictureName)) }
        }

    fun getProfilePicture(profilePictureName: String?): ByteArray? {
        if (profilePictureName == null) return null
        return cs.userCloudStorageRepository.getProfilePicture(profilePictureName)
    }

    fun addProfilePicture() { }

    fun login(username: String?, email: String?, password: String): String {
        val user = checkIfUserExists(username, email) ?: throw UserNotFound(username ?: email)
        checkIfUserIsLoggedIn(username, email)

        if (!userDomain.verifyPassword(password, user.passwordHash)) throw IncorrectPassword()
        return createToken(username, email)
    }

    fun follow(userId: Int, usernameToFollow: String) {
        val userToFollow = checkIfUserExists(username = usernameToFollow) ?: throw UserNotFound(usernameToFollow)
        checkIfUserIsAlreadyFollowing(userId, userToFollow.id)
        val followingStatus = if (userToFollow.privacy) FollowingStatus.PENDING else FollowingStatus.ACCEPTED
        tm.run {
            it.userRepository.followUser(userId, userToFollow.id, followingStatus.ordinal)
        }
    }

    fun updateUser(username: String, userUpdate: UpdateUserInputModel): User {
        if (checkIfUserExists(userUpdate.username, userUpdate.email) != null) throw UserAlreadyExists()

        if (userUpdate.country != null)
            if (!countriesDomain.checkIfCodeIsValid(userUpdate.country)) throw InvalidCountry()

        if (userUpdate.password != null) {
            if (userUpdate.confirmPassword == null) {
                throw PasswordsDoNotMatch()
            }
            checkIfPasswordsMatch(userUpdate.password, userUpdate.confirmPassword)
        }

        return tm.run {
            it.userRepository.updateUser(
                username,
                UpdateUserModel(
                    userUpdate.username,
                    userUpdate.email,
                    userUpdate.country,
                    userUpdate.password?.let { password -> userDomain.encodePassword(password) },
                    userUpdate.privacy,
                    userUpdate.intolerances?.map { intolerance ->  Intolerance.toInt(intolerance) },
                    userUpdate.diet?.map { diet -> Diet.toInt(diet) }
                )
            )
        }
    }

    fun updateProfilePicture() { }

    fun resetPassword(email: String, newPassword: String, confirmPassword: String) {
        checkIfPasswordsMatch(newPassword, confirmPassword)
        val passwordHash = userDomain.encodePassword(newPassword)

        tm.run {
            it.userRepository.resetPassword(email, passwordHash)
            deleteToken(email = email)
        }
    }

    fun logout(username: String) {
        deleteToken(username = username)
    }

    fun unfollow(username: String, usernameToUnfollow: String) {
        //fs.userRepository.removeFollowing(username, usernameToUnfollow)
    }

    private fun createToken(username: String? = null, email: String? = null): String {
        checkIfUserExists(username, email)
        checkIfUserIsLoggedIn(username, email)

        val token = userDomain.generateTokenValue()
        val tokenHash = userDomain.hashToken(token)
        tm.run { it.tokenRepository.createToken(tokenHash, username, email) }
        return token
    }

    private fun deleteToken(username: String? = null, email: String? = null) {
        tm.run { it.tokenRepository.deleteToken(username, email) }
    }

    private fun checkIfUserExists(username: String? = null, email: String? = null, tokenHash: String? = null): User?
        = tm.run { it.userRepository.getUser(username, email, tokenHash) }

    private fun checkIfUserIsLoggedIn(username: String? = null, email: String? = null) {
        if (tm.run { it.userRepository.checkIfUserIsLoggedIn(username, email) })
            throw UserAlreadyLoggedIn()
    }

    private fun checkIfUserIsAlreadyFollowing(userId: Int, userIdToFollow: Int) {
        if (tm.run { it.userRepository.checkIfUserIsAlreadyFollowing(userId, userIdToFollow) })
            throw IllegalArgumentException("User is already being followed by $userId")
    }


    private fun checkIfPasswordsMatch(password: String, confirmPassword: String) {
        if (password != confirmPassword) throw PasswordsDoNotMatch()
    }
}