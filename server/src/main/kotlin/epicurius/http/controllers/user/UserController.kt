package epicurius.http.controllers.user

import epicurius.domain.user.AuthenticatedUser
import epicurius.domain.user.FollowRequestType
import epicurius.domain.user.FollowingStatus
import epicurius.domain.user.UserProfile
import epicurius.http.controllers.user.models.input.LoginInputModel
import epicurius.http.controllers.user.models.input.ResetPasswordInputModel
import epicurius.http.controllers.user.models.input.SignUpInputModel
import epicurius.http.controllers.user.models.input.UpdateUserInputModel
import epicurius.http.controllers.user.models.output.GetUserDietsOutputModel
import epicurius.http.controllers.user.models.output.GetUserFollowRequestsOutputModel
import epicurius.http.controllers.user.models.output.GetUserFollowersOutputModel
import epicurius.http.controllers.user.models.output.GetUserFollowingOutputModel
import epicurius.http.controllers.user.models.output.GetUserIntolerancesOutputModel
import epicurius.http.controllers.user.models.output.GetUserOutputModel
import epicurius.http.controllers.user.models.output.GetUserProfileOutputModel
import epicurius.http.controllers.user.models.output.SearchUsersOutputModel
import epicurius.http.controllers.user.models.output.UpdateUserOutputModel
import epicurius.http.controllers.user.models.output.UpdateUserProfilePictureOutputModel
import epicurius.http.media.Uris
import epicurius.http.media.createdHttpResponse
import epicurius.http.media.noContentHttpResponse
import epicurius.http.media.okHttpResponse
import epicurius.http.pipeline.authentication.cookie.addCookie
import epicurius.http.pipeline.authentication.cookie.removeCookie
import epicurius.services.user.UserService
import jakarta.servlet.http.HttpServletResponse
import jakarta.validation.Valid
import org.springframework.http.MediaType
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.DeleteMapping
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PatchMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RequestPart
import org.springframework.web.bind.annotation.RestController
import org.springframework.web.multipart.MultipartFile

@RestController
@RequestMapping(Uris.PREFIX)
class UserController(val userService: UserService) {

    @GetMapping(Uris.User.USER)
    fun getUserInfo(
        authenticatedUser: AuthenticatedUser,
    ): ResponseEntity<*> {
        return okHttpResponse(GetUserOutputModel(authenticatedUser.user.toUserInfo()))
    }

    @GetMapping(Uris.User.USER_PROFILE)
    fun getUserProfile(
        authenticatedUser: AuthenticatedUser,
        @PathVariable name: String,
    ): ResponseEntity<*> {
        return if (name == authenticatedUser.user.name) {
            val userProfilePicture = userService.getProfilePicture(authenticatedUser.user.profilePictureName)
            val followersCount = userService.getFollowersCount(authenticatedUser.user.id)
            val followingCount = userService.getFollowingCount(authenticatedUser.user.id)
            val userProfile = UserProfile(
                authenticatedUser.user.name,
                authenticatedUser.user.country,
                authenticatedUser.user.privacy,
                userProfilePicture,
                followersCount,
                followingCount,
                FollowingStatus.ACCEPTED
            )
            okHttpResponse(GetUserProfileOutputModel(userProfile))
        } else {
            val userProfile = userService.getUserProfile(authenticatedUser.user.id, name)
            okHttpResponse(GetUserProfileOutputModel(userProfile))
        }
    }

    @GetMapping(Uris.User.USERS)
    fun searchUsers(
        authenticatedUser: AuthenticatedUser,
        @RequestParam partialUsername: String,
        @RequestParam lastUserId: Int?,
        @RequestParam limit: Int,
    ): ResponseEntity<*> {
        val users = userService.searchUsers(authenticatedUser.user.id, partialUsername, lastUserId, limit)
        return okHttpResponse(SearchUsersOutputModel(users))
    }

    @GetMapping(Uris.User.USER_INTOLERANCES)
    fun getUserIntolerances(
        authenticatedUser: AuthenticatedUser,
    ): ResponseEntity<*> {
        val intolerances = authenticatedUser.user.intolerances
        return okHttpResponse(GetUserIntolerancesOutputModel(intolerances))
    }

    @GetMapping(Uris.User.USER_DIETS)
    fun getUserDiets(
        authenticatedUser: AuthenticatedUser,
    ): ResponseEntity<*> {
        val diets = authenticatedUser.user.diets
        return okHttpResponse(GetUserDietsOutputModel(diets))
    }

    @GetMapping(Uris.User.USER_FOLLOWERS)
    fun getUserFollowers(
        authenticatedUser: AuthenticatedUser,
        @RequestParam username: String?,
        @RequestParam partialFollowerName: String?,
        @RequestParam lastFollowerId: Int?,
        @RequestParam limit: Int
    ): ResponseEntity<*> {
        val followers = userService.getFollowers(
            authenticatedUser.user.id,
            username,
            partialFollowerName,
            lastFollowerId,
            limit
        )
        return okHttpResponse(GetUserFollowersOutputModel(followers))
    }

