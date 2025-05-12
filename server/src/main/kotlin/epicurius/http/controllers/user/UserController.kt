package epicurius.http.controllers.user

import epicurius.domain.PagingParams
import epicurius.domain.user.AuthenticatedUser
import epicurius.domain.user.FollowRequestType
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
import epicurius.http.pipeline.authentication.AuthenticationRefreshHandler
import epicurius.http.pipeline.authentication.addCookie
import epicurius.http.utils.Uris
import epicurius.services.user.UserService
import jakarta.servlet.http.Cookie
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
class UserController(val authenticationRefreshHandler: AuthenticationRefreshHandler, val userService: UserService) {

    @GetMapping(Uris.User.USER)
    fun getUserInfo(
        authenticatedUser: AuthenticatedUser,
        response: HttpServletResponse
    ): ResponseEntity<*> {
        return ResponseEntity
            .ok()
            .body(GetUserOutputModel(authenticatedUser.user.toUserInfo()))
            .addCookie(response, authenticationRefreshHandler.refreshToken(authenticatedUser.token))
    }

    @GetMapping(Uris.User.USER_PROFILE)
    fun getUserProfile(
        authenticatedUser: AuthenticatedUser,
        @PathVariable name: String,
        response: HttpServletResponse
    ): ResponseEntity<*> {
        return if (name == authenticatedUser.user.name) {
            val userProfilePicture = userService.getProfilePicture(authenticatedUser.user.profilePictureName)
            val followers = userService.getFollowers(authenticatedUser.user.id)
            val following = userService.getFollowing(authenticatedUser.user.id)
            val userProfile = UserProfile(
                authenticatedUser.user.name,
                authenticatedUser.user.country,
                authenticatedUser.user.privacy,
                userProfilePicture,
                followers,
                following,
            )
            ResponseEntity
                .ok()
                .body(GetUserProfileOutputModel(userProfile))
                .addCookie(response, authenticationRefreshHandler.refreshToken(authenticatedUser.token))
        } else {
            val userProfile = userService.getUserProfile(name)
            ResponseEntity
                .ok()
                .body(GetUserProfileOutputModel(userProfile))
                .addCookie(response, authenticationRefreshHandler.refreshToken(authenticatedUser.token))
        }
    }

    @GetMapping(Uris.User.USERS)
    fun searchUsers(
        authenticatedUser: AuthenticatedUser,
        @RequestParam partialUsername: String,
        @RequestParam skip: Int,
        @RequestParam limit: Int,
        response: HttpServletResponse
    ): ResponseEntity<*> {
        val pagingParams = PagingParams(skip, limit)
        val users = userService.searchUsers(authenticatedUser.user.id, partialUsername, pagingParams)
        return ResponseEntity
            .ok()
            .body(SearchUsersOutputModel(users))
            .addCookie(response, authenticationRefreshHandler.refreshToken(authenticatedUser.token))
    }

    @GetMapping(Uris.User.USER_INTOLERANCES)
    fun getUserIntolerances(
        authenticatedUser: AuthenticatedUser,
        response: HttpServletResponse
    ): ResponseEntity<*> {
        val intolerances = authenticatedUser.user.intolerances
        return ResponseEntity
            .ok()
            .body(GetUserIntolerancesOutputModel(intolerances))
            .addCookie(response, authenticationRefreshHandler.refreshToken(authenticatedUser.token))
    }

    @GetMapping(Uris.User.USER_DIETS)
    fun getUserDiet(
        authenticatedUser: AuthenticatedUser,
        response: HttpServletResponse
    ): ResponseEntity<*> {
        val diets = authenticatedUser.user.diets
        return ResponseEntity
            .ok()
            .body(GetUserDietsOutputModel(diets))
            .addCookie(response, authenticationRefreshHandler.refreshToken(authenticatedUser.token))
    }

    @GetMapping(Uris.User.USER_FOLLOWERS)
    fun getUserFollowers(
        authenticatedUser: AuthenticatedUser,
        response: HttpServletResponse
    ): ResponseEntity<*> {
        val followers = userService.getFollowers(authenticatedUser.user.id)
        return ResponseEntity
            .ok()
            .body(GetUserFollowersOutputModel(followers))
            .addCookie(response, authenticationRefreshHandler.refreshToken(authenticatedUser.token))
    }

    @GetMapping(Uris.User.USER_FOLLOWING)
    fun getUserFollowing(
        authenticatedUser: AuthenticatedUser,
        response: HttpServletResponse
    ): ResponseEntity<*> {
        val following = userService.getFollowing(authenticatedUser.user.id)
        return ResponseEntity
            .ok()
            .body(GetUserFollowingOutputModel(following))
            .addCookie(response, authenticationRefreshHandler.refreshToken(authenticatedUser.token))
    }

