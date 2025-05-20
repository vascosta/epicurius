package epicurius.integration.user

import epicurius.domain.exceptions.PasswordsDoNotMatch
import epicurius.domain.exceptions.UserNotFound
import epicurius.http.utils.Problem
import epicurius.http.utils.Uris
import epicurius.integration.utils.getBody
import epicurius.integration.utils.patch
import epicurius.utils.createTestUser
import epicurius.utils.generateEmail
import epicurius.utils.generateSecurePassword
import org.springframework.http.HttpStatus
import kotlin.test.Test
import kotlin.test.assertEquals

class ResetUserPasswordIntegrationTests: UserIntegrationTest() {

    private val testUser = createTestUser(tm)
    val testPassword = generateSecurePassword()

    @Test
    fun `Should reset a user's password successfully with code 204`() {
        // given a user (testUser) and a new password (testPassword)

        // when resetting the password
        // then the password was reset successfully
        resetUserPassword(testUser.user.email, testPassword, testPassword)
    }

    @Test
    fun `Should fail with code 404 when resetting a user's password for a non-existing user`() {
        // given a non-existing user email
        val nonExistingUserEmail = generateEmail("nonexistinguser")

        // when resetting the password
        val error = patch<Problem>(
            client,
            api(Uris.User.USER_RESET_PASSWORD),
            body = mapOf(
                "email" to nonExistingUserEmail,
                "newPassword" to testPassword,
                "confirmPassword" to testPassword
            ),
            responseStatus = HttpStatus.NOT_FOUND,
            token = testUser.token
        )

        // then the password cannot be reset and fails with code 404
        val errorBody = getBody(error)
        assertEquals(UserNotFound(nonExistingUserEmail).message, errorBody.detail)
    }

    @Test
    fun `Should fail with code 400 when resetting a user's password with different passwords`() {
        // given a user (testUser) and different passwords
        val password1 = generateSecurePassword()
        val password2 = generateSecurePassword()

        // when resetting the password
        val error = patch<Problem>(
            client,
            api(Uris.User.USER_RESET_PASSWORD),
            body = mapOf(
                "email" to testUser.user.email,
                "newPassword" to password1,
                "confirmPassword" to password2
            ),
            responseStatus = HttpStatus.BAD_REQUEST,
            token = testUser.token
        )

        // then the password cannot be reset and fails with code 400
        val errorBody = getBody(error)
        assertEquals(PasswordsDoNotMatch().message, errorBody.detail)
    }
}