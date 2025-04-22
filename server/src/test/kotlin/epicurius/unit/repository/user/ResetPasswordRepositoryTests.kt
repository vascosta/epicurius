package epicurius.unit.repository.user

import epicurius.utils.createTestUser
import java.util.UUID
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotEquals
import kotlin.test.assertNotNull

class ResetPasswordRepositoryTests : UserRepositoryTest() {

    private val testUser = createTestUser(tm)

    @Test
    fun `Should reset an user's password and then retrieve it successfully`() {
        // given a user (testUser)

        // when resetting the password
        val newPassword = UUID.randomUUID().toString()
        val newPasswordHash = userDomain.encodePassword(newPassword)
        resetPassword(testUser.email, newPasswordHash)

        // when getting the user by name
        val userAfterResetPassword = getUserByName(testUser.name)

        // then the password is reset successfully
        assertNotNull(userAfterResetPassword)
        assertEquals(testUser.name, userAfterResetPassword.name)
        assertEquals(testUser.email, userAfterResetPassword.email)
        assertEquals(newPasswordHash, userAfterResetPassword.passwordHash)
        assertNotEquals(testUser.passwordHash, userAfterResetPassword.passwordHash)
    }
}
