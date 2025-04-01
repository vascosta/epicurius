package epicurius.http.user

import epicurius.domain.PagingParams
import epicurius.domain.user.AuthenticatedUser
import epicurius.domain.user.UserProfile
import epicurius.http.user.models.input.CancelFollowRequestInputModel
import epicurius.http.user.models.input.FollowInputModel
import epicurius.http.user.models.input.LoginInputModel
import epicurius.http.user.models.input.ResetPasswordInputModel
import epicurius.http.user.models.input.SignUpInputModel
import epicurius.http.user.models.input.UnfollowInputModel
import epicurius.http.user.models.input.UpdateProfilePictureInputModel
import epicurius.http.user.models.input.UpdateUserInputModel
import epicurius.http.user.models.output.GetDietsOutputModel
import epicurius.http.user.models.output.GetFollowRequestsOutputModel
import epicurius.http.user.models.output.GetFollowersOutputModel
import epicurius.http.user.models.output.GetFollowingOutputModel
import epicurius.http.user.models.output.GetIntolerancesOutputModel
import epicurius.http.user.models.output.GetUserOutputModel
import epicurius.http.user.models.output.GetUserProfileOutputModel
import epicurius.http.user.models.output.GetUsersOutputModel
import epicurius.http.user.models.output.UpdateProfilePictureOutputModel
import epicurius.http.user.models.output.UpdateUserOutputModel
import epicurius.http.utils.Uris
import epicurius.services.UserService
import jakarta.servlet.http.HttpServletResponse
import jakarta.validation.Valid
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PatchMapping
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController
import java.net.URI

@RestController
@RequestMapping(Uris.PREFIX)
class UserController(val userService: UserService) {

    @GetMapping(Uris.User.USER)
    fun getUser(authenticatedUser: AuthenticatedUser): ResponseEntity<*> {
        return ResponseEntity.ok().body(GetUserOutputModel(authenticatedUser.user.toUserInfo()))
    }

    @GetMapping(Uris.User.USER_PROFILE)
    fun getUserProfile(
        authenticatedUser: AuthenticatedUser,
        @RequestParam username: String
    ): ResponseEntity<*> {
        return if (username.isEmpty()) {
            val userProfilePicture = userService.getProfilePicture(authenticatedUser.user.profilePictureName)
            val followers = userService.getFollowers(authenticatedUser.user.id)
            val following = userService.getFollowing(authenticatedUser.user.id)
            val userProfile = UserProfile(
                authenticatedUser.user.username,
                authenticatedUser.user.country,
                authenticatedUser.user.privacy,
                userProfilePicture,
                followers,
                following,
            )
            ResponseEntity.ok().body(GetUserProfileOutputModel(userProfile))
        } else {
            val userProfile = userService.getUserProfile(username)
            ResponseEntity.ok().body(GetUserProfileOutputModel(userProfile))
        }
    }

    @GetMapping(Uris.User.USERS)
    fun getUsers(
        authenticatedUser: AuthenticatedUser,
        @RequestParam partialUsername: String,
        @RequestParam skip: Int,
        @RequestParam limit: Int
    ): ResponseEntity<*> {
        val pagingParams = PagingParams(skip, limit)
        val users = userService.getUsers(partialUsername, pagingParams)
        return ResponseEntity.ok().body(GetUsersOutputModel(users))
    }

    @GetMapping(Uris.User.USER_INTOLERANCES)
    fun getIntolerances(authenticatedUser: AuthenticatedUser): ResponseEntity<*> {
        val intolerances = authenticatedUser.user.intolerances
        return ResponseEntity.ok().body(GetIntolerancesOutputModel(intolerances))
    }

    @GetMapping(Uris.User.USER_DIETS)
    fun getDiet(authenticatedUser: AuthenticatedUser): ResponseEntity<*> {
        val diet = authenticatedUser.user.diets
        return ResponseEntity.ok().body(GetDietsOutputModel(diet))
    }

    @GetMapping(Uris.User.USER_FOLLOWERS)
    fun getFollowers(authenticatedUser: AuthenticatedUser): ResponseEntity<*> {
        val followers = userService.getFollowers(authenticatedUser.user.id)
        return ResponseEntity.ok().body(GetFollowersOutputModel(followers))
    }

