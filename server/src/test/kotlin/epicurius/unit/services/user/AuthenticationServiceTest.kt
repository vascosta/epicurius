package epicurius.unit.services.user

import epicurius.domain.PagingParams
import epicurius.domain.exceptions.IncorrectPassword
import epicurius.domain.exceptions.InvalidCountry
import epicurius.domain.exceptions.InvalidToken
import epicurius.domain.exceptions.PasswordsDoNotMatch
import epicurius.domain.exceptions.UserAlreadyExists
import epicurius.domain.exceptions.UserAlreadyLoggedIn
import epicurius.domain.exceptions.UserNotFound
import epicurius.domain.user.SearchUser
import epicurius.domain.user.User
import epicurius.unit.services.ServiceTest
import epicurius.utils.createTestUser
import epicurius.utils.generateEmail
import epicurius.utils.generateRandomUsername
import epicurius.utils.generateSecurePassword
import org.mockito.kotlin.verify
import org.mockito.kotlin.whenever
import java.util.UUID
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith
import kotlin.test.assertFalse
import kotlin.test.assertNotNull
import kotlin.test.assertNull
import kotlin.test.assertTrue

class AuthenticationServiceTest : ServiceTest() {

    private var testUser = createTestUser(tm)

    @Test
    fun `Should create new user and retrieve it successfully`() {
        // given user required information
        val username = generateRandomUsername()
        val email = generateEmail(username)
        val country = "PT"
        val password = generateSecurePassword()
        val passwordHash = usersDomain.encodePassword(password)
        val token = usersDomain.generateTokenValue()
        val tokenHash = usersDomain.hashToken(token)

        // mocks for createUser
        whenever(userRepositoryMock.getUser(username, email)).thenReturn(null)
        whenever(countriesDomainMock.checkIfCountryCodeIsValid(country)).thenReturn(true)
        whenever(usersDomainMock.encodePassword(password)).thenReturn(passwordHash)
        whenever(userRepositoryMock.checkIfUserIsLoggedIn(username, email)).thenReturn(false)
        whenever(usersDomainMock.generateTokenValue()).thenReturn(token)
        whenever(usersDomainMock.hashToken(token)).thenReturn(tokenHash)

        // when creating a user
        val createToken = createUser(username, email, country, password, password)
        verify(userRepositoryMock).createUser(username, email, country, passwordHash)
        verify(tokenRepositoryMock).createToken(tokenHash, username, email)

        // then the user is created successfully
        assertNotNull(createToken)
        assertEquals(token, createToken)

        // mocks for getAuthenticatedUser
        val mockUser = User(1, username, email, passwordHash, tokenHash, country, false, emptyList(), emptyList(), null)
        whenever(usersDomainMock.isToken(createToken)).thenReturn(true)
        whenever(usersDomainMock.hashToken(createToken)).thenReturn(tokenHash)
        whenever(userRepositoryMock.getUser(tokenHash = tokenHash)).thenReturn(mockUser)

        // when retrieving the authenticated user
        val authenticatedUser = getAuthenticatedUser(token)
        assertNotNull(authenticatedUser)

        // then the user is retrieved successfully
        val user = authenticatedUser.user
        assertEquals(mockUser, user)
    }

    @Test
    fun `Create new users and retrieve them successfully`() {
        // given 2 created users
        val username = "partial"
        val username2 = "partialUsername"
        val email = generateEmail(username)
        val email2 = generateEmail(username2)
        val country = "PT"
        val password = generateSecurePassword()
        createUser(username, email, country, password, password)
        createUser(username2, email2, country, password, password)

        // when retrieving the users by a partial username
        val users = getUsers("partial", PagingParams())

        // then the users are retrieved successfully
        assertTrue(users.isNotEmpty())
        assertEquals(2, users.size)
        assertTrue(users.contains(SearchUser(username, null)))
        assertTrue(users.contains(SearchUser(username2, null)))
    }

    @Test
    fun `Try to create user with an existing username or email and throws UserAlreadyExists Exception`() {
        // given an existing user and a different username and email
        val user = testUser
        val password = generateSecurePassword()
        val randomUsername = generateRandomUsername()
        val randomEmail = generateEmail(randomUsername)

        // when creating a user with an existing username
        // then the user cannot be created and throws UserAlreadyExists Exception
        assertFailsWith<UserAlreadyExists> {
            createUser(user.username, randomEmail, "PT", password, password)
        }

        // when creating a user with an existing email
        // then the user cannot be created and throws UserAlreadyExists Exception
        assertFailsWith<UserAlreadyExists> {
            createUser(randomUsername, user.email, "PT", password, password)
        }

        // when creating a user with an existing username and email
        // then the user cannot be created and throws UserAlreadyExists Exception
        assertFailsWith<UserAlreadyExists> {
            createUser(user.username, user.email, "PT", password, password)
        }
    }

    @Test
    fun `Try to create user with an invalid country and throws InvalidCountry Exception`() {
        // given an invalid country
        val username = generateRandomUsername()
        val email = generateEmail(username)
        val country = "XX"
        val password = generateSecurePassword()

        // when creating a user with an invalid country
        // then the user cannot be created and throws InvalidCountry Exception
        assertFailsWith<InvalidCountry> { createUser(username, email, country, password, password) }
    }

