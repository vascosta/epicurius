package epicurius.unit.services.user

import epicurius.domain.exceptions.UserNotFound
import org.mockito.kotlin.verify
import org.mockito.kotlin.whenever
import java.time.LocalDate
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith

class RefreshUserTokenServiceTests : UserServiceTest() {

    @Test
    fun `Should refresh the user's token successfully`() {
        // given the old user token
        val oldToken = "oldToken"

        // mock
        val mockOldTokenHash = userDomain.hashToken(oldToken)
        val mockLastUsed = LocalDate.now()
        val mockNewToken = "newToken"
        val mockNewTokenHash = userDomain.hashToken(mockNewToken)
        whenever(userDomainMock.isToken(oldToken)).thenReturn(true)
        whenever(userDomainMock.hashToken(oldToken)).thenReturn(mockOldTokenHash)
        whenever(jdbiUserRepositoryMock.getUser(tokenHash = mockOldTokenHash)).thenReturn(publicTestUser)
        whenever(jdbiUserRepositoryMock.checkIfUserIsLoggedIn(publicTestUser.id)).thenReturn(false)
        whenever(userDomainMock.generateTokenValue()).thenReturn(mockNewToken)
        whenever(userDomainMock.hashToken(mockNewToken)).thenReturn(mockNewTokenHash)

        // when refreshing the user's token
        val newTokenForUser = refreshUserToken(oldToken)

        // then the new token should be returned
        verify(jdbiTokenRepositoryMock).deleteToken(publicTestUser.id)
        verify(jdbiTokenRepositoryMock).createToken(mockNewTokenHash, mockLastUsed, publicTestUser.id)
        assertEquals(mockNewToken, newTokenForUser)
    }

    @Test
    fun `Should throw UserNotFound exception when refreshing a user's token for an non-existing user`() {
        // given an invalid token
        val invalidToken = "invalidToken"

        // mock
        whenever(userDomainMock.isToken(invalidToken)).thenReturn(true)
        whenever(jdbiUserRepositoryMock.getUser(tokenHash = invalidToken)).thenReturn(null)

        // when refreshing the user's token
        // then the token cannot be refreshed and throws UserNotFound exception
        assertFailsWith<UserNotFound> {
            refreshUserToken(invalidToken)
        }
    }
}
