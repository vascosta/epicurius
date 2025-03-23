package epicurius.http.user

import epicurius.services.UserService
import epicurius.domain.user.AuthenticatedUser
import epicurius.http.user.models.output.DietOutputModel
import epicurius.http.user.models.output.GetUserOutputModel
import epicurius.http.user.models.output.IntolerancesOutputModel
import epicurius.http.user.models.input.LoginInputModel
import epicurius.http.user.models.input.ResetPasswordInputModel
import epicurius.http.user.models.input.SignUpInputModel
import epicurius.http.user.models.input.UpdateUserInputModel
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

    @GetMapping(Uris.User.INTOLERANCES)
    fun getIntolerances(authenticatedUser: AuthenticatedUser): ResponseEntity<*> {
        val intolerances = authenticatedUser.userInfo.intolerances
        return ResponseEntity.ok().body(IntolerancesOutputModel(intolerances))
    }

    @GetMapping(Uris.User.DIET)
    fun getDiet(authenticatedUser: AuthenticatedUser): ResponseEntity<*> {
        val diet = authenticatedUser.userInfo.diet
        return ResponseEntity.ok().body(DietOutputModel(diet))
    }

    @GetMapping(Uris.User.FOLLOWERS)
    fun getFollowers(authenticatedUser: AuthenticatedUser): ResponseEntity<*> {
        val followers = userService.getFollowers(authenticatedUser.userInfo.username)
        return ResponseEntity.ok().body(followers)
    }

    @PostMapping(Uris.User.SIGNUP)
    fun signUp(@Valid @RequestBody body: SignUpInputModel, response: HttpServletResponse): ResponseEntity<*> {
        val token = userService.createUser(body.username, body.email, body.country, body.password)
        response.addHeader("Authorization", "Bearer $token")
        return ResponseEntity.created(URI.create(Uris.User.SIGNUP)).build<Unit>()
    }

    @PostMapping(Uris.User.LOGIN)
    fun login(@Valid @RequestBody body: LoginInputModel, response: HttpServletResponse): ResponseEntity<*> {
        val token = userService.login(body.username, body.email, body.password)
        response.addHeader("Authorization", "Bearer $token")
        return ResponseEntity.ok().build<Unit>()
    }

    @PostMapping(Uris.User.LOGOUT)
    fun logout(authenticatedUser: AuthenticatedUser, response: HttpServletResponse): ResponseEntity<*> {
        userService.logout(authenticatedUser.userInfo.username)
        response.addHeader("Authorization", "")
        return ResponseEntity.ok().build<Unit>()
    }

    @PatchMapping(Uris.User.FOLLOW)
    fun follow(authenticatedUser: AuthenticatedUser, @PathVariable usernameToFollow: String): ResponseEntity<*> {
        val newFollowing = userService.follow(authenticatedUser.userInfo.id, usernameToFollow)

        return if (newFollowing != null) {
            ResponseEntity.ok().body(newFollowing)
        } else {
            ResponseEntity.ok().build<Unit>()
        }
    }

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
        return ResponseEntity.ok().build<Unit>()
    }

    @PatchMapping(Uris.User.USER_PROFILE)
    fun updateProfile(
        authenticatedUser: AuthenticatedUser,
        @Valid @RequestBody body: UpdateUserInputModel
    ): ResponseEntity<*> {
        userService.updateProfile(authenticatedUser.userInfo.username, body)
        return ResponseEntity.ok().build<Unit>()
    }
}