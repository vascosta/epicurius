package epicurius.http

import epicurius.EpicuriusTest
import epicurius.domain.user.User
import epicurius.http.user.models.output.GetUserOutputModel
import epicurius.http.utils.Uris
import epicurius.utils.createTestUser
import org.junit.jupiter.api.BeforeAll
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.test.web.server.LocalServerPort
import org.springframework.test.web.reactive.server.WebTestClient
import org.springframework.test.web.reactive.server.expectBody

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class HttpTest : EpicuriusTest() {
    @LocalServerPort
    var port: Int = 0
    val client = WebTestClient.bindToServer().baseUrl(api("/")).build()
    final fun api(path: String): String = "http://localhost:$port/api$path"

    fun createUser(username: String, email: String, country: String, password: String) =
        client.post().uri(api(Uris.User.SIGNUP))
            .bodyValue(
                mapOf(
                    "username" to username,
                    "email" to email,
                    "password" to password,
                    "confirmPassword" to password,
                    "country" to country
                )
            )
            .exchange()
            .expectStatus().isCreated
            .expectBody<String>()
            .returnResult()
            .responseHeaders["Authorization"]?.first()?.substringAfter("Bearer ")

    fun getUser(token: String) =
        client.get().uri(api(Uris.User.USER))
            .header("Authorization", "Bearer $token")
            .exchange()
            .expectStatus().isOk
            .expectBody<GetUserOutputModel>()
            .returnResult()
            .responseBody

    fun login(username: String? = null, email: String? = null, password: String) =
        client.post().uri(api(Uris.User.LOGIN))
            .bodyValue(mapOf("username" to username, "email" to email, "password" to password))
            .exchange()
            .expectStatus().isNoContent
            .expectBody<Unit>()
            .returnResult()
            .responseHeaders["Authorization"]?.first()?.substringAfter("Bearer ")

    fun logout(token: String) =
        client.post().uri(api(Uris.User.LOGOUT))
            .header("Authorization", "Bearer $token")
            .exchange()
            .expectStatus().isNoContent
            .expectBody<Unit>()
            .returnResult()
            .responseHeaders["Authorization"]?.first()

    companion object {

        lateinit var publicTestUser: User
        lateinit var privateTestUser: User

        @JvmStatic
        @BeforeAll
        fun setupDB() {
            publicTestUser = createTestUser(tm)
            privateTestUser = createTestUser(tm)
        }
    }
}
