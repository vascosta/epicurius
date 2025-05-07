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
        // given a user (publicTestUser) and a new password
        val newPassword = generateSecurePassword()

        // mock
        val mockPasswordHash = userDomain.encodePassword(newPassword)
        whenever(userDomainMock.encodePassword(newPassword)).thenReturn(mockPasswordHash)

        // when resetting the password
        resetPassword(publicTestUser.email, newPassword, newPassword)

        // then the password was reset successfully
        verify(jdbiUserRepositoryMock).resetPassword(publicTestUser.id, mockPasswordHash)
        verify(jdbiTokenRepositoryMock).deleteToken(publicTestUser.id)
    }

    @Test
    fun `Should throw PasswordsDoNotMatch when resetting a user's password with different passwords`() {
        // given a user (publicTestUser) and different passwords
        val password1 = generateSecurePassword()
        val password2 = generateSecurePassword()

        // when resetting the password with different passwords
        // then the password cannot be reset and throws PasswordsDoNotMatch exception
        assertFailsWith<PasswordsDoNotMatch> {
            resetPassword(publicTestUser.email, password1, password2)
        }
    }
}
