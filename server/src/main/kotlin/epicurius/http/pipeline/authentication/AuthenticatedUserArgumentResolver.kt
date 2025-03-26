package epicurius.http.pipeline.authentication

import epicurius.domain.user.AuthenticatedUser
import jakarta.servlet.http.HttpServletRequest
import org.springframework.core.MethodParameter
import org.springframework.stereotype.Component
import org.springframework.web.bind.support.WebDataBinderFactory
import org.springframework.web.context.request.NativeWebRequest
import org.springframework.web.method.support.HandlerMethodArgumentResolver
import org.springframework.web.method.support.ModelAndViewContainer

@Component
class AuthenticatedUserArgumentResolver : HandlerMethodArgumentResolver {

    override fun supportsParameter(parameter: MethodParameter) =
        parameter.parameterType == AuthenticatedUser::class.java

    override fun resolveArgument(
        parameter: MethodParameter,
        mavContainer: ModelAndViewContainer?,
        webRequest: NativeWebRequest,
        binderFactory: WebDataBinderFactory?
    ): AuthenticatedUser {
        val request = webRequest.getNativeRequest(HttpServletRequest::class.java)
            ?: throw IllegalStateException("HttpServletRequest not found")
        return getSession(request) ?: throw IllegalStateException("User not authenticated")
    }

    companion object {
        private const val KEY = "AuthenticatedUserArgumentResolver"

        fun addSession(authenticatedUser: AuthenticatedUser, request: HttpServletRequest) = request.setAttribute(KEY, authenticatedUser)
        fun getSession(request: HttpServletRequest): AuthenticatedUser? = request.getAttribute(KEY)?.let { it as? AuthenticatedUser }
    }
}
