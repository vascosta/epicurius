package epicurius.http

import epicurius.http.utils.Uris
import epicurius.utils.generateEmail
import epicurius.utils.generateRandomUsername
import epicurius.utils.generateSecurePassword
import org.springframework.test.web.reactive.server.expectBody
import java.util.*
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertNotNull
import kotlin.test.assertNull
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
        val token = client.post().uri(api(Uris.User.SIGNUP))
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

        // then the user is created successfully
        assertNotNull(token)

        // when getting the user
        val user = getUser(token)

        // then the user is retrieved successfully
        assertNotNull(user)
        assertEquals(user.user.username, username)
        assertEquals(user.user.email, email)
        assertEquals(user.user.country, country)
        assertTrue(usersDomain.verifyPassword(password, user.user.passwordHash))
        assertEquals(user.user.privacy, false)
        assertEquals(user.user.intolerances, emptyList())
        assertEquals(user.user.diet, emptyList())
        assertNull(user.user.profilePictureName)
    }

    @Test
    fun `Try to create a user with existing name or email and fail`() {
        // given information for a new user and an existing user
        val username = generateRandomUsername()
        val email = generateEmail(username)
        val password = generateSecurePassword()
        val country = "PT"

        val existingUser = publicTestUser

        // when trying to create a user with the same username
        client.post().uri(api(Uris.User.SIGNUP))
            .bodyValue(
                mapOf(
                    "username" to existingUser.username,
                    "email" to email,
                    "password" to password,
                    "confirmPassword" to password,
                    "country" to country
                )
            )
            .exchange()
            .expectStatus().isBadRequest

        // when trying to create a user with the same email
        client.post().uri(api(Uris.User.SIGNUP))
            .bodyValue(
                mapOf(
                    "username" to username,
                    "email" to existingUser.email,
                    "password" to password,
                    "confirmPassword" to password,
                    "country" to country
                )
            )
            .exchange()
            .expectStatus().isBadRequest

        // when trying to create a user with the same username and email
        client.post().uri(api(Uris.User.SIGNUP))
            .bodyValue(
                mapOf(
                    "username" to existingUser.username,
                    "email" to existingUser.email,
                    "password" to password,
                    "confirmPassword" to password,
                    "country" to country
                )
            )
            .exchange()
            .expectStatus().isBadRequest
    }

    @Test
    fun `Try to create a user with invalid country and fail`() {
        // given information for a new user
        val username = generateRandomUsername()
        val email = generateEmail(username)
        val password = generateSecurePassword()
        val country = "XX"

        // when trying to create a user with an invalid country
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
            .expectStatus().isBadRequest
    }

    @Test
    fun `Try to create a user with different passwords and fail`() {
        // given information for a new user
        val username = generateRandomUsername()
        val email = generateEmail(username)
        val password = generateSecurePassword()
        val country = "PT"

        // when trying to create a user with an invalid password
        client.post().uri(api(Uris.User.SIGNUP))
            .bodyValue(
                mapOf(
                    "username" to username,
                    "email" to email,
                    "password" to password,
                    "confirmPassword" to generateSecurePassword(),
                    "country" to country
                )
            )
            .exchange()
            .expectStatus().isBadRequest
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

    @Test
    fun `Reset password successfully`() {
        // given an existing user
        val user = publicTestUser

        // when resetting the password
        val newPassword = generateSecurePassword()
        client.patch().uri(api(Uris.User.RESET_PASSWORD))
            .bodyValue(
                mapOf(
                    "email" to publicTestUser.email,
                    "newPassword" to newPassword,
                    "confirmPassword" to newPassword
                )
            )
            .exchange()
            .expectStatus().isNoContent
            .expectBody<Unit>()
            .returnResult()

        // then the password is reset successfully
        val newToken = login(email = user.email, password = newPassword)
        assertNotNull(newToken)

        // then the user is logged in successfully
        val authenticatedUser = getUser(newToken)
        assertNotNull(authenticatedUser)
        assertEquals(publicTestUser.username, authenticatedUser.user.username)
        assertEquals(publicTestUser.email, authenticatedUser.user.email)
        assertTrue(usersDomain.verifyPassword(newPassword, authenticatedUser.user.passwordHash))
        assertFalse(usersDomain.verifyPassword(publicTestUser.password, authenticatedUser.user.passwordHash))
    }

    @Test
    fun `Try to reset password with different passwords and fail`() {
        // given an existing user
        val user = publicTestUser

        // when trying to reset the password with different passwords
        client.patch().uri(api(Uris.User.RESET_PASSWORD))
            .bodyValue(
                mapOf(
                    "email" to user.email,
                    "newPassword" to generateSecurePassword(),
                    "confirmPassword" to generateSecurePassword()
                )
            )
            .exchange()
            .expectStatus().isBadRequest
    }

    @Test
    fun `Update user profile successfully`() {
        // given information for a new user
        val username = generateRandomUsername()
        val email = generateEmail(username)
        val country = "PT"
        val password = generateSecurePassword()
        val token = createUser(username, email, country, password)
        assertNotNull(token)

        // when updating the user profile
        val newUsername = generateRandomUsername()
        val newEmail = generateEmail(newUsername)
        val newCountry = "ES"
        val newPassword = generateSecurePassword()

        client.patch().uri(api(Uris.User.USER_PROFILE))
            .header("Authorization", "Bearer $token")
            .bodyValue(
                mapOf(
                    "username" to newUsername,
                    "email" to newEmail,
                    "country" to newCountry,
                    "password" to newPassword,
                    "confirmPassword" to newPassword
                )
            )
            .exchange()
            .expectStatus().isOk
            .expectBody<Unit>()
            .returnResult()

        // then the user profile is updated successfully
        val authenticatedUser = getUser(token)
        assertNotNull(authenticatedUser)
        assertEquals(newUsername, authenticatedUser.user.username)
        assertEquals(newEmail, authenticatedUser.user.email)
        assertEquals(newCountry, authenticatedUser.user.country)
        assertTrue(usersDomain.verifyPassword(newPassword, authenticatedUser.user.passwordHash))
    }

    @Test
    fun `Try to update user profile with existing username or email and fail`() {
        // given information for a new user
        val username = generateRandomUsername()
        val email = generateEmail(username)
        val country = "PT"
        val password = generateSecurePassword()
        val token = createUser(username, email, country, password)
        assertNotNull(token)

        // given information for an existing user
        val existingUser = publicTestUser

        // when trying to update the user profile with an existing username
        client.patch().uri(api(Uris.User.USER_PROFILE))
            .header("Authorization", "Bearer $token")
            .bodyValue(
                mapOf(
                    "username" to existingUser.username
                )
            )
            .exchange()
            .expectStatus().isBadRequest

        // when trying to update the user profile with an existing email
        client.patch().uri(api(Uris.User.USER_PROFILE))
            .header("Authorization", "Bearer $token")
            .bodyValue(
                mapOf(
                    "email" to existingUser.email
                )
            )
            .exchange()
            .expectStatus().isBadRequest

        // when trying to update the user profile with an existing username and email
        client.patch().uri(api(Uris.User.USER_PROFILE))
            .header("Authorization", "Bearer $token")
            .bodyValue(
                mapOf(
                    "username" to existingUser.username,
                    "email" to existingUser.email
                )
            )
            .exchange()
            .expectStatus().isBadRequest
    }

    @Test
    fun `Try to update user profile with invalid country and fail`() {
        // given information for a new user
        val username = generateRandomUsername()
        val email = generateEmail(username)
        val country = "PT"
        val password = generateSecurePassword()
        val token = createUser(username, email, country, password)
        assertNotNull(token)

        // when updating the user profile with an invalid country
        client.patch().uri(api(Uris.User.USER_PROFILE))
            .header("Authorization", "Bearer $token")
            .bodyValue(
                mapOf(
                    "country" to "XX"
                )
            )
            .exchange()
            .expectStatus().isBadRequest
    }

    @Test
    fun `Try to update user profile with different passwords and fail`() {
        // given information for a new user
        val username = generateRandomUsername()
        val email = generateEmail(username)
        val country = "PT"
        val password = generateSecurePassword()
        val token = createUser(username, email, country, password)
        assertNotNull(token)

        // when updating the user profile with different passwords
        client.patch().uri(api(Uris.User.USER_PROFILE))
            .header("Authorization", "Bearer $token")
            .bodyValue(
                mapOf(
                    "password" to generateSecurePassword(),
                    "confirmPassword" to generateSecurePassword()
                )
            )
            .exchange()
            .expectStatus().isBadRequest
    }
}