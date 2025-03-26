package epicurius.http.utils

import org.springframework.http.HttpHeaders
import org.springframework.http.HttpStatusCode
import org.springframework.http.ResponseEntity
import java.net.URI

data class Problem(
    val type: URI,
    val title: String,
    val detail: String? = null,
    val instance: URI? = null
) {
    fun toResponse(status: HttpStatusCode, headers: HttpHeaders? = null) =
        ResponseEntity
            .status(status)
            .header("Content-Type", PROBLEM_TYPE)
            .headers(headers)
            .body<Problem>(this)
}
