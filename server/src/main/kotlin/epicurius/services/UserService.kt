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
import epicurius.domain.user.SocialUser
import epicurius.domain.user.User
import epicurius.http.user.models.input.UpdateUserInputModel
import epicurius.repository.transaction.TransactionManager
import epicurius.repository.transaction.firestore.FirestoreManager
import epicurius.services.models.UpdateUserModel
import org.springframework.stereotype.Component
import java.lang.Exception
import java.lang.IllegalArgumentException

@Component
class UserService(
    private val tm: TransactionManager,
    private val fs: FirestoreManager,
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

    fun getFollowers(userId: Int) = tm.run { it.userRepository.getFollowers(userId) }
    fun getFollowing(userId: Int) = tm.run { it.userRepository.getFollowing(userId) }

    fun getFollowingRequests(username: String) {

    }

    fun getProfilePicture(username: String) { }

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

    fun updateProfile(username: String, userUpdate: UpdateUserInputModel) {
        if (checkIfUserExists(userUpdate.username, userUpdate.email) != null) throw UserAlreadyExists()

        if (userUpdate.country != null)
            if (!countriesDomain.checkIfCodeIsValid(userUpdate.country)) throw InvalidCountry()

        if (userUpdate.password != null) {
            if (userUpdate.confirmPassword == null) {
                throw PasswordsDoNotMatch()
            }
            checkIfPasswordsMatch(userUpdate.password, userUpdate.confirmPassword)
        }

        tm.run {
            it.userRepository.updateProfile(
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

    fun removeProfilePicture() { }

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