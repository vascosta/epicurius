package epicurius.http

import epicurius.http.user.models.GetUserOutputModel
import epicurius.EpicuriusTest
import epicurius.utils.UserTest
import epicurius.utils.createTestUser
import org.junit.jupiter.api.BeforeAll
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.test.web.server.LocalServerPort
import org.springframework.test.web.reactive.server.WebTestClient
import org.springframework.test.web.reactive.server.expectBody

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class HttpTest: EpicuriusTest() {
    @LocalServerPort
    var port: Int = 0
    val client = WebTestClient.bindToServer().baseUrl(api("/")).build()
    final fun api(path: String): String = "http://localhost:$port/api$path"

    fun createUser(username: String, email: String, country: String, passwordHash: String) =
        client.post().uri(api("/signup"))
            .bodyValue(mapOf("username" to username, "email" to email, "country" to country, "password" to passwordHash))
            .exchange()
            .expectStatus().isCreated
            .expectBody<String>()
            .returnResult()
            .responseHeaders["Authorization"]?.first()

    fun getUser() =
        client.get().uri(api("/user"))
            .exchange()
            .expectStatus().isOk
            .expectBody<GetUserOutputModel>()
            .returnResult()
            .responseBody
    companion object {

        lateinit var publicTestUser: UserTest
        lateinit var privateTestUser: UserTest

        @JvmStatic
        @BeforeAll
        fun setupDB() {
            publicTestUser = createTestUser(tm, fs, false)
            privateTestUser = createTestUser(tm, fs, true)
        }

    }
}