package epicurius.unit.services.user

import epicurius.domain.exceptions.InvalidCountry
import epicurius.domain.exceptions.PasswordsDoNotMatch
import epicurius.domain.exceptions.UserAlreadyExists
import epicurius.utils.generateEmail
import epicurius.utils.generateRandomUsername
import epicurius.utils.generateSecurePassword
import org.mockito.kotlin.verify
import org.mockito.kotlin.whenever
import java.time.LocalDate
import kotlin.test.Test
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

        // mock
        val mockUserId = 1
        val mockPasswordHash = userDomain.encodePassword(password)
        val mockToken = userDomain.generateTokenValue()
        val mockTokenHash = userDomain.hashToken(mockToken)
        val mockLastUsed = LocalDate.now()
        whenever(jdbiUserRepositoryMock.getUser(username, email)).thenReturn(null)
        whenever(countriesDomainMock.checkIfCountryCodeIsValid(country)).thenReturn(true)
        whenever(userDomainMock.encodePassword(password)).thenReturn(mockPasswordHash)
        whenever(jdbiUserRepositoryMock.createUser(username, email, country, mockPasswordHash))
            .thenReturn(mockUserId)
        whenever(jdbiUserRepositoryMock.checkIfUserIsLoggedIn(mockUserId)).thenReturn(false)
        whenever(userDomainMock.generateTokenValue()).thenReturn(mockToken)
        whenever(userDomainMock.hashToken(mockToken)).thenReturn(mockTokenHash)

        // when creating a user
        val createToken = createUser(username, email, country, password, password)

        // then the user is created successfully
        verify(jdbiTokenRepositoryMock).createToken(mockTokenHash, mockLastUsed, mockUserId)
        assertNotNull(createToken)
    }

    @Test
    fun `Should throw UserAlreadyExists exception when creating an user with an existing username or email`() {
        // given an existing user (publicTestUser) and a different username and email
        val randomUsername = generateRandomUsername()
        val randomEmail = generateEmail(randomUsername)

        // mock
        whenever(jdbiUserRepositoryMock.getUser(publicTestUsername, randomEmail)).thenReturn(publicTestUser)
        whenever(jdbiUserRepositoryMock.getUser(randomUsername, publicTestUser.email)).thenReturn(publicTestUser)
        whenever(jdbiUserRepositoryMock.getUser(publicTestUsername, publicTestUser.email)).thenReturn(publicTestUser)

        // when creating a user with an existing username
        // then the user cannot be created and throws UserAlreadyExists exception
        assertFailsWith<UserAlreadyExists> {
            createUser(publicTestUsername, randomEmail, "PT", password, password)
        }

        // when creating a user with an existing email
        // then the user cannot be created and throws UserAlreadyExists exception
        assertFailsWith<UserAlreadyExists> {
            createUser(randomUsername, publicTestUser.email, "PT", password, password)
        }

        // when creating a user with an existing username and email
        // then the user cannot be created and throws UserAlreadyExists exception
        assertFailsWith<UserAlreadyExists> {
            createUser(publicTestUsername, publicTestUser.email, "PT", password, password)
        }
    }

    @Test
    fun `Should throw InvalidCountry exception creating an user with an invalid country`() {
        // given an invalid country
        val invalidCounty = "XX"

        // mock
        whenever(jdbiUserRepositoryMock.getUser(username, email)).thenReturn(null)
        whenever(countriesDomainMock.checkIfCountryCodeIsValid(invalidCounty)).thenReturn(false)

        // when creating a user with an invalid country
        // then the user cannot be created and throws InvalidCountry exception
        assertFailsWith<InvalidCountry> { createUser(username, email, invalidCounty, password, password) }
    }

    @Test
    fun `Should throw PasswordsDoNotMatch exception when creating an user with different passwords`() {
        // given different passwords
        val password1 = generateSecurePassword()
        val password2 = generateSecurePassword()

        // mock
        whenever(jdbiUserRepositoryMock.getUser(username, email)).thenReturn(null)
        whenever(countriesDomainMock.checkIfCountryCodeIsValid(country)).thenReturn(true)

        // when creating a user with different passwords
        // then the user cannot be created and throws PasswordsDoNotMatch exception
        assertFailsWith<PasswordsDoNotMatch> {
            createUser(username, generateEmail(username), "PT", password1, password2)
        }
    }
}
