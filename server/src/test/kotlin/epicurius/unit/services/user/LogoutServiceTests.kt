package epicurius.unit.services.user

import org.mockito.kotlin.verify
import kotlin.test.Test

class LogoutServiceTests : UserServiceTest() {

    @Test
    fun `Should logout a user successfully`() {
        // given a logged-in user (publicTestUser)

        // when logging out
        logout(publicTestUsername)

        // then the user is logged out successfully
        verify(jdbiTokenRepositoryMock).deleteToken(publicTestUsername)
    }
}
