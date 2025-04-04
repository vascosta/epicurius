package epicurius.services

import epicurius.domain.CountriesDomain
import epicurius.domain.Diet
import epicurius.domain.FollowingStatus
import epicurius.domain.Intolerance
import epicurius.domain.PagingParams
import epicurius.domain.exceptions.FollowRequestAlreadyBeenSent
import epicurius.domain.exceptions.FollowRequestNotFound
import epicurius.domain.exceptions.IncorrectPassword
import epicurius.domain.exceptions.InvalidCountry
import epicurius.domain.exceptions.InvalidFollowRequestType
import epicurius.domain.exceptions.InvalidToken
import epicurius.domain.exceptions.PasswordsDoNotMatch
import epicurius.domain.exceptions.UserAlreadyBeingFollowed
import epicurius.domain.exceptions.UserAlreadyExists
import epicurius.domain.exceptions.UserAlreadyLoggedIn
import epicurius.domain.exceptions.UserNotFollowed
import epicurius.domain.exceptions.UserNotFound
import epicurius.domain.user.AuthenticatedUser
import epicurius.domain.user.FollowRequestType
import epicurius.domain.user.FollowUser
import epicurius.domain.user.FollowingUser
import epicurius.domain.user.SearchUser
import epicurius.domain.user.UpdateUserInfo
import epicurius.domain.user.User
import epicurius.domain.user.UserDomain
import epicurius.domain.user.UserInfo
import epicurius.domain.user.UserProfile
import epicurius.http.user.models.input.UpdateUserInputModel
import epicurius.repository.cloudStorage.CloudStorageManager
import epicurius.repository.transaction.TransactionManager
import org.springframework.stereotype.Component
import org.springframework.web.multipart.MultipartFile
import java.util.UUID

