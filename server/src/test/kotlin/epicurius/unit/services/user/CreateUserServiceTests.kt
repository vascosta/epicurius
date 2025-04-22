package epicurius.unit.services.user

import epicurius.domain.exceptions.InvalidCountry
import epicurius.domain.exceptions.PasswordsDoNotMatch
import epicurius.domain.exceptions.UserAlreadyExists
import epicurius.utils.generateEmail
import epicurius.utils.generateRandomUsername
import epicurius.utils.generateSecurePassword
import org.mockito.kotlin.verify
import org.mockito.kotlin.whenever
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith
import kotlin.test.assertNotNull

class CreateUserServiceTests : UserServiceTest() {

    private val username = generateRandomUsername()
    private val email = generateEmail(username)
    private val country = "PT"
    private val password = generateSecurePassword()

    @Test
    fun `Should create new user and retrieve it successfully`() {
        // given information to create a user (username, email, country, password)
        val passwordHash = userDomain.encodePassword(password)
        val token = userDomain.generateTokenValue()
        val tokenHash = userDomain.hashToken(token)

        // mocks
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
    }

    @Test
    fun `Should throw UserAlreadyExists exception when creating an user with an existing username or email`() {
        // given an existing user and a different username and email
        val randomUsername = generateRandomUsername()
        val randomEmail = generateEmail(randomUsername)

        // mocks
        whenever(jdbiUserRepositoryMock.getUser(testUsername, randomEmail)).thenReturn(testUser)
        whenever(jdbiUserRepositoryMock.getUser(randomUsername, testUser.email)).thenReturn(testUser)
        whenever(jdbiUserRepositoryMock.getUser(testUsername, testUser.email)).thenReturn(testUser)

        // when creating a user with an existing username
        // then the user cannot be created and throws UserAlreadyExists exception
        assertFailsWith<UserAlreadyExists> {
            createUser(testUsername, randomEmail, "PT", password, password)
        }

        // when creating a user with an existing email
        // then the user cannot be created and throws UserAlreadyExists exception
        assertFailsWith<UserAlreadyExists> {
            createUser(randomUsername, testUser.email, "PT", password, password)
        }

        // when creating a user with an existing username and email
        // then the user cannot be created and throws UserAlreadyExists exception
        assertFailsWith<UserAlreadyExists> {
            createUser(testUsername, testUser.email, "PT", password, password)
        }
    }

    @Test
    fun `Should throw InvalidCountry exception creating an user with an invalid country`() {
        // given an invalid country
        val invalidCounty = "XX"

        // mocks
        whenever(jdbiUserRepositoryMock.getUser(username, email)).thenReturn(null)
        whenever(countriesDomainMock.checkIfCountryCodeIsValid(invalidCounty)).thenReturn(false)

        // when creating a user with an invalid country
        // then the user cannot be created and throws InvalidCountry exception
        assertFailsWith<InvalidCountry> { createUser(username, email, invalidCounty, password, password) }
    }

    @Test
    fun `Try to create user with different passwords and throws PasswordsDoNotMatch Exception`() {
        // given a different passwords
        val password1 = generateSecurePassword()
        val password2 = generateSecurePassword()

        // mocks
        whenever(jdbiUserRepositoryMock.getUser(username, email)).thenReturn(null)
        whenever(countriesDomainMock.checkIfCountryCodeIsValid(country)).thenReturn(true)

        // when creating a user with different passwords
        // then the user cannot be created and throws PasswordsDoNotMatch exception
        assertFailsWith<PasswordsDoNotMatch> {
            createUser(username, generateEmail(username), "PT", password1, password2)
        }
    }
}
