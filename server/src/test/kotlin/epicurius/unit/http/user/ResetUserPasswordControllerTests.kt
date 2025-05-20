package epicurius.unit.http.user

import epicurius.domain.exceptions.PasswordsDoNotMatch
import epicurius.domain.exceptions.UserNotFound
import epicurius.http.controllers.user.models.input.ResetPasswordInputModel
import epicurius.unit.services.ServiceTest.Companion.resetPassword
import epicurius.utils.generateEmail
import epicurius.utils.generateSecurePassword
import org.mockito.kotlin.verify
import org.mockito.kotlin.whenever
import org.springframework.http.HttpStatus
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith

class ResetUserPasswordControllerTests : UserControllerTest() {

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
    fun `Should throw UserNotFound when resetting a user's password for a non-existing user`() {
        // given a non-existing user email
        val nonExistingUserEmail = generateEmail("nonexistinguser")

        // mock
        whenever(userServiceMock.resetPassword(nonExistingUserEmail, password, password)).thenThrow(UserNotFound(nonExistingUserEmail))

        // when resetting the password
        // then the password cannot be reset and throws UserNotFound exception
        assertFailsWith<UserNotFound> {
            resetUserPassword(resetPasswordInputInfo.copy(email = nonExistingUserEmail))
        }
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
