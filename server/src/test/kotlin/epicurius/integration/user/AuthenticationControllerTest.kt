package epicurius.integration.user

import epicurius.domain.exceptions.IncorrectPassword
import epicurius.domain.exceptions.InvalidCountry
import epicurius.domain.exceptions.PasswordsDoNotMatch
import epicurius.domain.exceptions.UnauthorizedException
import epicurius.domain.exceptions.UserAlreadyExists
import epicurius.domain.exceptions.UserAlreadyLoggedIn
import epicurius.domain.exceptions.UserNotFound
import epicurius.domain.user.User
import epicurius.domain.user.UserDomain
import epicurius.http.utils.Problem
import epicurius.http.utils.Regex.VALID_PASSWORD_MSG
import epicurius.http.utils.Regex.VALID_STRING_MSG
import epicurius.http.utils.Uris
import epicurius.integration.EpicuriusIntegrationTest
import epicurius.integration.utils.getBody
import epicurius.integration.utils.patch
import epicurius.integration.utils.post
import epicurius.utils.createTestUser
import epicurius.utils.generateEmail
import epicurius.utils.generateRandomUsername
import epicurius.utils.generateSecurePassword
import org.springframework.http.HttpStatus
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertNotNull
import kotlin.test.assertNull
import kotlin.test.assertTrue

class AuthenticationControllerTest : EpicuriusIntegrationTest() {

    val publicTestUser: User = createTestUser(tm)

    @Test
    fun `Unauthenticated user tries to do an authenticated operation and fails with code 401`() {
        // given a non-authenticated user
        val username = generateRandomUsername()

        // when trying to do an authenticated operation, e.g. logout
        val error = post<Problem>(
            client,
            api(Uris.User.LOGOUT),
            mapOf("username" to username, "password" to generateSecurePassword()),
            HttpStatus.UNAUTHORIZED,
            ""
        )
        assertNotNull(error)

        // then the user couldn't do the operation and an error is returned
        val errorBody = getBody(error)
        assertNotNull(errorBody)
        assertEquals(UnauthorizedException("Missing user token").message, errorBody.detail)
    }

    @Test
    fun `Signup a new user and retrieve it successfully with code 200`() {
        // given user required information
        val username = generateRandomUsername()
        val email = generateEmail(username)
        val country = "PT"
        val password = generateSecurePassword()

        // when creating a user
        val token = signUp(username, email, country, password)

        // when getting the user
        val userBody = getUser(token)

        // then the user is retrieved successfully
        assertNotNull(userBody)
        assertEquals(username, userBody.user.name)
        assertEquals(email, userBody.user.email)
        assertEquals(country, userBody.user.country)
        assertFalse(userBody.user.privacy)
        assertEquals(emptyList(), userBody.user.intolerances)
        assertEquals(emptyList(), userBody.user.diets)
        assertNull(userBody.user.profilePictureName)
    }

    @Test
    fun `Try to signup with invalid username too long or too small`() {
        // given information for a new user
        val usernameToShort = "ab"
        val usernameToLong = "wPIETGFH29THshfgOPHohasfn21h"
        val invalidUsernameString = "/-+==;:"
        val email = generateEmail(usernameToShort)
        val password = generateSecurePassword()
        val country = "PT"

        // when trying to create a user with an invalid username
        val errorWithShortUsername = post<Problem>(
            client,
            api(Uris.User.SIGNUP),
            mapOf(
                "username" to usernameToShort,
                "email" to email,
                "password" to password,
                "confirmPassword" to password,
                "country" to country
            ),
            HttpStatus.BAD_REQUEST
        )

        val errorWithLongUsername = post<Problem>(
            client,
            api(Uris.User.SIGNUP),
            mapOf(
                "username" to usernameToLong,
                "email" to email,
                "password" to password,
                "confirmPassword" to password,
                "country" to country
            ),
            HttpStatus.BAD_REQUEST
        )

        val errorWithInvalidUsernameString = post<Problem>(
            client,
            api(Uris.User.SIGNUP),
            mapOf(
                "username" to invalidUsernameString,
                "email" to email,
                "password" to password,
                "confirmPassword" to password,
                "country" to country
            ),
            HttpStatus.BAD_REQUEST
        )

        // then the user is not created
        val errorWithShortUsernameBody = getBody(errorWithShortUsername)
        val errorWithLongUsernameBody = getBody(errorWithLongUsername)
        val errorWithInvalidUsernameStringBody = getBody(errorWithInvalidUsernameString)
        assertNotNull(errorWithShortUsernameBody)
        assertNotNull(errorWithLongUsernameBody)
        assertNotNull(errorWithInvalidUsernameStringBody)
        assertEquals("Username " + UserDomain.USERNAME_LENGTH_MSG, errorWithShortUsernameBody.detail)
        assertEquals("Username " + UserDomain.USERNAME_LENGTH_MSG, errorWithLongUsernameBody.detail)
        assertEquals("Username $VALID_STRING_MSG", errorWithInvalidUsernameStringBody.detail)
    }

