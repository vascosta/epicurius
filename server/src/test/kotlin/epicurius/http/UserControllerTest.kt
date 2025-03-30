package epicurius.http

import epicurius.domain.Diet
import epicurius.domain.Intolerance
import epicurius.domain.exceptions.UserNotFound
import epicurius.http.user.models.output.UpdateUserOutputModel
import epicurius.http.utils.Problem
import epicurius.http.utils.Uris
import epicurius.http.utils.get
import epicurius.utils.createTestUser
import epicurius.utils.generateEmail
import epicurius.utils.generateRandomUsername
import epicurius.utils.generateSecurePassword
import org.springframework.http.HttpStatus
import org.springframework.test.web.reactive.server.expectBody
import kotlin.test.*

class UserControllerTest : HttpTest() {

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
        assertEquals(user.user.diets, emptyList())
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
            .expectStatus().isBadRequest // then the user is not created

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
            .expectStatus().isBadRequest // then the user is not created

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
            .expectStatus().isBadRequest // then the user is not created
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
            .expectStatus().isBadRequest // then the user is not created
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
            .expectStatus().isBadRequest // then the user is not created
    }

    @Test
    fun `Retrieve its own user profile successfully`() {
        // given an existing logged in user
        val username = generateRandomUsername()
        val token = signUp(
            username = username,
            email = generateEmail(username),
            country = "PT",
            password = generateSecurePassword()
        )

        // when retrieving the user profile
        val body = getUserProfile(token)

        // then the user profile is retrieved successfully
        assertNotNull(body)
        assertEquals(username, body.userProfile.username)
        assertEquals("PT", body.userProfile.country)
        assertFalse(body.userProfile.privacy)
        assertNull(body.userProfile.profilePicture)
        assertTrue(body.userProfile.followers.isEmpty())
        assertTrue(body.userProfile.following.isEmpty())
    }

    @Test
    fun `Retrieve another user profile successfully`() {
        // given an existing logged in user and another user
        val username = generateRandomUsername()
        val token = signUp(
            username = username,
            email = generateEmail(username),
            country = "PT",
            password = generateSecurePassword()
        )
        val user = createTestUser(tm)

        // when retrieving the user profile
        val body = getUserProfile(token, user.username)

        // then the user profile is retrieved successfully
        assertNotNull(body)
        assertEquals(user.username, body.userProfile.username)
        assertEquals(user.country, body.userProfile.country)
        assertEquals(user.privacy, body.userProfile.privacy)
        assertNull(body.userProfile.profilePicture)
        assertTrue(body.userProfile.followers.isEmpty())
        assertTrue(body.userProfile.following.isEmpty())
    }

    @Test
    fun `Try to retrieve a profile from a non-existing user and returns code 404`() {
        // given an existing logged in user
        val username = generateRandomUsername()
        val token = signUp(
            username = username,
            email = generateEmail(username),
            country = "PT",
            password = generateSecurePassword()
        )

        // when trying to retrieve a profile from a non-existing user
        val error = get<Problem>(
            client,
            api(Uris.User.USER_PROFILE) + "?username=nonExistingUser",
            HttpStatus.NOT_FOUND,
            token
        )

        // then the user profile is not retrieved and an error is returned with the UserNotFound message
        assertNotNull(error)
        assertEquals(UserNotFound("nonExistingUser").message, error.detail)
    }

    @Test
    fun `Login a user by name successfully`() {
        // given an existing user logged out
        val username = generateRandomUsername()
        val email = generateEmail(username)
        val country = "PT"
        val password = generateSecurePassword()
        val token = signUp(username, email, country, password)

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
        val token = signUp(username, email, country, password)

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
        client.patch().uri(api(Uris.User.USER_RESET_PASSWORD))
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
        assertNotEquals(publicTestUser.passwordHash, authenticatedUser.user.passwordHash)
    }

    @Test
    fun `Try to reset password with different passwords and fail`() {
        // given an existing user
        val user = publicTestUser

        // when trying to reset the password with different passwords
        client.patch().uri(api(Uris.User.USER_RESET_PASSWORD))
            .bodyValue(
                mapOf(
                    "email" to user.email,
                    "newPassword" to generateSecurePassword(),
                    "confirmPassword" to generateSecurePassword()
                )
            )
            .exchange()
            .expectStatus().isBadRequest // then the password is not reset
    }

    @Test
    fun `Update user successfully`() {
        // given information for a new user
        val username = generateRandomUsername()
        val email = generateEmail(username)
        val country = "PT"
        val password = generateSecurePassword()
        val token = signUp(username, email, country, password)
        assertNotNull(token)

        // when updating the user
        val newUsername = generateRandomUsername()
        val newEmail = generateEmail(newUsername)
        val newCountry = "ES"
        val newPassword = generateSecurePassword()
        val newIntolerances = listOf(Intolerance.SOY)
        val newDiets = listOf(Diet.WHOLE30)

        val user = client.patch().uri(api(Uris.User.USER))
            .header("Authorization", "Bearer $token")
            .bodyValue(
                mapOf(
                    "username" to newUsername,
                    "email" to newEmail,
                    "country" to newCountry,
                    "password" to newPassword,
                    "confirmPassword" to newPassword,
                    "privacy" to true,
                    "intolerances" to newIntolerances,
                    "diet" to newDiets
                )
            )
            .exchange()
            .expectStatus().isOk
            .expectBody<UpdateUserOutputModel>()
            .returnResult()
            .responseBody

        // then the user is updated successfully
        assertNotNull(user)
        assertEquals(newUsername, user.username)
        assertEquals(newEmail, user.email)
        assertEquals(newCountry, user.country)
        assertTrue(user.privacy)
        assertEquals(newIntolerances, user.intolerances)
        assertEquals(newDiets, user.diet)

        // when logging out
        logout(token)

        // when logging in with the new username and password
        val newToken = login(username = newUsername, password = newPassword)
        assertNotNull(newToken)

        // then the user is logged in successfully with the new password
        val authenticatedUser = getUser(newToken)
        assertNotNull(authenticatedUser)
        assertEquals(newUsername, authenticatedUser.user.username)
        assertTrue(usersDomain.verifyPassword(newPassword, authenticatedUser.user.passwordHash))
    }

    @Test
    fun `Try to update user with existing username or email and fail`() {
        // given information for a new user
        val username = generateRandomUsername()
        val email = generateEmail(username)
        val country = "PT"
        val password = generateSecurePassword()
        val token = signUp(username, email, country, password)
        assertNotNull(token)

        // given information for an existing user
        val existingUser = publicTestUser

        // when trying to update the user with an existing username
        client.patch().uri(api(Uris.User.USER))
            .header("Authorization", "Bearer $token")
            .bodyValue(
                mapOf(
                    "username" to existingUser.username
                )
            )
            .exchange()
            .expectStatus().isBadRequest // then the user is not updated

        // when trying to update the user with an existing email
        client.patch().uri(api(Uris.User.USER))
            .header("Authorization", "Bearer $token")
            .bodyValue(
                mapOf(
                    "email" to existingUser.email
                )
            )
            .exchange()
            .expectStatus().isBadRequest // then the user is not updated

        // when trying to update the user with an existing username and email
        client.patch().uri(api(Uris.User.USER))
            .header("Authorization", "Bearer $token")
            .bodyValue(
                mapOf(
                    "username" to existingUser.username,
                    "email" to existingUser.email
                )
            )
            .exchange()
            .expectStatus().isBadRequest // then the user is not updated
    }

    @Test
    fun `Try to update user with invalid country and fail`() {
        // given information for a new user
        val username = generateRandomUsername()
        val email = generateEmail(username)
        val country = "PT"
        val password = generateSecurePassword()
        val token = signUp(username, email, country, password)
        assertNotNull(token)

        // when updating the user with an invalid country
        client.patch().uri(api(Uris.User.USER))
            .header("Authorization", "Bearer $token")
            .bodyValue(
                mapOf(
                    "country" to "XX"
                )
            )
            .exchange()
            .expectStatus().isBadRequest // then the user is not updated
    }

    @Test
    fun `Try to update user with different passwords and fail`() {
        // given information for a new user
        val username = generateRandomUsername()
        val email = generateEmail(username)
        val country = "PT"
        val password = generateSecurePassword()
        val token = signUp(username, email, country, password)
        assertNotNull(token)

        // when updating the user with different passwords
        client.patch().uri(api(Uris.User.USER))
            .header("Authorization", "Bearer $token")
            .bodyValue(
                mapOf(
                    "password" to generateSecurePassword(),
                    "confirmPassword" to generateSecurePassword()
                )
            )
            .exchange()
            .expectStatus().isBadRequest // then the user is not updated
    }
}
