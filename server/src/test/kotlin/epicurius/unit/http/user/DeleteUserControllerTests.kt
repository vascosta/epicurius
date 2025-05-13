package epicurius.unit.http.user

import org.mockito.kotlin.verify
import org.springframework.http.HttpStatus
import kotlin.test.Test
import kotlin.test.assertEquals

class DeleteUserControllerTests: UserHttpTest() {

    @Test
    fun `Should delete a user successfully`() {
        // given an existing user (publicTestUser)

        // when deleting the user
        val response = deleteUser(publicTestUser, mockResponse)

        //
        verify(userServiceMock).deleteUser(publicTestUser.user.id)
        assertEquals(HttpStatus.NO_CONTENT, response.statusCode)
    }
}