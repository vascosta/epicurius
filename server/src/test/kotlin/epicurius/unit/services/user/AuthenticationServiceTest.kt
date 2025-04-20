package epicurius.unit.services.user

import epicurius.domain.PagingParams
import epicurius.domain.exceptions.InvalidCountry
import epicurius.domain.exceptions.InvalidToken
import epicurius.domain.exceptions.PasswordsDoNotMatch
import epicurius.domain.exceptions.UserAlreadyExists
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
        val passwordHash = userDomain.encodePassword(password)
        val token = userDomain.generateTokenValue()
        val tokenHash = userDomain.hashToken(token)

        // mocks for createUser
        whenever(jdbiUserRepositoryMock.getUser(username, email)).thenReturn(null)
        whenever(countriesDomainMock.checkIfCountryCodeIsValid(country)).thenReturn(true)
        whenever(userDomainMock.encodePassword(password)).thenReturn(passwordHash)
        whenever(jdbiUserRepositoryMock.checkIfUserIsLoggedIn(username, email)).thenReturn(false)
        whenever(userDomainMock.generateTokenValue()).thenReturn(token)
        whenever(userDomainMock.hashToken(token)).thenReturn(tokenHash)

        // when creating a user
        val createToken = createUser(username, email, country, password, password)
        verify(jdbiUserRepositoryMock).createUser(username, email, country, passwordHash)
        verify(jdbiTokenRepositoryMock).createToken(tokenHash, username, email)

        // then the user is created successfully
        assertNotNull(createToken)
        assertEquals(token, createToken)

        // mocks for getAuthenticatedUser
        val mockUser = User(1, username, email, passwordHash, tokenHash, country, false, emptyList(), emptyList(), null)
        whenever(userDomainMock.isToken(createToken)).thenReturn(true)
        whenever(userDomainMock.hashToken(createToken)).thenReturn(tokenHash)
        whenever(jdbiUserRepositoryMock.getUser(tokenHash = tokenHash)).thenReturn(mockUser)

        // when retrieving the authenticated user
        val authenticatedUser = getAuthenticatedUser(token)
        assertNotNull(authenticatedUser)

        // then the user is retrieved successfully
        val user = authenticatedUser.user
        assertEquals(mockUser, user)
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
            createUser(user.name, randomEmail, "PT", password, password)
        }

        // when creating a user with an existing email
        // then the user cannot be created and throws UserAlreadyExists Exception
        assertFailsWith<UserAlreadyExists> {
            createUser(randomUsername, user.email, "PT", password, password)
        }

        // when creating a user with an existing username and email
        // then the user cannot be created and throws UserAlreadyExists Exception
        assertFailsWith<UserAlreadyExists> {
            createUser(user.name, user.email, "PT", password, password)
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
}
