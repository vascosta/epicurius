package epicurius.http.user

import epicurius.services.UserService
import epicurius.domain.AuthenticatedUser
import epicurius.http.user.models.IntolerancesInputModel
import epicurius.http.user.models.LoginInputModel
import epicurius.http.user.models.ResetPasswordInputModel
import epicurius.http.user.models.SignUpInputModel
import epicurius.http.utils.Uris
import jakarta.servlet.http.HttpServletResponse
import jakarta.validation.Valid
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping(Uris.PREFIX)
class UserController(val userService: UserService) {

    @PostMapping(Uris.User.SIGNUP)
    fun signUp(
        @Valid @RequestBody body: SignUpInputModel,
        response: HttpServletResponse
    ): ResponseEntity<*> {
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

    @PostMapping(Uris.User.RESET_PASSWORD)
    fun resetPassword(
        authenticatedUser: AuthenticatedUser,
        @Valid @RequestBody body: ResetPasswordInputModel
    ): ResponseEntity<*> {
        userService.resetPassword(authenticatedUser.user.username, body.newPassword, body.confirmPassword)
        return ResponseEntity.ok().build<Unit>()
    }

    @PostMapping(Uris.User.ADD_INTOLERANCES)
    fun addIntolerances(
        authenticatedUser: AuthenticatedUser,
        @Valid @RequestBody body: IntolerancesInputModel
    ): ResponseEntity<*> {
        userService.addIntolerances(authenticatedUser.user.username, body.intolerances)
        return ResponseEntity.ok().build<Unit>()
    }

    @GetMapping(Uris.User.GET_INTOLERANCES)
    fun getIntolerances(authenticatedUser: AuthenticatedUser): ResponseEntity<*> {
        val intolerances = userService.getIntolerances(authenticatedUser.user.username)
        return ResponseEntity.ok().body(intolerances)
    }
}