    @GetMapping(Uris.User.USER_FOLLOW_REQUESTS)
    fun getUserFollowRequests(
        authenticatedUser: AuthenticatedUser,
        response: HttpServletResponse
    ): ResponseEntity<*> {
        val followRequests = userService.getFollowRequests(authenticatedUser.user.id)
        return ResponseEntity
            .ok()
            .body(GetUserFollowRequestsOutputModel(followRequests))
            .addCookie(response, authenticationRefreshHandler.refreshToken(authenticatedUser.token))
    }

    @PostMapping(Uris.User.SIGNUP)
    fun signUp(
        @Valid @RequestBody body: SignUpInputModel,
        response: HttpServletResponse
    ): ResponseEntity<*> {
        val token = userService.createUser(body.name, body.email, body.country, body.password, body.confirmPassword)
        response.addCookie(Cookie("token", token))
        return ResponseEntity
            .created(Uris.User.userProfile(body.name))
            .build<Unit>()
    }

    @PostMapping(Uris.User.LOGIN)
    fun login(
        @Valid @RequestBody body: LoginInputModel,
        response: HttpServletResponse
    ): ResponseEntity<*> {
        val token = userService.login(body.name, body.email, body.password)
        response.addCookie(Cookie("token", token))
        return ResponseEntity
            .noContent()
            .build<Unit>()
    }

    @PostMapping(Uris.User.LOGOUT)
    fun logout(
        authenticatedUser: AuthenticatedUser,
        response: HttpServletResponse
    ): ResponseEntity<*> {
        userService.logout(authenticatedUser.user.id)
        response.addCookie(Cookie("token", ""))
        return ResponseEntity
            .noContent()
            .build<Unit>()
    }

    @PatchMapping(Uris.User.USER)
    fun updateUser(
        authenticatedUser: AuthenticatedUser,
        @Valid @RequestBody body: UpdateUserInputModel,
        response: HttpServletResponse
    ): ResponseEntity<*> {
        val updatedUserInfo = userService.updateUser(authenticatedUser.user.id, body)
        return ResponseEntity
            .ok()
            .body(UpdateUserOutputModel(updatedUserInfo))
            .addCookie(response, authenticationRefreshHandler.refreshToken(authenticatedUser.token))
    }

    @PatchMapping(Uris.User.USER_PICTURE, consumes = [MediaType.MULTIPART_FORM_DATA_VALUE])
    fun updateUserProfilePicture(
        authenticatedUser: AuthenticatedUser,
        @RequestPart("picture", required = false) picture: MultipartFile?,
        response: HttpServletResponse
    ): ResponseEntity<*> {
        val newProfilePicture = userService.updateProfilePicture(
            authenticatedUser.user.id,
            authenticatedUser.user.profilePictureName,
            picture
        )
        return if (newProfilePicture == null) {
            ResponseEntity
                .noContent()
                .build<Unit>()
                .addCookie(response, authenticationRefreshHandler.refreshToken(authenticatedUser.token))
        } else {
            ResponseEntity
                .ok()
                .body(UpdateUserProfilePictureOutputModel(newProfilePicture))
                .addCookie(response, authenticationRefreshHandler.refreshToken(authenticatedUser.token))
        }
    }

    @PatchMapping(Uris.User.USER_RESET_PASSWORD)
    fun resetUserPassword(
        @Valid @RequestBody body: ResetPasswordInputModel
    ): ResponseEntity<*> {
        userService.resetPassword(body.email, body.newPassword, body.confirmPassword)
        return ResponseEntity
            .noContent()
            .build<Unit>()
    }

    @PatchMapping(Uris.User.USER_FOLLOW)
    fun follow(
        authenticatedUser: AuthenticatedUser,
        @PathVariable name: String,
        response: HttpServletResponse
    ): ResponseEntity<*> {
        userService.follow(authenticatedUser.user.id, authenticatedUser.user.name, name)
        return ResponseEntity
            .noContent()
            .build<Unit>()
            .addCookie(response, authenticationRefreshHandler.refreshToken(authenticatedUser.token))
    }

    @PatchMapping(Uris.User.USER_FOLLOW_REQUEST)
    fun followRequest(
        authenticatedUser: AuthenticatedUser,
        @PathVariable name: String,
        @RequestParam type: FollowRequestType,
        response: HttpServletResponse
    ): ResponseEntity<*> {
        userService.followRequest(authenticatedUser.user.id, authenticatedUser.user.name, name, type)
        return ResponseEntity
            .noContent()
            .build<Unit>()
            .addCookie(response, authenticationRefreshHandler.refreshToken(authenticatedUser.token))
    }

    @DeleteMapping(Uris.User.USER_FOLLOW)
    fun unfollow(
        authenticatedUser: AuthenticatedUser,
        @PathVariable name: String,
        response: HttpServletResponse
    ): ResponseEntity<*> {
        userService.unfollow(authenticatedUser.user.id, authenticatedUser.user.name, name)
        return ResponseEntity
            .ok()
            .build<Unit>()
            .addCookie(response, authenticationRefreshHandler.refreshToken(authenticatedUser.token))
    }
}
