package epicurius.http.pipeline.authentication

import epicurius.domain.AuthenticatedUser
import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import org.springframework.stereotype.Component
import org.springframework.web.method.HandlerMethod
import org.springframework.web.servlet.HandlerInterceptor
import epicurius.domain.exceptions.UserException.UnauthorizedException

@Component
class AuthenticationInterceptor(
    private val requestTokenProcessor: RequestTokenProcessor
) : HandlerInterceptor {

    override fun preHandle(request: HttpServletRequest, response: HttpServletResponse, handler: Any): Boolean {
        if (handler is HandlerMethod && handler.hasParameterType<AuthenticatedUser>()) {
            val token = getToken(request) ?: throw UnauthorizedException("Missing user token")
            val authenticatedUser = requestTokenProcessor.getAuthenticatedUser(token)
            return if (authenticatedUser == null) {
                throw UnauthorizedException("Invalid user token")
            } else {
                AuthenticatedUserArgumentResolver.addSession(authenticatedUser, request)
                true
            }
        }
        return true
    }

    fun getToken(request: HttpServletRequest): String? {
        val header = request.getHeader(AUTHORIZATION_HEADER)
        return requestTokenProcessor.parseAuthorizationHeader(header)
    }

    private inline fun <reified T : Any> HandlerMethod.hasParameterType() =
    methodParameters.any { it.parameterType == T::class.java }

    companion object {
        const val AUTHORIZATION_HEADER = "Authorization"
    }
}