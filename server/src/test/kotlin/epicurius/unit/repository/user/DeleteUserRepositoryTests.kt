package epicurius.unit.repository.user

import epicurius.utils.createTestUser
import kotlin.test.Test
import kotlin.test.assertNull

class DeleteUserRepositoryTests: UserRepositoryTest() {

    @Test
    fun `Should delete a user successfully`() {
        // given an existing user
        val user = createTestUser(tm)

        // when deleting the user
        deleteUser(user.id)

        // then the user is deleted successfully
        val deletedUser = getUserById(user.id)
        assertNull(deletedUser)
    }
}