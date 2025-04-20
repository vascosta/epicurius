package epicurius.unit.services.user

import epicurius.domain.exceptions.PasswordsDoNotMatch
import epicurius.utils.generateSecurePassword
import org.mockito.kotlin.verify
import org.mockito.kotlin.whenever
import kotlin.test.Test
import kotlin.test.assertFailsWith

class ResetPasswordServiceTests : UserServiceTest() {

    @Test
    fun `Should reset a user's password successfully`() {
        // given an existing user (testUser) and a new password
        val newPassword = generateSecurePassword()

        // mock
        val mockPasswordHash = userDomain.encodePassword(newPassword)
        whenever(userDomainMock.encodePassword(newPassword)).thenReturn(mockPasswordHash)

        // when resetting the password
        // then the password was reset successfully
        resetPassword(testUser.email, newPassword, newPassword)
        verify(jdbiUserRepositoryMock).resetPassword(testUser.email, mockPasswordHash)
        verify(jdbiTokenRepositoryMock).deleteToken(email = testUser.email)
    }

    @Test
    fun `Should throw PasswordsDoNotMatch when resetting a user's password with different passwords`() {
        // given an existing user (testUser) and different passwords
        val password1 = generateSecurePassword()
        val password2 = generateSecurePassword()

        // when resetting the password with different passwords
        // then the password cannot be reset and throws PasswordsDoNotMatch Exception
        assertFailsWith<PasswordsDoNotMatch> {
            resetPassword(testUser.email, password1, password2)
        }
    }
}