    @GetMapping(Uris.User.USER_FOLLOWING)
    fun getUserFollowing(
        authenticatedUser: AuthenticatedUser,
        @RequestParam username: String?,
        @RequestParam partialFollowingName: String?,
        @RequestParam lastFollowingId: Int?,
        @RequestParam limit: Int
    ): ResponseEntity<*> {
        val following = userService.getFollowing(
            authenticatedUser.user.id,
            username,
            partialFollowingName,
            lastFollowingId,
            limit
        )
        return okHttpResponse(GetUserFollowingOutputModel(following))
    }

    @GetMapping(Uris.User.USER_FOLLOW_REQUESTS)
    fun getUserFollowRequests(
        authenticatedUser: AuthenticatedUser,
    ): ResponseEntity<*> {
        val followRequests = userService.getFollowRequests(authenticatedUser.user.id)
        return okHttpResponse(GetUserFollowRequestsOutputModel(followRequests))
    }

    @PostMapping(Uris.User.SIGNUP)
    fun signUp(
        @Valid @RequestBody body: SignUpInputModel,
        response: HttpServletResponse
    ): ResponseEntity<*> {
        val token = userService.createUser(body.name, body.email, body.country, body.password)
        return createdHttpResponse(Uris.User.userProfile(body.name), Unit).addCookie(response, token)
    }

    @PostMapping(Uris.User.LOGIN)
    fun login(
        @Valid @RequestBody body: LoginInputModel,
        response: HttpServletResponse
    ): ResponseEntity<*> {
        val token = userService.login(body.name, body.email, body.password)
        return noContentHttpResponse().addCookie(response, token)
    }

    @PostMapping(Uris.User.LOGOUT)
    fun logout(
        authenticatedUser: AuthenticatedUser,
        response: HttpServletResponse
    ): ResponseEntity<*> {
        userService.logout(authenticatedUser.user.id)
        return noContentHttpResponse().removeCookie(response)
    }

    @PatchMapping(Uris.User.USER)
    fun updateUser(
        authenticatedUser: AuthenticatedUser,
        @Valid @RequestBody body: UpdateUserInputModel,
    ): ResponseEntity<*> {
        val updatedUserInfo = userService.updateUser(authenticatedUser.user.id, body)
        return okHttpResponse(UpdateUserOutputModel(updatedUserInfo))
    }

    @PatchMapping(Uris.User.USER_PICTURE, consumes = [MediaType.MULTIPART_FORM_DATA_VALUE])
    fun updateUserProfilePicture(
        authenticatedUser: AuthenticatedUser,
        @RequestPart("picture", required = false) picture: MultipartFile,
    ): ResponseEntity<*> {
        val newProfilePicture = userService.updateProfilePicture(
            authenticatedUser.user.id,
            authenticatedUser.user.profilePictureName,
            picture
        )
        return if (newProfilePicture == null) {
            noContentHttpResponse()
        } else {
            okHttpResponse(UpdateUserProfilePictureOutputModel(newProfilePicture))
        }
    }

    @PatchMapping(Uris.User.USER_RESET_PASSWORD)
    fun resetUserPassword(
        @Valid @RequestBody body: ResetPasswordInputModel
    ): ResponseEntity<*> {
        userService.resetPassword(body.email, body.newPassword)
        return noContentHttpResponse()
    }

    @PatchMapping(Uris.User.USER_FOLLOW)
    fun follow(
        authenticatedUser: AuthenticatedUser,
        @PathVariable name: String,
    ): ResponseEntity<*> {
        userService.follow(authenticatedUser.user.id, authenticatedUser.user.name, name)
        return noContentHttpResponse()
    }

    @PatchMapping(Uris.User.USER_FOLLOW_REQUEST)
    fun followRequest(
        authenticatedUser: AuthenticatedUser,
        @PathVariable name: String,
        @RequestParam type: FollowRequestType,
    ): ResponseEntity<*> {
        userService.followRequest(authenticatedUser.user.id, authenticatedUser.user.name, name, type)
        return noContentHttpResponse()
    }

    @DeleteMapping(Uris.User.USER_FOLLOW)
    fun unfollow(
        authenticatedUser: AuthenticatedUser,
        @PathVariable name: String,
    ): ResponseEntity<*> {
        userService.unfollow(authenticatedUser.user.id, authenticatedUser.user.name, name)
        return noContentHttpResponse()
    }

    @DeleteMapping(Uris.User.USER)
    fun deleteUser(
        authenticatedUser: AuthenticatedUser,
        response: HttpServletResponse
    ): ResponseEntity<*> {
        userService.deleteUser(authenticatedUser.user.id)
        return noContentHttpResponse().removeCookie(response)
    }
}
