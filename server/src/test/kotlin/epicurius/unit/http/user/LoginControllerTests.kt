package epicurius.unit.http.user

import epicurius.domain.exceptions.IncorrectPassword
import epicurius.domain.exceptions.UserAlreadyLoggedIn
import epicurius.domain.exceptions.UserNotFound
import epicurius.http.user.models.input.LoginInputModel
import epicurius.utils.generateSecurePassword
import org.mockito.kotlin.verify
import org.mockito.kotlin.whenever
import org.springframework.http.HttpStatus
import org.springframework.http.HttpStatusCode
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith

class LoginControllerTests : UserHttpTest() {

    private val loginInputInfo = LoginInputModel(publicTestUsername, publicTestUser.user.email, generateSecurePassword())

    @Test
    fun `Should login a user by name successfully`() {
        // given a user (publicTestUser)

        // mock
        val mockToken = userDomain.generateTokenValue()
        whenever(userServiceMock.login(loginInputInfo.name, null, loginInputInfo.password)).thenReturn(mockToken)

        // when logging in by name
        val response = login(loginInputInfo.copy(email = null), mockResponse)

        // then the user is logged in successfully
        verify(mockResponse).addHeader("Authorization", "Bearer $mockToken")
        assertEquals(HttpStatus.NO_CONTENT, response.statusCode)
    }

    @Test
    fun `Should login a user by email successfully`() {
        // given a user (publicTestUser)

        // mock
        val mockToken = userDomain.generateTokenValue()
        whenever(userServiceMock.login(null, loginInputInfo.email, loginInputInfo.password)).thenReturn(mockToken)

        // when logging in by email
        val response = login(loginInputInfo.copy(name = null), mockResponse)

        // then the user is logged in successfully
        verify(mockResponse).addHeader("Authorization", "Bearer $mockToken")
        assertEquals(HttpStatus.NO_CONTENT, response.statusCode)
    }

    @Test
    fun `Should throw UserNotFound exception when login with an non existing user `() {
        // given a non-existing username and email
        val nonExistingUsername = ""
        val nonExistingEmail = ""

        // mock
        whenever(userServiceMock.login(nonExistingUsername, null, loginInputInfo.password)).thenThrow(UserNotFound(nonExistingUsername))
        whenever(userServiceMock.login(null, nonExistingEmail, loginInputInfo.password)).thenThrow(UserNotFound(nonExistingEmail))

        // when logging in
        // then the user cannot be logged in and throws UserNotFound exception
        assertFailsWith<UserNotFound> { login(loginInputInfo.copy(name = nonExistingUsername, email = null), mockResponse) }
        assertFailsWith<UserNotFound> { login(loginInputInfo.copy(name = null, email = nonExistingEmail), mockResponse) }
    }

    @Test
    fun `Should throw UserAlreadyLoggedIn exception when login with an already logged in user`() {
        // given a logged-in user (publicTestUser)

        // mock
        whenever(userServiceMock.login(loginInputInfo.name, null, loginInputInfo.password)).thenThrow(UserAlreadyLoggedIn())
        whenever(userServiceMock.login(null, loginInputInfo.email, loginInputInfo.password)).thenThrow(UserAlreadyLoggedIn())

        // when logging in
        // then the user cannot be logged in and throws UserAlreadyLoggedIn exception
        assertFailsWith<UserAlreadyLoggedIn> { login(loginInputInfo.copy(name = publicTestUsername, email = null), mockResponse) }
        assertFailsWith<UserAlreadyLoggedIn> { login(loginInputInfo.copy(name = null, email = publicTestUser.user.email), mockResponse) }
    }

    @Test
    fun `Should throw IncorrectPassword exception when login with an incorrect password`() {
        // given a user (publicTestUser) and an incorrect password
        val incorrectPassword = "incorrectPassword"

        // mock
        whenever(userServiceMock.login(publicTestUsername, null, incorrectPassword)).thenThrow(IncorrectPassword())
        whenever(userServiceMock.login(null, publicTestUser.user.email, incorrectPassword)).thenThrow(IncorrectPassword())

        // when logging in with an incorrect password
        // then the user cannot be logged in and throws IncorrectPassword exception
        assertFailsWith<IncorrectPassword> {
            login(loginInputInfo.copy(name = publicTestUsername, email = null, password = incorrectPassword), mockResponse)
        }
        assertFailsWith<IncorrectPassword> {
            login(loginInputInfo.copy(name = null, email = publicTestUser.user.email, password = incorrectPassword), mockResponse)
        }
    }
}