    @Test
    fun `Try to signup with invalid email`() {
        // given information for a new user
        val username = generateRandomUsername()
        val invalidEmail = "invalidEmail"
        val password = generateSecurePassword()
        val country = "PT"

        // when trying to create a user with an invalid email
        val errorWithInvalidEmail = post<Problem>(
            client,
            api(Uris.User.SIGNUP),
            mapOf(
                "username" to username,
                "email" to invalidEmail,
                "password" to password,
                "confirmPassword" to password,
                "country" to country
            ),
            HttpStatus.BAD_REQUEST
        )

        // then the user is not created
        val errorWithInvalidEmailBody = getBody(errorWithInvalidEmail)
        assertNotNull(errorWithInvalidEmailBody)
        assertEquals("Email " + UserDomain.VALID_EMAIL_MSG, errorWithInvalidEmailBody.detail)
    }

    @Test
    fun `Try to signup a user with existing name or email and fails with code 400`() {
        // given information for a new user and an existing user
        val username = generateRandomUsername()
        val email = generateEmail(username)
        val password = generateSecurePassword()
        val country = "PT"

        val existingUser = publicTestUser

        // when trying to create a user with the same username
        val usernameError = post<Problem>(
            client,
            api(Uris.User.SIGNUP),
            mapOf(
                "username" to existingUser.name,
                "email" to email,
                "password" to password,
                "confirmPassword" to password,
                "country" to country
            ),
            HttpStatus.BAD_REQUEST
        )
        assertNotNull(usernameError)

        // then the user is not created
        val usernameErrorBody = getBody(usernameError)
        assertNotNull(usernameErrorBody)
        assertEquals(UserAlreadyExists().message, usernameErrorBody.detail)

        // when trying to create a user with the same email
        val emailError = post<Problem>(
            client,
            api(Uris.User.SIGNUP),
            mapOf(
                "username" to username,
                "email" to existingUser.email,
                "password" to password,
                "confirmPassword" to password,
                "country" to country
            ),
            HttpStatus.BAD_REQUEST
        )
        assertNotNull(emailError)

        // then the user is not created
        val emailErrorBody = getBody(emailError)
        assertNotNull(emailErrorBody)
        assertEquals(UserAlreadyExists().message, emailErrorBody.detail)

        // when trying to create a user with the same username and email
        val error = post<Problem>(
            client,
            api(Uris.User.SIGNUP),
            mapOf(
                "username" to existingUser.name,
                "email" to existingUser.email,
                "password" to password,
                "confirmPassword" to password,
                "country" to country
            ),
            HttpStatus.BAD_REQUEST
        )
        assertNotNull(error)

        // then the user is not created
        val errorBody = getBody(error)
        assertNotNull(errorBody)
        assertEquals(UserAlreadyExists().message, errorBody.detail)
    }

    @Test
    fun `Try to signup a user with invalid country and fails with code 400`() {
        // given information for a new user
        val username = generateRandomUsername()
        val email = generateEmail(username)
        val password = generateSecurePassword()

        // when trying to create a user with an invalid country
        val error = post<Problem>(
            client,
            api(Uris.User.SIGNUP),
            mapOf(
                "username" to username,
                "email" to email,
                "password" to password,
                "confirmPassword" to password,
                "country" to "XX"
            ),
            HttpStatus.BAD_REQUEST
        )
        assertNotNull(error)

        // then the user is not created
        val errorBody = getBody(error)
        assertNotNull(errorBody)
        assertEquals(InvalidCountry().message, errorBody.detail)
    }

