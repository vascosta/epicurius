package epicurius.unit.http.user

import jakarta.servlet.http.Cookie
import org.mockito.kotlin.verify
import org.springframework.http.HttpStatus
import kotlin.test.Test
import kotlin.test.assertEquals

class DeleteUserControllerTests: UserControllerTest() {

    @Test
    fun `Should delete a user successfully`() {
        // given an existing user (publicTestUser)

        // when deleting the user
        val response = deleteUser(publicTestUser, mockResponse)

        // then the user is deleted successfully
        verify(userServiceMock).deleteUser(publicTestUser.user.id)
        verify(mockResponse).addCookie(Cookie("token", ""))
        assertEquals(HttpStatus.NO_CONTENT, response.statusCode)
    }
}