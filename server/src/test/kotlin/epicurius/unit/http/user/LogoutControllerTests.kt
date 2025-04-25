package epicurius.unit.http.user

import org.mockito.kotlin.verify
import kotlin.test.Test

class LogoutControllerTests: UserHttpTest() {

    @Test
    fun `Should logout a user successfully`() {
        // given a logged-in user (publicTestUser)

        // when logging out
        logout(publicTestUser, mockResponse)

        // then the user is logged out successfully
        verify(mockResponse).addHeader("Authorization", "")
    }
}