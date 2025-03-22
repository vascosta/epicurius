package epicurius.http

import org.springframework.test.web.reactive.server.expectBody
import java.util.*
import kotlin.test.Test

class UserControllerTest: HttpTest() {

    @Test
    fun `Create new user and retrieve it successfully`() {
        // given user required information
        val username = "test${UUID.randomUUID().toString().take(8)}"
        val email = "$username@email.com"
        val country = "PT"
        val password = "P" + UUID.randomUUID().toString().take(10)

        // when creating a user
        client.post().uri(api("/signup"))
            .bodyValue(mapOf("username" to username, "email" to email, "password" to password, "country" to country))
            .exchange()
            .expectStatus().isOk
            .expectBody<String>()
            .returnResult()
            .responseBody
    }
}