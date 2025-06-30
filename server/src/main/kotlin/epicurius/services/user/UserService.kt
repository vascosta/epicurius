package epicurius.services.user

import epicurius.domain.exceptions.FollowRequestAlreadyBeenSent
import epicurius.domain.exceptions.FollowRequestNotFound
import epicurius.domain.exceptions.IncorrectPassword
import epicurius.domain.exceptions.InvalidCountry
import epicurius.domain.exceptions.InvalidSelfAcceptFollowRequest
import epicurius.domain.exceptions.InvalidSelfCancelFollowRequest
import epicurius.domain.exceptions.InvalidSelfFollow
import epicurius.domain.exceptions.InvalidSelfRejectFollowRequest
import epicurius.domain.exceptions.InvalidSelfUnfollow
import epicurius.domain.exceptions.InvalidToken
import epicurius.domain.exceptions.PictureNotFound
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
import epicurius.http.controllers.user.models.input.UpdateUserInputModel
import epicurius.repository.cloudStorage.manager.CloudStorageManager
import epicurius.repository.jdbi.user.models.JdbiUpdateUserModel
import epicurius.repository.transaction.TransactionManager
import org.springframework.stereotype.Service
import org.springframework.web.multipart.MultipartFile
import java.time.LocalDate

@Service
class UserService(
    private val tm: TransactionManager,
    private val cs: CloudStorageManager,
    private val userDomain: UserDomain,
    private val pictureDomain: PictureDomain,
    private val countriesDomain: CountriesDomain
) {
    fun createUser(name: String, email: String, country: String, password: String): String {
        if (checkIfUserExists(name, email) != null) throw UserAlreadyExists()
        if (!countriesDomain.checkIfCountryCodeIsValid(country)) throw InvalidCountry()
        val passwordHash = userDomain.encodePassword(password)
        val userId = tm.run { it.userRepository.createUser(name, email, country, passwordHash) }
        return createToken(userId)
    }

    fun getAuthenticatedUser(token: String): AuthenticatedUser? {
        checkIfTokenIsValid(token)
        val tokenHash = userDomain.hashToken(token)
        val user = checkIfUserExists(tokenHash = tokenHash) ?: return null
        return AuthenticatedUser(user, token)
    }

    fun getUserProfile(authenticatedUserId: Int, name: String): UserProfile {
        val user = checkIfUserExists(name = name) ?: throw UserNotFound(name)
        val followers = getFollowersCount(user.id)
        val following = getFollowingCount(user.id)
        val isFollowing = if (authenticatedUserId == user.id) true else checkIfUserIsBeingFollowedBy(user.id, authenticatedUserId)
        val sentFollowRequest = if (authenticatedUserId == user.id) false else checkIfUserAlreadySentFollowRequest(user.id, authenticatedUserId)
        val followingStatus =
            if (isFollowing) FollowingStatus.ACCEPTED
            else if (sentFollowRequest) FollowingStatus.PENDING
            else null
        return if (user.profilePictureName == null) {
            UserProfile(user.name, user.country, user.privacy, null, followers, following, followingStatus)
        } else {
            val userProfilePicture = getProfilePicture(user.profilePictureName)
            UserProfile(user.name, user.country, user.privacy, userProfilePicture, followers, following, followingStatus)
        }
    }

    fun getProfilePicture(profilePictureName: String? = null): ByteArray? {
        if (profilePictureName == null) return null
        return cs.pictureRepository.getPicture(profilePictureName, PictureDomain.USERS_FOLDER)
    }

    fun searchUsers(userId: Int, partialUsername: String, lastUserId: Int?, limit: Int): List<SearchUser> {
        return tm.run { it.userRepository.searchUsers(userId, partialUsername, lastUserId, limit) }
            .map { user -> SearchUser(user.id, user.name, getProfilePicture(user.profilePictureName)) }
    }

    fun getFollowers(
        userId: Int,
        username: String?,
        partialFollowerName: String?,
        lastFollowerId: Int?,
        limit: Int
    ): List<FollowUser> {
        if (username != null) {
            val user = checkIfUserExists(name = username) ?: throw UserNotFound(username)
            return tm.run { it.userRepository.getFollowers(user.id, partialFollowerName, lastFollowerId, limit) }
                .map { u -> FollowUser(u.id, u.name, getProfilePicture(u.profilePictureName)) }

        }
        return tm.run { it.userRepository.getFollowers(userId, partialFollowerName, lastFollowerId, limit) }
            .map { u -> FollowUser(u.id, u.name, getProfilePicture(u.profilePictureName)) }
    }

    fun getFollowersCount(userId: Int) =
        tm.run { it.userRepository.getFollowersCount(userId) }

    fun getFollowing(
        userId: Int,
        username: String?,
        partialFollowingName: String?,
        lastFollowingId: Int?,
        limit: Int
    ): List<FollowingUser> {
        if (username != null) {
            val user = checkIfUserExists(name = username) ?: throw UserNotFound(username)
            return tm.run { it.userRepository.getFollowing(user.id, partialFollowingName, lastFollowingId, limit) }
                .map { u -> FollowingUser(u.id, u.name, getProfilePicture(u.profilePictureName)) }

        }
        return tm.run { it.userRepository.getFollowing(userId, partialFollowingName, lastFollowingId, limit) }
            .map { u -> FollowingUser(u.id, u.name, getProfilePicture(u.profilePictureName)) }
    }

    fun getFollowingCount(userId: Int) =
        tm.run { it.userRepository.getFollowingCount(userId) }

    fun getFollowRequests(userId: Int) =
        tm.run { it.userRepository.getFollowRequests(userId) }
            .map { user -> FollowUser(user.id, user.name, getProfilePicture(user.profilePictureName)) }

    fun login(name: String? = null, email: String? = null, password: String): String {
        val user = checkIfUserExists(name, email) ?: throw UserNotFound(name ?: email)
        checkIfUserIsLoggedIn(user.id)

        if (!userDomain.verifyPassword(password, user.passwordHash)) throw IncorrectPassword()
        return createToken(user.id)
    }

    fun logout(userId: Int) {
        deleteToken(userId)
    }

    fun updateUser(userId: Int, userUpdateInfo: UpdateUserInputModel): UserInfo {
        if (checkIfUserExists(userUpdateInfo.name, userUpdateInfo.email) != null) throw UserAlreadyExists()

        if (userUpdateInfo.country != null)
            if (!countriesDomain.checkIfCountryCodeIsValid(userUpdateInfo.country)) throw InvalidCountry()

        return tm.run {
            it.userRepository.updateUser(
                userId,
                userUpdateInfo.toJdbiUpdateUser(
                    userUpdateInfo.password?.let { password -> userDomain.encodePassword(password) }
                )
            ).toUserInfo()
        }
    }

    fun updateProfilePicture(
        userId: Int,
        profilePictureName: String? = null,
        profilePicture: MultipartFile? = null
    ): String? {
        return when {
            profilePictureName == null && profilePicture != null -> { // add new profile picture
                pictureDomain.validatePicture(profilePicture)
                val newProfilePictureName = pictureDomain.generatePictureName()

                cs.pictureRepository.updatePicture(newProfilePictureName, profilePicture, PictureDomain.USERS_FOLDER)
                tm.run {
                    it.userRepository.updateUser(userId, JdbiUpdateUserModel(profilePictureName = newProfilePictureName))
                }
                newProfilePictureName
            }

            profilePictureName != null && profilePicture != null -> { // update profile picture
                checkUserProfilePicture(userId, profilePictureName)
                pictureDomain.validatePicture(profilePicture)
                cs.pictureRepository.updatePicture(profilePictureName, profilePicture, PictureDomain.USERS_FOLDER)
                profilePictureName
            }

            profilePictureName != null && profilePicture == null -> { // remove profile picture
                checkUserProfilePicture(userId, profilePictureName)
                removeProfilePicture(userId, profilePictureName)
                null
            }

            else -> null
        }
    }

    fun resetPassword(email: String, newPassword: String) {
        val user = checkIfUserExists(email = email) ?: throw UserNotFound(email)
        val passwordHash = userDomain.encodePassword(newPassword)

        tm.run { it.userRepository.resetPassword(user.id, passwordHash) }
        deleteToken(user.id)
    }

    fun followRequest(userId: Int, username: String, usernameToRequest: String, type: FollowRequestType) {
        when (type) {
            FollowRequestType.CANCEL -> cancelFollowRequest(userId, username, usernameToRequest)
            FollowRequestType.ACCEPT -> acceptFollowRequest(userId, username, usernameToRequest)
            FollowRequestType.REJECT -> rejectFollowRequest(userId, username, usernameToRequest)
        }
    }

    fun follow(userId: Int, username: String, usernameToFollow: String) {
        if (checkSelf(username, usernameToFollow)) throw InvalidSelfFollow()
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
            it.userRepository.follow(userId, userToFollow.id, followingStatus.ordinal)
        }
    }

    fun unfollow(userId: Int, username: String, usernameToUnfollow: String) {
        if (checkSelf(username, usernameToUnfollow)) throw InvalidSelfUnfollow()
        val userToUnfollow = checkIfUserExists(name = usernameToUnfollow) ?: throw UserNotFound(usernameToUnfollow)
        if (!checkIfUserIsBeingFollowedBy(userToUnfollow.id, userId)) throw UserNotFollowed(usernameToUnfollow)
        tm.run {
            it.userRepository.unfollow(userId, userToUnfollow.id)
        }
    }

    fun deleteUser(userId: Int) {
        tm.run { it.userRepository.deleteUser(userId) }
    }

    private fun removeProfilePicture(userId: Int, profilePictureName: String) {
        cs.pictureRepository.deletePicture(profilePictureName, PictureDomain.USERS_FOLDER)
        tm.run { it.userRepository.updateUser(userId, JdbiUpdateUserModel(profilePictureName = null)) }
    }

    private fun cancelFollowRequest(userId: Int, username: String, usernameToCancelFollow: String) {
        if (checkSelf(username, usernameToCancelFollow)) throw InvalidSelfCancelFollowRequest()
        val userToCancelFollow = checkIfUserExists(name = usernameToCancelFollow) ?: throw UserNotFound(usernameToCancelFollow)
        if (!checkIfUserAlreadySentFollowRequest(userToCancelFollow.id, userId)) throw FollowRequestNotFound(usernameToCancelFollow)
        tm.run {
            it.userRepository.cancelFollowRequest(userToCancelFollow.id, userId)
        }
    }

    private fun acceptFollowRequest(userId: Int, username: String, usernameToAcceptFollow: String) {
        if (checkSelf(username, usernameToAcceptFollow)) throw InvalidSelfAcceptFollowRequest()
        val userToAcceptFollow = checkIfUserExists(name = usernameToAcceptFollow) ?: throw UserNotFound(usernameToAcceptFollow)
        if (!checkIfUserAlreadySentFollowRequest(userId, userToAcceptFollow.id)) throw FollowRequestNotFound(username)
        tm.run {
            it.userRepository.acceptFollowRequest(userId, userToAcceptFollow.id)
        }
    }

    private fun rejectFollowRequest(userId: Int, username: String, usernameToRejectFollow: String) {
        if (checkSelf(username, usernameToRejectFollow)) throw InvalidSelfRejectFollowRequest()
        val userToRejectFollow = checkIfUserExists(name = usernameToRejectFollow) ?: throw UserNotFound(usernameToRejectFollow)
        if (!checkIfUserAlreadySentFollowRequest(userId, userToRejectFollow.id)) throw FollowRequestNotFound(username)
        tm.run {
            it.userRepository.rejectFollowRequest(userId, userToRejectFollow.id)
        }
    }

    private fun createToken(userId: Int): String {
        checkIfUserIsLoggedIn(userId)
        val token = userDomain.generateTokenValue()
        val tokenHash = userDomain.hashToken(token)
        val lastUsed = LocalDate.now()
        tm.run { it.tokenRepository.createToken(tokenHash, lastUsed, userId) }
        return token
    }

    private fun deleteToken(userId: Int) {
        tm.run { it.tokenRepository.deleteToken(userId) }
    }

    private fun checkIfTokenIsValid(token: String) {
        if (!userDomain.isToken(token)) throw InvalidToken()
    }

    private fun checkIfUserExists(name: String? = null, email: String? = null, tokenHash: String? = null): User? =
        tm.run { it.userRepository.getUser(name, email, tokenHash) }

    private fun checkIfUserIsLoggedIn(userId: Int) {
        if (tm.run { it.userRepository.checkIfUserIsLoggedIn(userId) })
            throw UserAlreadyLoggedIn()
    }

    private fun checkIfUserIsBeingFollowedBy(userId: Int, followerId: Int) =
        tm.run { it.userRepository.checkIfUserIsBeingFollowedBy(userId, followerId) }

    private fun checkIfUserAlreadySentFollowRequest(userId: Int, followerId: Int) =
        tm.run { it.userRepository.checkIfUserAlreadySentFollowRequest(userId, followerId) }

    private fun checkSelf(userId: String, userId2: String) = userId == userId2

    private fun checkUserProfilePicture(userId: Int, profilePictureName: String?) {
        val userProfilePictureName = tm.run { it.userRepository.getUserProfilePictureName(userId) } ?: throw PictureNotFound()
        if (userProfilePictureName != profilePictureName) {
            throw PictureNotFound()
        }
    }
}
