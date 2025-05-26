package epicurius.http.pipeline.authentication.cookie

import jakarta.servlet.http.Cookie
import jakarta.servlet.http.HttpServletResponse
import org.springframework.http.ResponseEntity

const val TOKEN = "token"

fun <T> ResponseEntity<T>.addCookie(response: HttpServletResponse, value: String): ResponseEntity<T> {
    response.addCookie(
        Cookie(TOKEN, value).apply {
            isHttpOnly = true
            secure = true
        }
    )
    return this
}

fun <T> ResponseEntity<T>.removeCookie(response: HttpServletResponse): ResponseEntity<T> {
    response.addCookie(
        Cookie(TOKEN, "")
            .apply {
                isHttpOnly = true
                secure = true
                maxAge = 0
            }
    )
    return this
}
