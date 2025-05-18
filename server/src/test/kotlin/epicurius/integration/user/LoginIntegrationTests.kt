package epicurius.integration.user

import epicurius.domain.exceptions.IncorrectPassword
import epicurius.domain.exceptions.UserAlreadyLoggedIn
import epicurius.domain.exceptions.UserNotFound
import epicurius.http.utils.Problem
import epicurius.http.utils.Uris
import epicurius.integration.utils.getBody
import epicurius.integration.utils.post
import epicurius.utils.generateEmail
import epicurius.utils.generateRandomUsername
import epicurius.utils.generateSecurePassword
import org.springframework.http.HttpStatus
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class LoginIntegrationTests: UserIntegrationTest() {

    private val country = "PT"
    private val password = generateSecurePassword()
    private val passwordHash = userDomain.encodePassword(password)

    @Test
    fun `Should login a user by name successfully`() {
        // given a user
        val username = generateRandomUsername()
        val email = generateEmail(username)
        tm.run { it.userRepository.createUser(username, email, country, passwordHash) }

        // when logging in by name
        val cookieHeader = login(username, password = password)

        // then the user is logged in successfully
        assertTrue(cookieHeader.isNotEmpty())
    }

    @Test
    fun `Should login a user by email successfully`() {
        // given a user
        val username = generateRandomUsername()
        val email = generateEmail(username)
        tm.run { it.userRepository.createUser(username, email, country, passwordHash) }

        // when logging in by email
        val cookieHeader = login(email = email, password = password)

        // then the user is logged in successfully
        assertTrue(cookieHeader.isNotEmpty())
    }

    @Test
    fun `Should fail with code 404 when login with an non existing user `() {
        // given a non-existing username and email
        val nonExistingUsername = "123abc"
        val nonExistingEmail = "123@gmail.com"

        // when logging in
        val nonExistingUsernameError = post<Problem>(
            client,
            api(Uris.User.LOGIN),
            mapOf("name" to nonExistingUsername, "password" to password),
            HttpStatus.NOT_FOUND
        )

        val nonExistingEmailResponse = post<Problem>(
            client,
            api(Uris.User.LOGIN),
            mapOf("email" to nonExistingEmail, "password" to password),
            HttpStatus.NOT_FOUND
        )

        // then the user cannot be logged in and fails with code 404
        val nonExistingUsernameErrorBody = getBody(nonExistingUsernameError)
        val nonExistingEmailErrorBody = getBody(nonExistingEmailResponse)
        assertEquals(UserNotFound(nonExistingUsername).message, nonExistingUsernameErrorBody.detail)
        assertEquals(UserNotFound(nonExistingEmail).message, nonExistingEmailErrorBody.detail)
    }

    @Test
    fun `Should fail with code 409 when login with an already logged in user`() {
        // given a logged-in user
        val username = generateRandomUsername()
        val email = generateEmail(username)
        tm.run { it.userRepository.createUser(username, email, country, passwordHash) }
        login(username, password = password)

        // when logging in again
        val usernameAlreadyLoggedInError = post<Problem>(
            client,
            api(Uris.User.LOGIN),
            mapOf("name" to username, "password" to password),
            HttpStatus.CONFLICT
        )

        val emailAlreadyLoggedInResponse = post<Problem>(
            client,
            api(Uris.User.LOGIN),
            mapOf("email" to email, "password" to password),
            HttpStatus.CONFLICT
        )

        // then the user cannot be logged in and fails with code 404
        val usernameAlreadyLoggedInErrorBody = getBody(usernameAlreadyLoggedInError)
        val emailAlreadyLoggedInErrorBody = getBody(emailAlreadyLoggedInResponse)
        assertEquals(UserAlreadyLoggedIn().message, usernameAlreadyLoggedInErrorBody.detail)
        assertEquals(UserAlreadyLoggedIn().message, emailAlreadyLoggedInErrorBody.detail)
    }

    @Test
    fun `Should fail with code 401 IncorrectPassword exception when login with an incorrect password`() {
        // given a user and an incorrect password
        val username = generateRandomUsername()
        val email = generateEmail(username)
        tm.run { it.userRepository.createUser(username, email, country, passwordHash) }
        val incorrectPassword = generateSecurePassword()


        // when logging in with an incorrect password
        val incorrectPasswordError = post<Problem>(
            client,
            api(Uris.User.LOGIN),
            mapOf("name" to username, "password" to incorrectPassword),
            HttpStatus.BAD_REQUEST
        )

        // then the user cannot be logged in and fails with code 401
        val incorrectPasswordErrorBody = getBody(incorrectPasswordError)
        assertEquals(IncorrectPassword().message, incorrectPasswordErrorBody.detail)
    }
}