    @Test
    fun `Try to signup a user with invalid password and fails with code 400`() {
        // given information for a new user
        val username = generateRandomUsername()
        val email = generateEmail(username)
        val password = "123456789"
        val country = "PT"

        // when trying to create a user with an invalid password
        val errorInvalidPassword = post<Problem>(
            client,
            api(Uris.User.SIGNUP),
            mapOf(
                "username" to username,
                "email" to email,
                "password" to password,
                "confirmPassword" to generateSecurePassword(),
                "country" to country
            ),
            HttpStatus.BAD_REQUEST
        )
        assertNotNull(errorInvalidPassword)

        // when trying to create a user with an invalid confirm password
        val errorInvalidConfirmPassword = post<Problem>(
            client,
            api(Uris.User.SIGNUP),
            mapOf(
                "username" to username,
                "email" to email,
                "password" to generateSecurePassword(),
                "confirmPassword" to password,
                "country" to country
            ),
            HttpStatus.BAD_REQUEST
        )
        assertNotNull(errorInvalidConfirmPassword)

        // then the user is not created
        val errorInvalidPasswordBody = getBody(errorInvalidPassword)
        val errorInvalidConfirmPasswordBody = getBody(errorInvalidConfirmPassword)
        assertNotNull(errorInvalidPasswordBody)
        assertNotNull(errorInvalidConfirmPasswordBody)
        assertEquals("Password $VALID_PASSWORD_MSG", errorInvalidPasswordBody.detail)
        assertEquals("ConfirmPassword $VALID_PASSWORD_MSG", errorInvalidConfirmPasswordBody.detail)
    }

    @Test
    fun `Try to signup a user with different passwords and fails with code 400`() {
        // given information for a new user
        val username = generateRandomUsername()
        val email = generateEmail(username)
        val password = generateSecurePassword()
        val country = "PT"

        // when trying to create a user with an invalid password
        val error = post<Problem>(
            client,
            api(Uris.User.SIGNUP),
            mapOf(
                "username" to username,
                "email" to email,
                "password" to password,
                "confirmPassword" to generateSecurePassword(),
                "country" to country
            ),
            HttpStatus.BAD_REQUEST
        )
        assertNotNull(error)

        // then the user is not created
        val errorBody = getBody(error)
        assertNotNull(errorBody)
        assertEquals(PasswordsDoNotMatch().message, errorBody.detail)
    }

    @Test
    fun `Logout an user successfully and then login him by name successfully with code 204`() {
        // given an existing user logged out
        val username = generateRandomUsername()
        val email = generateEmail(username)
        val country = "PT"
        val password = generateSecurePassword()
        val token = signUp(username, email, country, password)
        assertNotNull(token)

        val oldToken = logout(token)
        assertTrue(oldToken.isEmpty())

        // when logging in
        val newToken = login(username = username, password = password)
        assertNotNull(newToken)

        // then the user is logged in successfully
        val authenticatedUserBody = getUser(newToken)
        assertNotNull(authenticatedUserBody)
        assertEquals(username, authenticatedUserBody.user.name)
        assertEquals(email, authenticatedUserBody.user.email)
        assertEquals(country, authenticatedUserBody.user.country)
    }

    @Test
    fun `Logout an user successfully and then login him by email successfully with code 204`() {
        // given an existing user logged out
        val username = generateRandomUsername()
        val email = generateEmail(username)
        val country = "PT"
        val password = generateSecurePassword()
        val token = signUp(username, email, country, password)
        assertNotNull(token)

        val oldToken = logout(token)
        assertTrue(oldToken.isEmpty())

        // when logging in
        val newToken = login(email = email, password = password)
        assertNotNull(newToken)

        // then the user is logged in successfully
        val authenticatedUserBody = getUser(newToken)
        assertNotNull(authenticatedUserBody)
        assertEquals(username, authenticatedUserBody.user.name)
        assertEquals(email, authenticatedUserBody.user.email)
        assertEquals(country, authenticatedUserBody.user.country)
    }

    @Test
    fun `Try to login an user with a non-existing username and fails with code 404`() {
        // given a non-existing username
        val username = generateRandomUsername()
        val password = generateSecurePassword()

        // when trying to login with a non-existing username
        val error = post<Problem>(
            client,
            api(Uris.User.LOGIN),
            mapOf("username" to username, "password" to password),
            HttpStatus.NOT_FOUND
        )
        assertNotNull(error)

        // then the user is not logged in and an error is returned with the UserNotFound message
        val errorBody = getBody(error)
        assertNotNull(errorBody)
        assertEquals(UserNotFound(username).message, errorBody.detail)
    }

    @Test
    fun `Try to login an user with a non-existing email and fails with code 404`() {
        // given a non-existing username
        val email = generateEmail("user")
        val password = generateSecurePassword()

        // when trying to login with a non-existing username
        val error = post<Problem>(
            client,
            api(Uris.User.LOGIN),
            mapOf("email" to email, "password" to password),
            HttpStatus.NOT_FOUND
        )
        assertNotNull(error)

        // then the user is not logged in and an error is returned with the UserNotFound message
        val errorBody = getBody(error)
        assertNotNull(errorBody)
        assertEquals(UserNotFound(email).message, errorBody.detail)
    }

