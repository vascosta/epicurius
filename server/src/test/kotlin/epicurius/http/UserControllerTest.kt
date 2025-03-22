package epicurius.http

import epicurius.utils.generateEmail
import epicurius.utils.generateRandomUsername
import epicurius.utils.generateSecurePassword
import org.springframework.test.web.reactive.server.expectBody
import java.util.*
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotNull
import kotlin.test.assertTrue

class UserControllerTest: HttpTest() {

    @Test
    fun `Create new user and retrieve it successfully`() {
        // given user required information
        val username = generateRandomUsername()
        val email = generateEmail(username)
        val country = "PT"
        val password = generateSecurePassword()

        // when creating a user
        client.post().uri(api("/signup"))
            .bodyValue(mapOf("username" to username, "email" to email, "password" to password, "country" to country))
            .exchange()
            .expectStatus().isOk
            .expectBody<String>()
            .returnResult()
            .responseBody
    }

    @Test
    fun `Login a user by name successfully`() {
        // given an existing user logged out
        val username = generateRandomUsername()
        val email = generateEmail(username)
        val country = "PT"
        val password = generateSecurePassword()
        val token = createUser(username, email, country, password)

        assertNotNull(token)
        logout(token)

        // when logging in
        val newToken = login(username = username, password = password)
        assertNotNull(newToken)

        // then the user is logged in successfully
        val authenticatedUser = getUser(newToken)
        assertNotNull(authenticatedUser)
        assertEquals(username, authenticatedUser.user.username)
        assertEquals(email, authenticatedUser.user.email)
        assertEquals(country, authenticatedUser.user.country)
        assertTrue(usersDomain.verifyPassword(password, authenticatedUser.user.passwordHash))
    }

    @Test
    fun `Login a user by email successfully`() {
        // given an existing user logged out
        val username = generateRandomUsername()
        val email = generateEmail(username)
        val country = "PT"
        val password = generateSecurePassword()
        val token = createUser(username, email, country, password)

        assertNotNull(token)
        logout(token)

        // when logging in
        val newToken = login(email = email, password = password)
        assertNotNull(newToken)

        // then the user is logged in successfully
        val authenticatedUser = getUser(newToken)
        assertNotNull(authenticatedUser)
        assertEquals(username, authenticatedUser.user.username)
        assertEquals(email, authenticatedUser.user.email)
        assertEquals(country, authenticatedUser.user.country)
        assertTrue(usersDomain.verifyPassword(password, authenticatedUser.user.passwordHash))
    }
}