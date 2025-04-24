package epicurius.services.user

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
import epicurius.domain.picture.PictureDomain
import epicurius.domain.user.AuthenticatedUser
import epicurius.domain.user.CountriesDomain
import epicurius.domain.user.FollowRequestType
import epicurius.domain.user.FollowUser
import epicurius.domain.user.FollowingStatus
import epicurius.domain.user.FollowingUser
import epicurius.domain.user.SearchUser
import epicurius.domain.user.User
import epicurius.domain.user.UserDomain
import epicurius.domain.user.UserInfo
import epicurius.domain.user.UserProfile
import epicurius.http.user.models.input.UpdateUserInputModel
import epicurius.repository.cloudStorage.manager.CloudStorageManager
import epicurius.repository.jdbi.user.models.JdbiUpdateUserModel
import epicurius.repository.transaction.TransactionManager
import org.springframework.stereotype.Component
import org.springframework.web.multipart.MultipartFile

@Component
class UserService(
    private val tm: TransactionManager,
    private val cs: CloudStorageManager,
    private val userDomain: UserDomain,
    private val pictureDomain: PictureDomain,
    private val countriesDomain: CountriesDomain
) {
    fun createUser(name: String, email: String, country: String, password: String, confirmPassword: String): String {
        if (checkIfUserExists(name, email) != null) throw UserAlreadyExists()
        if (!countriesDomain.checkIfCountryCodeIsValid(country)) throw InvalidCountry()
        checkIfPasswordsMatch(password, confirmPassword)
        val passwordHash = userDomain.encodePassword(password)
        tm.run { it.userRepository.createUser(name, email, country, passwordHash) }
        return createToken(name, email)
    }

    fun getAuthenticatedUser(token: String): AuthenticatedUser? {
        checkIfTokenIsValid(token)
        val tokenHash = userDomain.hashToken(token)
        val user = checkIfUserExists(tokenHash = tokenHash) ?: return null
        return AuthenticatedUser(user, token)
    }

    fun getUserProfile(name: String): UserProfile {
        val user = checkIfUserExists(name = name) ?: throw UserNotFound(name)
        val followers = getFollowers(user.id)
        val following = getFollowing(user.id)
        return if (user.profilePictureName == null) {
            UserProfile(user.name, user.country, user.privacy, null, followers, following)
        } else {
            val userProfilePicture = getProfilePicture(user.profilePictureName)
            UserProfile(user.name, user.country, user.privacy, userProfilePicture, followers, following)
        }
    }

    fun getProfilePicture(profilePictureName: String?): ByteArray? {
        if (profilePictureName == null) return null
        return cs.pictureRepository.getPicture(profilePictureName, PictureDomain.USERS_FOLDER)
    }

    fun searchUsers(partialUsername: String, pagingParams: PagingParams): List<SearchUser> {
        return tm.run { it.userRepository.searchUsers(partialUsername, pagingParams) }
            .map { user -> SearchUser(user.name, getProfilePicture(user.profilePictureName)) }
    }

    fun getFollowers(userId: Int) =
        tm.run { it.userRepository.getFollowers(userId) }
            .map { user -> FollowUser(user.name, getProfilePicture(user.profilePictureName)) }

    fun getFollowing(userId: Int) =
        tm.run { it.userRepository.getFollowing(userId) }
            .map { user -> FollowingUser(user.name, getProfilePicture(user.profilePictureName)) }

    fun getFollowRequests(userId: Int) =
        tm.run { it.userRepository.getFollowRequests(userId) }
            .map { user -> FollowUser(user.name, getProfilePicture(user.profilePictureName)) }

    fun login(name: String?, email: String?, password: String): String {
        val user = checkIfUserExists(name, email) ?: throw UserNotFound(name ?: email)
        checkIfUserIsLoggedIn(name, email)

        if (!userDomain.verifyPassword(password, user.passwordHash)) throw IncorrectPassword()
        return createToken(name, email)
    }

    fun logout(name: String) {
        deleteToken(name = name)
    }

    fun updateUser(name: String, userUpdateInfo: UpdateUserInputModel): UserInfo {
        if (checkIfUserExists(userUpdateInfo.name, userUpdateInfo.email) != null) throw UserAlreadyExists()

        if (userUpdateInfo.country != null)
            if (!countriesDomain.checkIfCountryCodeIsValid(userUpdateInfo.country)) throw InvalidCountry()

        if (userUpdateInfo.password != null) {
            checkIfPasswordsMatch(userUpdateInfo.password, userUpdateInfo.confirmPassword)
        }

        return tm.run {
            it.userRepository.updateUser(
                name,
                userUpdateInfo.toJdbiUpdateUser(
                    userUpdateInfo.password?.let { password -> userDomain.encodePassword(password) }
                )
            ).toUserInfo()
        }
    }

    fun updateProfilePicture(username: String, profilePictureName: String? = null, profilePicture: MultipartFile?): String? {
        return when {
            profilePictureName == null && profilePicture != null -> { // add new profile picture
                pictureDomain.validatePicture(profilePicture)
                val newProfilePictureName = pictureDomain.generatePictureName()

                cs.pictureRepository.updatePicture(newProfilePictureName, profilePicture, PictureDomain.USERS_FOLDER)
                tm.run {
                    it.userRepository.updateUser(username, JdbiUpdateUserModel(profilePictureName = newProfilePictureName))
                }
                newProfilePictureName
            }

            profilePictureName != null && profilePicture != null -> { // update profile picture
                pictureDomain.validatePicture(profilePicture)
                cs.pictureRepository.updatePicture(profilePictureName, profilePicture, PictureDomain.USERS_FOLDER)
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
            // "accept" -> acceptFollowRequest(authenticatedUser.user.id, username)
            // "reject" -> rejectFollowRequest(authenticatedUser.user.id, username)
            else -> throw InvalidFollowRequestType()
        }
    }

    fun follow(userId: Int, usernameToFollow: String) {
        val userToFollow = checkIfUserExists(name = usernameToFollow) ?: throw UserNotFound(usernameToFollow)
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
        val userToUnfollow = checkIfUserExists(name = usernameToUnfollow) ?: throw UserNotFound(usernameToUnfollow)
        if (!checkIfUserIsBeingFollowedBy(userToUnfollow.id, userId)) throw UserNotFollowed(usernameToUnfollow)
        tm.run {
            it.userRepository.unfollowUser(userId, userToUnfollow.id)
        }
    }

    private fun removeProfilePicture(username: String, profilePictureName: String) {
        cs.pictureRepository.deletePicture(profilePictureName, PictureDomain.USERS_FOLDER)
        tm.run { it.userRepository.updateUser(username, JdbiUpdateUserModel(profilePictureName = null)) }
    }

    private fun cancelFollowRequest(userId: Int, usernameToCancelFollow: String) {
        val userToCancelFollow = checkIfUserExists(name = usernameToCancelFollow) ?: throw UserNotFound(usernameToCancelFollow)
        if (!checkIfUserAlreadySentFollowRequest(userToCancelFollow.id, userId)) throw FollowRequestNotFound(usernameToCancelFollow)
        tm.run {
            it.userRepository.cancelFollowRequest(userToCancelFollow.id, userId)
        }
    }

    private fun createToken(name: String? = null, email: String? = null): String {
        checkIfUserIsLoggedIn(name, email)
        val token = userDomain.generateTokenValue()
        val tokenHash = userDomain.hashToken(token)
        tm.run { it.tokenRepository.createToken(tokenHash, name, email) }
        return token
    }

    private fun deleteToken(name: String? = null, email: String? = null) {
        tm.run { it.tokenRepository.deleteToken(name, email) }
    }

    private fun checkIfTokenIsValid(token: String) {
        if (!userDomain.isToken(token)) throw InvalidToken()
    }

    private fun checkIfUserExists(name: String? = null, email: String? = null, tokenHash: String? = null): User? =
        tm.run { it.userRepository.getUser(name, email, tokenHash) }

    private fun checkIfUserIsLoggedIn(name: String? = null, email: String? = null) {
        if (tm.run { it.userRepository.checkIfUserIsLoggedIn(name, email) })
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
