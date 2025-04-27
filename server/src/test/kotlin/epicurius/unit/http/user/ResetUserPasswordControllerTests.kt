package epicurius.unit.http.user

import epicurius.domain.exceptions.PasswordsDoNotMatch
import epicurius.http.user.models.input.ResetPasswordInputModel
import epicurius.utils.generateSecurePassword
import org.mockito.kotlin.verify
import org.mockito.kotlin.whenever
import org.springframework.http.HttpStatus
import org.springframework.http.HttpStatusCode
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith

class ResetUserPasswordControllerTests : UserHttpTest() {

    private val password = generateSecurePassword()
    private val resetPasswordInputInfo = ResetPasswordInputModel(publicTestUser.user.email, password, password)

    @Test
    fun `Should reset a user's password successfully`() {
        // given a user (publicTestUser) and a new password (resetPasswordInputInfo)

        // when resetting the password
        val response = resetUserPassword(resetPasswordInputInfo)

        // then the password was reset successfully
        verify(userServiceMock)
            .resetPassword(resetPasswordInputInfo.email, resetPasswordInputInfo.newPassword, resetPasswordInputInfo.confirmPassword)
        assertEquals(HttpStatus.NO_CONTENT, response.statusCode)
    }

    @Test
    fun `Should throw PasswordsDoNotMatch when resetting a user's password with different passwords`() {
        // given a user (publicTestUser) and different passwords
        val password1 = generateSecurePassword()
        val password2 = generateSecurePassword()

        // mock
        whenever(userServiceMock.resetPassword(publicTestUser.user.email, password1, password2)).thenThrow(PasswordsDoNotMatch())

        // when resetting the password with different passwords
        // then the password cannot be reset and throws PasswordsDoNotMatch exception
        assertFailsWith<PasswordsDoNotMatch> {
            resetUserPassword(resetPasswordInputInfo.copy(newPassword = password1, confirmPassword = password2))
        }
    }
}