    @Test
    fun `Try to login an already logged in user and fails with code 400`() {
        // given a logged-in user
        val username = generateRandomUsername()
        val password = generateSecurePassword()
        signUp(username, generateEmail(username), "PT", generateSecurePassword())

        // when trying to login again
        val error = post<Problem>(
            client,
            api(Uris.User.LOGIN),
            mapOf("username" to username, "password" to password),
            HttpStatus.BAD_REQUEST
        )
        assertNotNull(error)

        // then the user was already logged in and an error is returned
        val errorBody = getBody(error)
        assertNotNull(errorBody)
        assertEquals(UserAlreadyLoggedIn().message, errorBody.detail)
    }

    @Test
    fun `Try to login an user with a different password and fails with code 400`() {
        // given an existing user
        val user = publicTestUser

        // when trying to login with a different password
        val error = post<Problem>(
            client,
            api(Uris.User.LOGIN),
            mapOf("username" to user.name, "password" to generateSecurePassword()),
            HttpStatus.BAD_REQUEST
        )
        assertNotNull(error)

        // then the user is not logged in and an error is returned
        val errorBody = getBody(error)
        assertNotNull(errorBody)
        assertEquals(IncorrectPassword().message, errorBody.detail)
    }

    @Test
    fun `Try to login with an invalid username and fails with code 400`() {
        // given invalids usernames
        val usernameToShort = "ab"
        val usernameToLong = "wPIETGFH29THshfgOPHohasfn21h"
        val invalidUsernameString = "/-+==;:"

        // when trying to login with an invalid username
        val errorWithShortUsername = post<Problem>(
            client,
            api(Uris.User.LOGIN),
            mapOf("username" to usernameToShort, "password" to generateSecurePassword()),
            HttpStatus.BAD_REQUEST
        )
        assertNotNull(errorWithShortUsername)

        val errorWithLongUsername = post<Problem>(
            client,
            api(Uris.User.LOGIN),
            mapOf("username" to usernameToLong, "password" to generateSecurePassword()),
            HttpStatus.BAD_REQUEST
        )
        assertNotNull(errorWithLongUsername)

        val errorWithInvalidUsernameString = post<Problem>(
            client,
            api(Uris.User.LOGIN),
            mapOf("username" to invalidUsernameString, "password" to generateSecurePassword()),
            HttpStatus.BAD_REQUEST
        )
        assertNotNull(errorWithInvalidUsernameString)

        // then the user is not logged in and an error is returned
        val errorWithShortUsernameBody = getBody(errorWithShortUsername)
        val errorWithLongUsernameBody = getBody(errorWithLongUsername)
        val errorWithInvalidUsernameStringBody = getBody(errorWithInvalidUsernameString)
        assertNotNull(errorWithShortUsernameBody)
        assertNotNull(errorWithLongUsernameBody)
        assertNotNull(errorWithInvalidUsernameStringBody)
        assertEquals("Username " + UserDomain.USERNAME_LENGTH_MSG, errorWithShortUsernameBody.detail)
        assertEquals("Username " + UserDomain.USERNAME_LENGTH_MSG, errorWithLongUsernameBody.detail)
        assertEquals("Username $VALID_STRING_MSG", errorWithInvalidUsernameStringBody.detail)
    }

    @Test
    fun `Try to login with an invalid email and fails with code 400`() {
        // given invalids emails
        val invalidEmail = "invalidEmail"
        val invalidEmail2 = "invalidEmail@"

        // when trying to login with an invalid email
        val errorWithInvalidEmail = post<Problem>(
            client,
            api(Uris.User.LOGIN),
            mapOf("email" to invalidEmail, "password" to generateSecurePassword()),
            HttpStatus.BAD_REQUEST
        )
        assertNotNull(errorWithInvalidEmail)

        val errorWithInvalidEmail2 = post<Problem>(
            client,
            api(Uris.User.LOGIN),
            mapOf("email" to invalidEmail2, "password" to generateSecurePassword()),
            HttpStatus.BAD_REQUEST
        )
        assertNotNull(errorWithInvalidEmail2)

        // then the user is not logged in and an error is returned
        val errorWithInvalidEmailBody = getBody(errorWithInvalidEmail)
        val errorWithInvalidEmail2Body = getBody(errorWithInvalidEmail2)
        assertNotNull(errorWithInvalidEmailBody)
        assertNotNull(errorWithInvalidEmail2Body)
        assertEquals("Email " + UserDomain.VALID_EMAIL_MSG, errorWithInvalidEmailBody.detail)
        assertEquals("Email " + UserDomain.VALID_EMAIL_MSG, errorWithInvalidEmail2Body.detail)
    }

