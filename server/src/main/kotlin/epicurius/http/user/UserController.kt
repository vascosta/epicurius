package epicurius.http.user

import epicurius.services.UserService
import epicurius.domain.user.AuthenticatedUser
import epicurius.domain.user.UserProfile
import epicurius.http.user.models.output.GetDietOutputModel
import epicurius.http.user.models.output.GetUserOutputModel
import epicurius.http.user.models.output.GetIntolerancesOutputModel
import epicurius.http.user.models.input.LoginInputModel
import epicurius.http.user.models.input.ResetPasswordInputModel
import epicurius.http.user.models.input.SignUpInputModel
import epicurius.http.user.models.input.UpdateUserInputModel
import epicurius.http.user.models.output.GetFollowRequestsOutputModel
import epicurius.http.user.models.output.GetFollowersOutputModel
import epicurius.http.user.models.output.GetFollowingOutputModel
import epicurius.http.user.models.output.GetUserProfileOutputModel
import epicurius.http.user.models.output.UpdateUserOutputModel
import epicurius.http.utils.Uris
import jakarta.servlet.http.HttpServletResponse
import jakarta.validation.Valid
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*
import java.net.URI

@RestController
@RequestMapping(Uris.PREFIX)
class UserController(val userService: UserService) {

    @GetMapping(Uris.User.USER)
    fun getUser(authenticatedUser: AuthenticatedUser): ResponseEntity<*> {
        return ResponseEntity.ok().body(GetUserOutputModel(authenticatedUser.userInfo))
    }

    @GetMapping(Uris.User.USER_PROFILE)
    fun getUserProfile(
        authenticatedUser: AuthenticatedUser,
        @PathVariable username: String? = null
    ): ResponseEntity<*> {
        return if (username == null) {
            val userProfilePicture = userService.getProfilePicture(authenticatedUser.userInfo.username)
            val userProfile = UserProfile(
                authenticatedUser.userInfo.username,
                authenticatedUser.userInfo.country,
                authenticatedUser.userInfo.privacy,
                userProfilePicture
            )
            ResponseEntity.ok().body(GetUserProfileOutputModel(userProfile))
        } else {
            val userProfile = userService.getUserProfile(username)
            ResponseEntity.ok().body(GetUserProfileOutputModel(userProfile))
        }
    }

    @GetMapping(Uris.User.INTOLERANCES)
    fun getIntolerances(authenticatedUser: AuthenticatedUser): ResponseEntity<*> {
        val intolerances = authenticatedUser.userInfo.intolerances
        return ResponseEntity.ok().body(GetIntolerancesOutputModel(intolerances))
    }

    @GetMapping(Uris.User.DIET)
    fun getDiet(authenticatedUser: AuthenticatedUser): ResponseEntity<*> {
        val diet = authenticatedUser.userInfo.diet
        return ResponseEntity.ok().body(GetDietOutputModel(diet))
    }

    @GetMapping(Uris.User.FOLLOWERS)
    fun getFollowers(authenticatedUser: AuthenticatedUser): ResponseEntity<*> {
        val followers = userService.getFollowers(authenticatedUser.userInfo.id)
        return ResponseEntity.ok().body(GetFollowersOutputModel(followers))
    }

    @GetMapping(Uris.User.FOLLOWING)
    fun getFollowing(authenticatedUser: AuthenticatedUser): ResponseEntity<*> {
        val following = userService.getFollowing(authenticatedUser.userInfo.id)
        return ResponseEntity.ok().body(GetFollowingOutputModel(following))
    }

    @GetMapping(Uris.User.FOLLOW_REQUESTS)
    fun getFollowRequests(authenticatedUser: AuthenticatedUser): ResponseEntity<*> {
        val followRequests = userService.getFollowRequests(authenticatedUser.userInfo.id)
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
        userService.logout(authenticatedUser.userInfo.username)
        response.addHeader("Authorization", "")
        return ResponseEntity.noContent().build<Unit>()
    }

    @PatchMapping(Uris.User.FOLLOW)
    fun follow(authenticatedUser: AuthenticatedUser, @PathVariable usernameToFollow: String): ResponseEntity<*> {
        userService.follow(authenticatedUser.userInfo.id, usernameToFollow)
        return ResponseEntity.noContent().build<Unit>()
    }

    // TODO
    @PatchMapping(Uris.User.UNFOLLOW)
    fun unfollow(authenticatedUser: AuthenticatedUser, @PathVariable usernameToUnfollow: String): ResponseEntity<*> {
        userService.unfollow(authenticatedUser.userInfo.username, usernameToUnfollow)
        return ResponseEntity.ok().build<Unit>()
    }

    @PatchMapping(Uris.User.RESET_PASSWORD)
    fun resetPassword(
        @Valid @RequestBody body: ResetPasswordInputModel
    ): ResponseEntity<*> {
        userService.resetPassword(body.email, body.newPassword, body.confirmPassword)
        return ResponseEntity.noContent().build<Unit>()
    }

    @PatchMapping(Uris.User.USER_PROFILE)
    fun updateProfile(
        authenticatedUser: AuthenticatedUser,
        @Valid @RequestBody body: UpdateUserInputModel
    ): ResponseEntity<*> {
        val updatedUser = userService.updateProfile(authenticatedUser.userInfo.username, body)
        return ResponseEntity.ok().body(
            UpdateUserOutputModel(
                updatedUser.username,
                updatedUser.email,
                updatedUser.country,
                updatedUser.privacy,
                updatedUser.intolerances,
                updatedUser.diet,
                updatedUser.profilePictureName
            )
        )
    }
}