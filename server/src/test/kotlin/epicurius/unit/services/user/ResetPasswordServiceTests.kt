package epicurius.unit.services.user

import epicurius.domain.exceptions.PasswordsDoNotMatch
import epicurius.domain.exceptions.UserNotFound
import epicurius.utils.generateEmail
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
        whenever(jdbiUserRepositoryMock.getUser(email = publicTestUser.email)).thenReturn(publicTestUser)
        whenever(userDomainMock.encodePassword(newPassword)).thenReturn(mockPasswordHash)

        // when resetting the password
        resetPassword(publicTestUser.email, newPassword, newPassword)

        // then the password was reset successfully
        verify(jdbiUserRepositoryMock).resetPassword(publicTestUser.id, mockPasswordHash)
        verify(jdbiTokenRepositoryMock).deleteToken(publicTestUser.id)
    }

    @Test
    fun `Should throw UserNotFound when resetting a user's password for a non-existing user`() {
        // given a non-existing user email
        val nonExistingUserEmail = generateEmail("nonexistinguser")

        // mock
        whenever(jdbiUserRepositoryMock.getUser(email = nonExistingUserEmail)).thenReturn(null)

        // when resetting the password
        // then the password cannot be reset and throws UserNotFound exception
        assertFailsWith<UserNotFound> {
            resetPassword(nonExistingUserEmail, generateSecurePassword(), generateSecurePassword())
        }
    }

    @Test
    fun `Should throw PasswordsDoNotMatch when resetting a user's password with different passwords`() {
        // given a user (publicTestUser) and different passwords
        val password1 = generateSecurePassword()
        val password2 = generateSecurePassword()

        // mock
        whenever(jdbiUserRepositoryMock.getUser(email = publicTestUser.email)).thenReturn(publicTestUser)

        // when resetting the password with different passwords
        // then the password cannot be reset and throws PasswordsDoNotMatch exception
        assertFailsWith<PasswordsDoNotMatch> {
            resetPassword(publicTestUser.email, password1, password2)
        }
    }
}
