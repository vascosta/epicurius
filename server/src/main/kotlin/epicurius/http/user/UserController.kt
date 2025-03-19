package epicurius.http.user

import epicurius.services.UserService
import epicurius.domain.AuthenticatedUser
import epicurius.http.user.models.LoginInputModel
import epicurius.http.user.models.SignUpInputModel
import epicurius.http.utils.Uris
import jakarta.servlet.http.HttpServletResponse
import jakarta.validation.Valid
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping(Uris.PREFIX)
class UserController(val userService: UserService) {

    @RequestMapping(Uris.User.SIGNUP)
    fun signUp(
        @Valid @RequestBody body: SignUpInputModel,
        response: HttpServletResponse
    ): ResponseEntity<*> {
        val token = userService.createUser(body.username, body.email, body.country, body.password)
        response.addHeader("Authorization", token)
        return ResponseEntity.ok().build<Unit>()
    }

    @RequestMapping(Uris.User.LOGIN)
    fun login(@Valid @RequestBody body: LoginInputModel, response: HttpServletResponse): ResponseEntity<*> {
        val token = userService.login(body.username, body.email, body.password)
        response.addHeader("Authorization", token)
        return ResponseEntity.ok().build<Unit>()
    }

    @RequestMapping(Uris.User.LOGOUT)
    fun logout(authenticatedUser: AuthenticatedUser, response: HttpServletResponse): ResponseEntity<*> {
        userService.logout(authenticatedUser.user.username)
        response.addHeader("Authorization", "")
        return ResponseEntity.ok().build<Unit>()
    }

    @RequestMapping(Uris.User.FOLLOW)
    fun follow(authenticatedUser: AuthenticatedUser, @PathVariable usernameToFollow: String): ResponseEntity<*> {
        userService.follow(authenticatedUser.user.username, usernameToFollow)
        return ResponseEntity.ok().build<Unit>()
    }
}