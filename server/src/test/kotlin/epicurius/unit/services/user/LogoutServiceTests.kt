package epicurius.unit.services.user

import org.mockito.kotlin.verify
import kotlin.test.Test

class LogoutServiceTests: UserServiceTest() {

    @Test
    fun `Should logout a user successfully`() {
        // given an existing logged-in user (testUser)

        // when logging out
        // then the user is logged out successfully
        logout(testUsername)
        verify(jdbiTokenRepositoryMock).deleteToken(testUsername)
    }
}