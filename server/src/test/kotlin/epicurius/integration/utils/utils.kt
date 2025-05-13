package epicurius.integration.utils

import org.springframework.http.HttpStatus
import org.springframework.http.MediaType
import org.springframework.http.MediaType.APPLICATION_JSON
import org.springframework.test.web.reactive.server.EntityExchangeResult
import org.springframework.test.web.reactive.server.WebTestClient
import org.springframework.web.reactive.function.BodyInserters

inline fun <reified T> get(
    client: WebTestClient,
    uri: String,
    responseStatus: HttpStatus = HttpStatus.OK,
    token: String
) =
    client.get().uri(uri)
        .header("Authorization", "Bearer $token")
        .exchange()
        .expectStatus().isEqualTo(responseStatus)
        .expectBody(T::class.java)
        .returnResult()
        .responseBody

inline fun <reified T> post(
    client: WebTestClient,
    uri: String,
    body: Any,
    responseStatus: HttpStatus = HttpStatus.NO_CONTENT,
    token: String? = null
) =
    client.post().uri(uri)
        .header("Authorization", "Bearer $token")
        .bodyValue(body)
        .exchange()
        .expectStatus().isEqualTo(responseStatus)
        .expectBody(T::class.java)
        .returnResult()

inline fun <reified T> patch(
    client: WebTestClient,
    uri: String,
    contentType: MediaType = APPLICATION_JSON,
    body: Any,
    responseStatus: HttpStatus = HttpStatus.NO_CONTENT,
    token: String? = null
) =
    client.patch().uri(uri)
        .header("Authorization", "Bearer $token")
        .contentType(contentType)
        .bodyValue(body)
        .exchange()
        .expectStatus().isEqualTo(responseStatus)
        .expectBody(T::class.java)
        .returnResult()

inline fun <reified T> patchMultiPart(
    client: WebTestClient,
    uri: String,
    body: BodyInserters.MultipartInserter,
    responseStatus: HttpStatus = HttpStatus.NO_CONTENT,
    token: String? = null
) =
    client.patch().uri(uri)
        .header("Authorization", "Bearer $token")
        .contentType(MediaType.MULTIPART_FORM_DATA)
        .body(body)
        .exchange()
        .expectStatus().isEqualTo(responseStatus)
        .expectBody(T::class.java)
        .returnResult()

inline fun <reified T> delete(
    client: WebTestClient,
    uri: String,
    responseStatus: HttpStatus = HttpStatus.OK,
    token: String? = null
) =
    client.delete().uri(uri)
        .header("Authorization", "Bearer $token")
        .exchange()
        .expectStatus().isEqualTo(responseStatus)
        .expectBody(T::class.java)
        .returnResult()

inline fun <reified T> getBody(result: EntityExchangeResult<T>): T = result.responseBody
    ?: throw IllegalStateException("Response body is null")

inline fun <reified T> getAuthorizationHeader(result: EntityExchangeResult<T>): String =
    result.responseHeaders["Set-Cookie"]?.first()?.substringAfter("token=")
        ?: throw IllegalStateException("Set cookie is null")
