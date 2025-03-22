package epicurius.http.user

import epicurius.services.UserService
import epicurius.domain.user.AuthenticatedUser
import epicurius.http.user.models.DietOutputModel
import epicurius.http.user.models.GetUserOutputModel
import epicurius.http.user.models.IntolerancesOutputModel
import epicurius.http.user.models.LoginInputModel
import epicurius.http.user.models.ResetPasswordInputModel
import epicurius.http.user.models.SignUpInputModel
import epicurius.http.user.models.UpdateUserInputModel
import epicurius.http.utils.Uris
import jakarta.servlet.http.HttpServletResponse
import jakarta.validation.Valid
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping(Uris.PREFIX)
class UserController(val userService: UserService) {

    @GetMapping(Uris.User.USER)
    fun getUser(authenticatedUser: AuthenticatedUser): ResponseEntity<*> {
        return ResponseEntity.ok().body(GetUserOutputModel(authenticatedUser.user))
    }

    @GetMapping(Uris.User.INTOLERANCES)
    fun getIntolerances(authenticatedUser: AuthenticatedUser): ResponseEntity<*> {
        val intolerances = authenticatedUser.user.intolerances
        return ResponseEntity.ok().body(IntolerancesOutputModel(intolerances))
    }

    @GetMapping(Uris.User.DIET)
    fun getDiet(authenticatedUser: AuthenticatedUser): ResponseEntity<*> {
        val diet = authenticatedUser.user.diet
        return ResponseEntity.ok().body(DietOutputModel(diet))
    }

    @PostMapping(Uris.User.SIGNUP)
    fun signUp(@Valid @RequestBody body: SignUpInputModel, response: HttpServletResponse): ResponseEntity<*> {
        val token = userService.createUser(body.username, body.email, body.country, body.password)
        response.addHeader("Authorization", token)
        return ResponseEntity.ok().build<Unit>()
    }

    @PostMapping(Uris.User.LOGIN)
    fun login(@Valid @RequestBody body: LoginInputModel, response: HttpServletResponse): ResponseEntity<*> {
        val token = userService.login(body.username, body.email, body.password)
        response.addHeader("Authorization", token)
        return ResponseEntity.ok().build<Unit>()
    }

    @PostMapping(Uris.User.LOGOUT)
    fun logout(authenticatedUser: AuthenticatedUser, response: HttpServletResponse): ResponseEntity<*> {
        userService.logout(authenticatedUser.user.username)
        response.addHeader("Authorization", "")
        return ResponseEntity.ok().build<Unit>()
    }

    @PostMapping(Uris.User.FOLLOWERS)
    fun getFollowers(authenticatedUser: AuthenticatedUser): ResponseEntity<*> {
        val followers = userService.getFollowers(authenticatedUser.user.username)
        return ResponseEntity.ok().body(followers)
    }

    @PostMapping(Uris.User.FOLLOW)
    fun follow(authenticatedUser: AuthenticatedUser, @PathVariable usernameToFollow: String): ResponseEntity<*> {
        userService.follow(authenticatedUser.user.username, usernameToFollow)
        return ResponseEntity.ok().build<Unit>()
    }

    @PostMapping(Uris.User.UNFOLLOW)
    fun unfollow(authenticatedUser: AuthenticatedUser, @PathVariable usernameToUnfollow: String): ResponseEntity<*> {
        userService.unfollow(authenticatedUser.user.username, usernameToUnfollow)
        return ResponseEntity.ok().build<Unit>()
    }

    @PutMapping(Uris.User.RESET_PASSWORD)
    fun resetPassword(
        @Valid @RequestBody body: ResetPasswordInputModel
    ): ResponseEntity<*> {
        userService.resetPassword(body.email, body.newPassword, body.confirmPassword)
        return ResponseEntity.ok().build<Unit>()
    }

    @PutMapping(Uris.User.USER_PROFILE)
    fun updateProfile(
        authenticatedUser: AuthenticatedUser,
        @Valid @RequestBody body: UpdateUserInputModel
    ): ResponseEntity<*> {
        userService.updateProfile(authenticatedUser.user.username, body)
        return ResponseEntity.ok().build<Unit>()
    }
}