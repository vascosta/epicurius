package epicurius.unit.http.user

import org.mockito.kotlin.verify
import org.springframework.http.HttpStatus
import kotlin.test.Test
import kotlin.test.assertEquals

class LogoutControllerTests : UserHttpTest() {

    @Test
    fun `Should logout a user successfully`() {
        // given a logged-in user (publicTestUser)

        // when logging out
        val response = logout(publicTestUser, mockResponse)

        // then the user is logged out successfully
        verify(mockResponse).addHeader("Authorization", "")
        assertEquals(HttpStatus.NO_CONTENT, response.statusCode)
    }
}
