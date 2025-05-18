package epicurius.unit.services.user

import epicurius.domain.exceptions.IncorrectPassword
import epicurius.domain.exceptions.UserAlreadyLoggedIn
import epicurius.domain.exceptions.UserNotFound
import epicurius.utils.generateSecurePassword
import org.mockito.kotlin.verify
import org.mockito.kotlin.whenever
import java.time.LocalDate
import java.util.UUID.randomUUID
import kotlin.test.Test
import kotlin.test.assertFailsWith
import kotlin.test.assertNotNull

class LoginServiceTests : UserServiceTest() {

    @Test
    fun `Should login a user by name successfully`() {
        // given a user (publicTestUser)

        // mock
        val mockPassword = generateSecurePassword()
        val mockToken = randomUUID().toString()
        val mockTokenHash = userDomain.hashToken(mockToken)
        whenever(jdbiUserRepositoryMock.getUser(publicTestUsername)).thenReturn(publicTestUser)
        whenever(jdbiUserRepositoryMock.checkIfUserIsLoggedIn(publicTestUser.id)).thenReturn(false)
        whenever(userDomainMock.verifyPassword(mockPassword, publicTestUser.passwordHash)).thenReturn(true)
        whenever(jdbiUserRepositoryMock.checkIfUserIsLoggedIn(publicTestUser.id)).thenReturn(false)
        whenever(userDomainMock.generateTokenValue()).thenReturn(mockToken)
        whenever(userDomainMock.hashToken(mockToken)).thenReturn(mockTokenHash)

        // when logging in by name
        val loginToken = login(publicTestUsername, password = mockPassword)

        // then the user is logged in successfully
        verify(jdbiTokenRepositoryMock).createToken(mockTokenHash, LocalDate.now(), publicTestUser.id)
        assertNotNull(loginToken)
    }

    @Test
    fun `Should login a user by email successfully`() {
        // given a user (publicTestUser)

        // mock
        val mockPassword = generateSecurePassword()
        val mockToken = randomUUID().toString()
        val mockTokenHash = userDomain.hashToken(mockToken)
        whenever(jdbiUserRepositoryMock.getUser(email = publicTestUser.email)).thenReturn(publicTestUser)
        whenever(jdbiUserRepositoryMock.checkIfUserIsLoggedIn(publicTestUser.id)).thenReturn(false)
        whenever(userDomainMock.verifyPassword(mockPassword, publicTestUser.passwordHash)).thenReturn(true)
        whenever(jdbiUserRepositoryMock.checkIfUserIsLoggedIn(publicTestUser.id)).thenReturn(false)
        whenever(userDomainMock.generateTokenValue()).thenReturn(mockToken)
        whenever(userDomainMock.hashToken(mockToken)).thenReturn(mockTokenHash)

        // when logging in by name
        val loginToken = login(email = publicTestUser.email, password = mockPassword)

        // then the user is logged in successfully
        verify(jdbiTokenRepositoryMock).createToken(mockTokenHash, LocalDate.now(), publicTestUser.id)
        assertNotNull(loginToken)
    }

    @Test
    fun `Should throw UserNotFound exception when login with an non existing user `() {
        // given a non-existing username and email
        val nonExistingUsername = ""
        val nonExistingEmail = ""
        val password = generateSecurePassword()

        // mock
        whenever(jdbiUserRepositoryMock.getUser(nonExistingUsername)).thenReturn(null)
        whenever(jdbiUserRepositoryMock.getUser(email = nonExistingEmail)).thenReturn(null)

        // when logging in
        // then the user cannot be logged in and throws UserNotFound exception
        assertFailsWith<UserNotFound> { login(nonExistingUsername, password = password) }
        assertFailsWith<UserNotFound> { login(email = nonExistingEmail, password = password) }
    }

    @Test
    fun `Should throw UserAlreadyLoggedIn exception when login with an already logged in user`() {
        // given a logged-in user (publicTestUser)
        val password = generateSecurePassword()

        // mock
        whenever(jdbiUserRepositoryMock.getUser(publicTestUsername)).thenReturn(publicTestUser)
        whenever(jdbiUserRepositoryMock.checkIfUserIsLoggedIn(publicTestUser.id)).thenReturn(true)
        whenever(jdbiUserRepositoryMock.getUser(email = publicTestUser.email)).thenReturn(publicTestUser)
        whenever(jdbiUserRepositoryMock.checkIfUserIsLoggedIn(publicTestUser.id)).thenReturn(true)

        // when logging in again
        // then the user cannot be logged in and throws UserAlreadyLoggedIn exception
        assertFailsWith<UserAlreadyLoggedIn> { login(publicTestUsername, password = password) }
        assertFailsWith<UserAlreadyLoggedIn> { login(email = publicTestUser.email, password = password) }
    }

    @Test
    fun `Should throw IncorrectPassword exception when login with an incorrect password`() {
        // given a user (publicTestUser) and an incorrect password
        val incorrectPassword = "incorrectPassword"

        // mock
        whenever(jdbiUserRepositoryMock.getUser(publicTestUsername)).thenReturn(publicTestUser)
        whenever(jdbiUserRepositoryMock.checkIfUserIsLoggedIn(publicTestUser.id)).thenReturn(false)
        whenever(jdbiUserRepositoryMock.getUser(email = publicTestUser.email)).thenReturn(publicTestUser)
        whenever(jdbiUserRepositoryMock.checkIfUserIsLoggedIn(publicTestUser.id)).thenReturn(false)
        whenever(userDomainMock.verifyPassword(incorrectPassword, publicTestUser.passwordHash)).thenReturn(false)

        // when logging in with an incorrect password
        // then the user cannot be logged in and throws IncorrectPassword exception
        assertFailsWith<IncorrectPassword> { login(publicTestUsername, password = incorrectPassword) }
        assertFailsWith<IncorrectPassword> { login(email = publicTestUser.email, password = incorrectPassword) }
    }
}