    @Test
    fun `Try to create user with different passwords and throws PasswordsDoNotMatch Exception`() {
        // given a different password and confirm password
        val username = generateRandomUsername()

        // when creating a user with different passwords
        // then the user cannot be created and throws PasswordsDoNotMatch Exception
        assertFailsWith<PasswordsDoNotMatch> {
            createUser(username, generateEmail(username), "PT", generateSecurePassword(), generateSecurePassword())
        }
    }

    @Test
    fun `Try to retrieve a user with an invalid token and throws InvalidToken Exception`() {
        // given an invalid token
        val token = "abc"

        // when retrieving the authenticated user
        // then the user cannot be retrieved and throws InvalidToken Exception
        assertFailsWith<InvalidToken> { getAuthenticatedUser(token) }
    }

    @Test
    fun `Logout an user successfully and then login him by name successfully`() {
        // given an existing user
        val username = generateRandomUsername()
        val email = generateEmail(username)
        val country = "PT"
        val password = generateSecurePassword()
        createUser(username, email, country, password, password)

        // when logging out
        // then the user is logged out
        logout(username)

        // when logging in by name
        val userToken = login(username, password = password)

        // then the user is logged in successfully
        val authenticatedUser = getAuthenticatedUser(userToken)
        assertNotNull(authenticatedUser)
        assertEquals(username, authenticatedUser.user.username)
        assertEquals(email, authenticatedUser.user.email)
    }

    @Test
    fun `Logout an user successfully and then login him by email successfully`() {
        // given an existing user logged out
        val username = generateRandomUsername()
        val email = generateEmail(username)
        val country = "PT"
        val password = generateSecurePassword()
        createUser(username, email, country, password, password)

        // when logging out
        // then the user is logged out
        logout(username)

        // when logging in by email
        val userToken = login(email = email, password = password)

        // then the user is logged in successfully
        val authenticatedUser = getAuthenticatedUser(userToken)
        assertNotNull(authenticatedUser)
        assertEquals(username, authenticatedUser.user.username)
        assertEquals(email, authenticatedUser.user.email)
    }

    @Test
    fun `Try to login with an non existing user and throws UserNotFound Exception`() {
        // given a non-existing username and email
        val username = ""
        val email = ""
        val password = ""

        // when logging in
        // then the user is cannot be logged in and throws UserNotFound Exception
        assertFailsWith<UserNotFound> { login(username, null, password) }
        assertFailsWith<UserNotFound> { login(null, email, password) }
    }

    @Test
    fun `Try to login with an incorrect password and throws IncorrectPassword Exception`() {
        // given an existing user logged out and an incorrect password
        val user = createTestUser(tm)
        val incorrectPassword = UUID.randomUUID().toString()

        // when logging in with an incorrect password
        // then the user is cannot be logged in and throws IncorrectPassword Exception
        assertFailsWith<IncorrectPassword> { login(user.username, password = incorrectPassword) }
        assertFailsWith<IncorrectPassword> { login(email = user.email, password = incorrectPassword) }
    }

    @Test
    fun `Try to login with an already logged in user and throws UserAlreadyLoggedIn Exception`() {
        // given an existing logged in user
        val username = generateRandomUsername()
        val email = generateEmail(username)
        val country = "PT"
        val password = generateSecurePassword()
        createUser(username, email, country, password, password)

        // when logging in
        // then the user is cannot be logged in and throws UserAlreadyLoggedIn Exception
        assertFailsWith<UserAlreadyLoggedIn> { login(username, null, password) }
        assertFailsWith<UserAlreadyLoggedIn> { login(null, email, password) }
    }

    @Test
    fun `Logout a user successfully`() {
        // given an existing logged in user
        val username = generateRandomUsername()
        val email = generateEmail(username)
        val country = "PT"
        val password = generateSecurePassword()
        val userToken = createUser(username, email, country, password, password)

        // when logging out
        logout(username)

        // then the user is logged out successfully
        val authenticatedUser = getAuthenticatedUser(userToken)
        assertNull(authenticatedUser)
    }

    @Test
    fun `Reset password successfully`() {
        // given an existing user
        val user = testUser

        // when resetting the password
        val newPassword = UUID.randomUUID().toString()
        resetPassword(user.email, newPassword, newPassword)

        // when logging in with the new password
        val userToken = login(email = user.email, password = newPassword)

        // then the password was reset successfully
        val authenticatedUser = getAuthenticatedUser(userToken)
        assertNotNull(authenticatedUser)
        assertEquals(user.username, authenticatedUser.user.username)
        assertEquals(user.email, authenticatedUser.user.email)
    }

    @Test
    fun `Try to reset password with different passwords and throws PasswordsDoNotMatch Exception`() {
        // given an existing user
        val user = testUser

        // when resetting the password with different passwords
        // then the password cannot be reset and throws PasswordsDoNotMatch Exception
        assertFailsWith<PasswordsDoNotMatch> {
            resetPassword(user.email, UUID.randomUUID().toString(), UUID.randomUUID().toString())
        }
    }
}