@Component
class UserService(
    private val tm: TransactionManager,
    // private val fs: FirestoreManager,
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
        checkIfTokenIsValid(token)
        val tokenHash = userDomain.hashToken(token)
        val user = checkIfUserExists(tokenHash = tokenHash) ?: return null
        return AuthenticatedUser(user, token)
    }

    fun getUserProfile(username: String): UserProfile {
        val user = checkIfUserExists(username = username) ?: throw UserNotFound(username)
        val followers = getFollowers(user.id)
        val following = getFollowing(user.id)
        return if (user.profilePictureName == null) {
            UserProfile(user.username, user.country, user.privacy, null, followers, following)
        } else {
            val userProfilePicture = getProfilePicture(user.profilePictureName)
            UserProfile(user.username, user.country, user.privacy, userProfilePicture, followers, following)
        }
    }

    fun getProfilePicture(profilePictureName: String?): ByteArray? {
        if (profilePictureName == null) return null
        return cs.userCloudStorageRepository.getProfilePicture(profilePictureName)
    }

    fun getUsers(partialUsername: String, pagingParams: PagingParams): List<SearchUser> {
        return tm.run { it.userRepository.getUsers(partialUsername, pagingParams) }
            .map { user -> SearchUser(user.username, getProfilePicture(user.profilePictureName)) }
    }

    fun getFollowers(userId: Int) =
        tm.run { it.userRepository.getFollowers(userId) }
            .map { user -> FollowUser(user.username, getProfilePicture(user.profilePictureName)) }

    fun getFollowing(userId: Int) =
        tm.run { it.userRepository.getFollowing(userId) }
            .map { user -> FollowingUser(user.username, getProfilePicture(user.profilePictureName)) }

    fun getFollowRequests(userId: Int) =
        tm.run { it.userRepository.getFollowRequests(userId) }
            .map { user -> FollowUser(user.username, getProfilePicture(user.profilePictureName)) }

    fun login(username: String?, email: String?, password: String): String {
        val user = checkIfUserExists(username, email) ?: throw UserNotFound(username ?: email)
        checkIfUserIsLoggedIn(username, email)

        if (!userDomain.verifyPassword(password, user.passwordHash)) throw IncorrectPassword()
        return createToken(username, email)
    }

    fun logout(username: String) {
        deleteToken(username = username)
    }

    fun updateUser(username: String, userUpdate: UpdateUserInputModel): UserInfo {
        if (checkIfUserExists(userUpdate.username, userUpdate.email) != null) throw UserAlreadyExists()

        if (userUpdate.country != null)
            if (!countriesDomain.checkIfCodeIsValid(userUpdate.country)) throw InvalidCountry()

        if (userUpdate.password != null) {
            checkIfPasswordsMatch(userUpdate.password, userUpdate.confirmPassword)
        }

        return tm.run {
            it.userRepository.updateUser(
                username,
                UpdateUserInfo(
                    userUpdate.username,
                    userUpdate.email,
                    userUpdate.country,
                    userUpdate.password?.let { password -> userDomain.encodePassword(password) },
                    userUpdate.privacy,
                    userUpdate.intolerances?.map { intolerance -> Intolerance.toInt(intolerance) },
                    userUpdate.diets?.map { diet -> Diet.toInt(diet) }
                )
            ).toUserInfo()
        }
    }

    fun updateProfilePicture(username: String, profilePictureName: String? = null, profilePicture: MultipartFile?): String? {
        return when {
            profilePictureName == null && profilePicture != null -> { // add new profile picture
                userDomain.validateProfilePicture(profilePicture)
                val newProfilePictureName = UUID.randomUUID().toString()

                cs.userCloudStorageRepository.updateProfilePicture(newProfilePictureName, profilePicture)
                tm.run {
                    it.userRepository.updateUser(username, UpdateUserInfo(profilePictureName = newProfilePictureName))
                }
                newProfilePictureName
            }

            profilePictureName != null && profilePicture != null -> { // update profile picture
                userDomain.validateProfilePicture(profilePicture)
                cs.userCloudStorageRepository.updateProfilePicture(profilePictureName, profilePicture)
                profilePictureName
            }

            profilePictureName != null && profilePicture == null -> { // remove profile picture
                removeProfilePicture(username, profilePictureName)
                null
            }

            else -> null
        }
    }

    fun resetPassword(email: String, newPassword: String, confirmPassword: String) {
        checkIfPasswordsMatch(newPassword, confirmPassword)
        val passwordHash = userDomain.encodePassword(newPassword)

        tm.run {
            it.userRepository.resetPassword(email, passwordHash)
            deleteToken(email = email)
        }
    }

    fun followRequest(userId: Int, username: String, type: FollowRequestType) {
        when (type) {
            FollowRequestType.CANCEL -> cancelFollowRequest(userId, username)
            // "accept" -> userService.acceptFollowRequest(authenticatedUser.user.id, username)
            // "reject" -> userService.rejectFollowRequest(authenticatedUser.user.id, username)
            else -> throw InvalidFollowRequestType()
        }
    }

    fun follow(userId: Int, usernameToFollow: String) {
        val userToFollow = checkIfUserExists(username = usernameToFollow) ?: throw UserNotFound(usernameToFollow)
        if (checkIfUserIsBeingFollowedBy(userToFollow.id, userId)) throw UserAlreadyBeingFollowed(usernameToFollow)
        val followingStatus =
            if (userToFollow.privacy) {
                if (checkIfUserAlreadySentFollowRequest(userToFollow.id, userId)) throw FollowRequestAlreadyBeenSent(usernameToFollow)
                FollowingStatus.PENDING
            } else {
                FollowingStatus.ACCEPTED
            }

        tm.run {
            it.userRepository.followUser(userId, userToFollow.id, followingStatus.ordinal)
        }
    }

    fun unfollow(userId: Int, usernameToUnfollow: String) {
        val userToUnfollow = checkIfUserExists(username = usernameToUnfollow) ?: throw UserNotFound(usernameToUnfollow)
        if (!checkIfUserIsBeingFollowedBy(userToUnfollow.id, userId)) throw UserNotFollowed(usernameToUnfollow)
        tm.run {
            it.userRepository.unfollowUser(userId, userToUnfollow.id)
        }
    }

    private fun removeProfilePicture(username: String, profilePictureName: String) {
        cs.userCloudStorageRepository.deleteProfilePicture(profilePictureName)
        tm.run { it.userRepository.updateUser(username, UpdateUserInfo(profilePictureName = null)) }
    }

    private fun cancelFollowRequest(userId: Int, usernameToCancelFollow: String) {
        val userToCancelFollow = checkIfUserExists(username = usernameToCancelFollow) ?: throw UserNotFound(usernameToCancelFollow)
        if (!checkIfUserAlreadySentFollowRequest(userToCancelFollow.id, userId)) throw FollowRequestNotFound(usernameToCancelFollow)
        tm.run {
            it.userRepository.cancelFollowRequest(userToCancelFollow.id, userId)
        }
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

    private fun checkIfTokenIsValid(token: String) {
        if (!userDomain.isToken(token)) throw InvalidToken()
    }

    private fun checkIfUserExists(username: String? = null, email: String? = null, tokenHash: String? = null): User? =
        tm.run { it.userRepository.getUser(username, email, tokenHash) }

    private fun checkIfUserIsLoggedIn(username: String? = null, email: String? = null) {
        if (tm.run { it.userRepository.checkIfUserIsLoggedIn(username, email) })
            throw UserAlreadyLoggedIn()
    }

    private fun checkIfUserIsBeingFollowedBy(userId: Int, followerId: Int) =
        tm.run { it.userRepository.checkIfUserIsBeingFollowedBy(userId, followerId) }

    private fun checkIfUserAlreadySentFollowRequest(userId: Int, followerId: Int) =
        tm.run { it.userRepository.checkIfUserAlreadySentFollowRequest(userId, followerId) }

    private fun checkIfPasswordsMatch(password: String, confirmPassword: String?) {
        if (password != confirmPassword) throw PasswordsDoNotMatch()
    }
}
