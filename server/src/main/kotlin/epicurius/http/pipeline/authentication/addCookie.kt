package epicurius.http.pipeline.authentication

import jakarta.servlet.http.Cookie
import jakarta.servlet.http.HttpServletResponse
import org.springframework.http.ResponseEntity

fun <T> ResponseEntity<T>.addCookie(response: HttpServletResponse, cookie: Cookie): ResponseEntity<T> {
    response.addCookie(cookie)
    return this
}