    @Test
    fun `Try to login with an invalid password and fails with code 400`() {
        // given an existing user
        val user = publicTestUser

        // when trying to login with an invalid password
        val error = post<Problem>(
            client,
            api(Uris.User.LOGIN),
            mapOf("username" to user.name, "password" to "invalidPassword"),
            HttpStatus.BAD_REQUEST
        )
        assertNotNull(error)

        // then the user is not logged in and an error is returned
        val errorBody = getBody(error)
        assertNotNull(errorBody)
        assertEquals("Password $VALID_PASSWORD_MSG", errorBody.detail)
    }

    @Test
    fun `Reset password successfully with code 204`() {
        // given an existing user
        val user = publicTestUser

        // when resetting the password
        val newPassword = generateSecurePassword()
        resetPassword(publicTestUser.email, newPassword, newPassword)

        // then the password is reset successfully
        val newToken = login(email = user.email, password = newPassword)
        assertNotNull(newToken)

        // then the user is logged in successfully
        val authenticatedUserBody = getUser(newToken)
        assertNotNull(authenticatedUserBody)
        assertEquals(publicTestUser.name, authenticatedUserBody.user.name)
        assertEquals(publicTestUser.email, authenticatedUserBody.user.email)
    }

    @Test
    fun `Try to reset password with different passwords and fails with code 400`() {
        // given an existing user
        val user = publicTestUser

        // when trying to reset the password with different passwords
        val newPassword = generateSecurePassword()
        val error = patch<Problem>(
            client,
            api(Uris.User.USER_RESET_PASSWORD),
            body = mapOf(
                "email" to user.email,
                "newPassword" to newPassword,
                "confirmPassword" to generateSecurePassword()
            ),
            responseStatus = HttpStatus.BAD_REQUEST
        )

        // then the password is not reset and an error is returned
        assertNotNull(error)
        val errorBody = getBody(error)
        assertNotNull(errorBody)
        assertEquals(PasswordsDoNotMatch().message, errorBody.detail)
    }

    @Test
    fun `Try to reset password with invalid email and fails with code 400`() {
        // when trying to reset the password with an invalid email
        val error = patch<Problem>(
            client,
            api(Uris.User.USER_RESET_PASSWORD),
            body = mapOf(
                "email" to "invalidEmail",
                "newPassword" to generateSecurePassword(),
                "confirmPassword" to generateSecurePassword()
            ),
            responseStatus = HttpStatus.BAD_REQUEST
        )

        // then the password is not reset and an error is returned
        assertNotNull(error)
        val errorBody = getBody(error)
        assertNotNull(errorBody)
        assertEquals("Email " + UserDomain.VALID_EMAIL_MSG, errorBody.detail)
    }

    @Test
    fun `Try to reset password with invalid password and fails with code 400`() {
        // given an existing user and an invalid password
        val user = publicTestUser
        val invalidPassword = "12345678"

        // when trying to reset the password with an invalid password
        val errorInvalidPassword = patch<Problem>(
            client,
            api(Uris.User.USER_RESET_PASSWORD),
            body = mapOf(
                "email" to user.email,
                "newPassword" to invalidPassword,
                "confirmPassword" to generateSecurePassword()
            ),
            responseStatus = HttpStatus.BAD_REQUEST
        )
        assertNotNull(errorInvalidPassword)

        // when trying to reset the password with an invalid confirm password
        val errorInvalidConfirmPassword = patch<Problem>(
            client,
            api(Uris.User.USER_RESET_PASSWORD),
            body = mapOf(
                "email" to user.email,
                "newPassword" to generateSecurePassword(),
                "confirmPassword" to invalidPassword
            ),
            responseStatus = HttpStatus.BAD_REQUEST
        )
        assertNotNull(errorInvalidConfirmPassword)

        // then the password is not reset and an error is returned
        val errorInvalidPasswordBody = getBody(errorInvalidPassword)
        val errorInvalidConfirmPasswordBody = getBody(errorInvalidConfirmPassword)
        assertNotNull(errorInvalidConfirmPasswordBody)
        assertNotNull(errorInvalidPasswordBody)
        assertEquals("NewPassword $VALID_PASSWORD_MSG", errorInvalidPasswordBody.detail)
        assertEquals("ConfirmPassword $VALID_PASSWORD_MSG", errorInvalidConfirmPasswordBody.detail)
    }
}
