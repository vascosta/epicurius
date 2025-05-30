package epicurius.http.media

import org.springframework.http.ResponseEntity
import java.net.URI

fun okHttpResponse(
    body: Any,
): ResponseEntity<*> =
    ResponseEntity
        .ok()
        .header("Content-Type", APPLICATION_JSON_TYPE)
        .body(body)

fun createdHttpResponse(
    location: URI,
    body: Any,
): ResponseEntity<*> =
    ResponseEntity
        .created(location)
        .header("Content-Type", APPLICATION_JSON_TYPE)
        .body(body)


fun noContentHttpResponse(): ResponseEntity<*> =
    ResponseEntity
        .noContent()
        .build<Any>()