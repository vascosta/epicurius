package epicurius.http.pipeline.authentication

import epicurius.domain.user.AuthenticatedUser
import epicurius.services.user.UserService
import org.springframework.stereotype.Component

@Component
class RequestTokenProcessor(val userService: UserService) {

    fun getAuthenticatedUser(token: String): AuthenticatedUser? = userService.getAuthenticatedUser(token)

    fun refreshToken(oldToken: String) = userService.refreshToken(oldToken)

    fun parseAuthorizationHeader(value: String?): String? {
        if (value.isNullOrBlank()) return null
        return value.trim().split(" ").let {
            if (it.size != 2 || it[0].lowercase() != SCHEME) null else it[1]
        }
    }

    companion object {
        const val SCHEME = "bearer"
    }
}
