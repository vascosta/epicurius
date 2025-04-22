package epicurius.unit.services.user

import epicurius.domain.exceptions.InvalidToken
import epicurius.domain.user.AuthenticatedUser
import org.mockito.kotlin.whenever
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith
import kotlin.test.assertNotNull
import kotlin.test.assertNull

class GetAuthenticatedUserServiceTests : UserServiceTest() {

    private val testUserToken = userDomain.generateTokenValue()

    @Test
    fun `Should retrieve an authenticated user successfully`() {
        // given a user's token (testUserToken)

        // mock
        val mockTokenHash = userDomain.hashToken(testUserToken)
        whenever(userDomainMock.isToken(testUserToken)).thenReturn(true)
        whenever(userDomainMock.hashToken(testUserToken)).thenReturn(mockTokenHash)
        whenever(jdbiUserRepositoryMock.getUser(tokenHash = mockTokenHash)).thenReturn(testUser)

        // when retrieving the authenticated user
        val authenticatedUser = getAuthenticatedUser(testUserToken)

        // then the user is retrieved successfully
        assertNotNull(authenticatedUser)
        assertEquals(AuthenticatedUser(testUser, testUserToken), authenticatedUser)
    }

    @Test
    fun `Should retrieve null when the user is not found`() {
        // given a valid token
        val token = userDomain.generateTokenValue()

        // mock
        val mockTokenHash = userDomain.hashToken(token)
        whenever(userDomainMock.isToken(token)).thenReturn(true)
        whenever(userDomainMock.hashToken(token)).thenReturn(mockTokenHash)
        whenever(jdbiUserRepositoryMock.getUser(tokenHash = mockTokenHash)).thenReturn(null)

        // when retrieving the authenticated user
        val authenticatedUser = getAuthenticatedUser(token)

        // then the user is not retrieved
        assertNull(authenticatedUser)
    }

    @Test
    fun `Should throw InvalidToken exception when retrieving an authenticated user with an invalid token`() {
        // given an invalid token
        val invalidToken = "invalidToken"

        // mock
        whenever(userDomainMock.isToken(invalidToken)).thenReturn(false)

        // when retrieving the authenticated user
        // then the user is not retrieved and throws InvalidToken exception
        assertFailsWith<InvalidToken> { getAuthenticatedUser(invalidToken) }
    }
}
