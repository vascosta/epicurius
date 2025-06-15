package epicurius.integration.user

import epicurius.domain.exceptions.InvalidCountry
import epicurius.domain.exceptions.UserAlreadyExists
import epicurius.http.media.Problem
import epicurius.http.media.Uris
import epicurius.integration.utils.getBody
import epicurius.integration.utils.post
import epicurius.utils.createTestUser
import epicurius.utils.generateEmail
import epicurius.utils.generateRandomUsername
import epicurius.utils.generateSecurePassword
import org.springframework.http.HttpStatus
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class SignUpIntegrationTests : UserIntegrationTest() {

    private val username = generateRandomUsername()
    private val password = generateSecurePassword()
    private val email = generateEmail(username)

    @Test
    fun `Should create new user successfully with code 201`() {
        // given information to create a user

        // when creating a user
        val cookieHeader = signUp(username, email, "PT", password)

        // then the user is created successfully with code 201
        assertTrue(cookieHeader.isNotEmpty())
    }

    @Test
    fun `Should fail with code 409 when creating an user with an existing username or email`() {
        // given an existing user
        val user = createTestUser(tm)

        // when creating a user with an existing username
        val errorExistingUsername = post<Problem>(
            client,
            api(Uris.User.SIGNUP),
            mapOf(
                "name" to user.user.name,
                "email" to email,
                "password" to password,
                "country" to user.user.country
            ),
            responseStatus = HttpStatus.CONFLICT
        )

        // when creating a user with an existing email
        val errorExistingEmail = post<Problem>(
            client,
            api(Uris.User.SIGNUP),
            mapOf(
                "name" to username,
                "email" to user.user.email,
                "password" to password,
                "country" to user.user.country
            ),
            responseStatus = HttpStatus.CONFLICT
        )

        // when creating a user with an existing username and email
        val errorExistingUsernameAndEmail = post<Problem>(
            client,
            api(Uris.User.SIGNUP),
            mapOf(
                "name" to user.user.name,
                "email" to user.user.email,
                "password" to password,
                "country" to user.user.country
            ),
            responseStatus = HttpStatus.CONFLICT
        )

        // then the user cannot be created and fails with code 409
        val errorExistingUsernameBody = getBody(errorExistingUsername)
        val errorExistingEmailBody = getBody(errorExistingEmail)
        val errorExistingUsernameAndEmailBody = getBody(errorExistingUsernameAndEmail)
        assertEquals(UserAlreadyExists().message, errorExistingUsernameBody.detail)
        assertEquals(UserAlreadyExists().message, errorExistingEmailBody.detail)
        assertEquals(UserAlreadyExists().message, errorExistingUsernameAndEmailBody.detail)
    }

    @Test
    fun `Should fail with code 400 when creating an user with an invalid country`() {
        // given an invalid country
        val invalidCountry = "XX"

        // when creating a user with an invalid country
        val error = post<Problem>(
            client,
            api(Uris.User.SIGNUP),
            mapOf(
                "name" to username,
                "email" to email,
                "password" to password,
                "country" to invalidCountry
            ),
            responseStatus = HttpStatus.BAD_REQUEST
        )

        // then the user cannot be created and fails with code 400
        val errorBody = getBody(error)
        assertEquals(InvalidCountry().message, errorBody.detail)
    }
}
