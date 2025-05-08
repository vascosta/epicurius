package epicurius.http.pipeline.authentication

import jakarta.servlet.http.Cookie
import org.springframework.stereotype.Component

@Component
class AuthenticationRefreshHandler(private val requestTokenProcessor: RequestTokenProcessor) {

    fun refreshToken(oldToken: String): Cookie {
        val newToken = requestTokenProcessor.refreshToken(oldToken)
        return Cookie(TOKEN, newToken)
    }

    companion object {
        const val TOKEN = "token"
    }
}