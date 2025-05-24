package epicurius.unit.services.user

import org.mockito.kotlin.verify
import kotlin.test.Test

class DeleteUserServiceTests : UserServiceTest() {

    @Test
    fun `Should delete a user successfully`() {
        // given an existing user (publicTestUser)

        // when deleting the user
        deleteUser(publicTestUser.id)

        // then the user is deleted successfully
        verify(jdbiUserRepositoryMock).deleteUser(publicTestUser.id)
    }
}