    @GetMapping(Uris.User.USER_FOLLOWING)
    fun getFollowing(authenticatedUser: AuthenticatedUser): ResponseEntity<*> {
        val following = userService.getFollowing(authenticatedUser.user.id)
        return ResponseEntity.ok().body(GetFollowingOutputModel(following))
    }

    @GetMapping(Uris.User.USER_FOLLOW_REQUESTS)
    fun getFollowRequests(authenticatedUser: AuthenticatedUser): ResponseEntity<*> {
        val followRequests = userService.getFollowRequests(authenticatedUser.user.id)
        return ResponseEntity.ok().body(GetFollowRequestsOutputModel(followRequests))
    }

    @PostMapping(Uris.User.SIGNUP)
    fun signUp(@Valid @RequestBody body: SignUpInputModel, response: HttpServletResponse): ResponseEntity<*> {
        val token = userService.createUser(body.username, body.email, body.country, body.password, body.confirmPassword)
        response.addHeader("Authorization", "Bearer $token")
        return ResponseEntity.created(URI.create(Uris.User.SIGNUP)).build<Unit>()
    }

    @PostMapping(Uris.User.LOGIN)
    fun login(@Valid @RequestBody body: LoginInputModel, response: HttpServletResponse): ResponseEntity<*> {
        val token = userService.login(body.username, body.email, body.password)
        response.addHeader("Authorization", "Bearer $token")
        return ResponseEntity.noContent().build<Unit>()
    }

    @PostMapping(Uris.User.LOGOUT)
    fun logout(authenticatedUser: AuthenticatedUser, response: HttpServletResponse): ResponseEntity<*> {
        userService.logout(authenticatedUser.user.username)
        response.addHeader("Authorization", "")
        return ResponseEntity.noContent().build<Unit>()
    }

    @PatchMapping(Uris.User.USER)
    fun updateUser(
        authenticatedUser: AuthenticatedUser,
        @Valid @RequestBody body: UpdateUserInputModel
    ): ResponseEntity<*> {
        val updatedUserInfo = userService.updateUser(authenticatedUser.user.username, body)
        return ResponseEntity.ok().body(
            UpdateUserOutputModel(updatedUserInfo)
        )
    }

    @PatchMapping(Uris.User.USER_PROFILE_PICTURE)
    fun updateProfilePicture(
        authenticatedUser: AuthenticatedUser,
        @Valid @RequestBody body: UpdateProfilePictureInputModel
    ): ResponseEntity<*> {
        val newProfilePicture = userService.updateProfilePicture(
            authenticatedUser.user.username,
            authenticatedUser.user.profilePictureName,
            body.profilePicture
        )
        return ResponseEntity.ok().body(UpdateProfilePictureOutputModel(newProfilePicture))
    }

    @PatchMapping(Uris.User.USER_RESET_PASSWORD)
    fun resetPassword(
        @Valid @RequestBody body: ResetPasswordInputModel
    ): ResponseEntity<*> {
        userService.resetPassword(body.email, body.newPassword, body.confirmPassword)
        return ResponseEntity.noContent().build<Unit>()
    }

    @PatchMapping(Uris.User.USER_FOLLOW)
    fun follow(authenticatedUser: AuthenticatedUser, @Valid @RequestBody body: FollowInputModel): ResponseEntity<*> {
        userService.follow(authenticatedUser.user.id, body.username)
        return ResponseEntity.noContent().build<Unit>()
    }

    @PatchMapping(Uris.User.USER_UNFOLLOW)
    fun unfollow(authenticatedUser: AuthenticatedUser, @Valid @RequestBody body: UnfollowInputModel): ResponseEntity<*> {
        userService.unfollow(authenticatedUser.user.id, body.username)
        return ResponseEntity.noContent().build<Unit>()
    }

    @PatchMapping(Uris.User.USER_FOLLOW_REQUESTS)
    fun cancelFollowRequest(
        authenticatedUser: AuthenticatedUser,
        @Valid @RequestBody body: CancelFollowRequestInputModel
    ): ResponseEntity<*> {
        userService.cancelFollowRequest(authenticatedUser.user.id, body.username)
        return ResponseEntity.noContent().build<Unit>()
    }
}
