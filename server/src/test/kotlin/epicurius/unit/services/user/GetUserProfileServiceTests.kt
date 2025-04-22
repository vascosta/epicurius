package epicurius.unit.services.user

import epicurius.domain.PictureDomain
import epicurius.domain.exceptions.UserNotFound
import org.mockito.kotlin.whenever
import java.util.UUID
import kotlin.test.Test
import kotlin.test.assertContentEquals
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith
import kotlin.test.assertTrue

class GetUserProfileServiceTests : UserServiceTest() {

    @Test
    fun `Should retrieve the user profile successfully`() {
        // given an existing user (testUser)

        // mocks
        whenever(jdbiUserRepositoryMock.getUser(testUsername)).thenReturn(testUser)
        whenever(jdbiUserRepositoryMock.getFollowers(testUser.id)).thenReturn(emptyList())
        whenever(jdbiUserRepositoryMock.getFollowing(testUser.id)).thenReturn(emptyList())
        whenever(pictureRepositoryMock.getPicture(testUser.profilePictureName!!, PictureDomain.USERS_FOLDER))
            .thenReturn(testPicture.bytes)

        // when retrieving the user profile
        val userProfile = getUserProfile(testUsername)

        // then the user profile is retrieved successfully
        assertEquals(testUsername, userProfile.name)
        assertEquals(testUser.country, userProfile.country)
        assertEquals(testUser.privacy, userProfile.privacy)
        assertContentEquals(testPicture.bytes, userProfile.profilePicture)
        assertTrue(userProfile.followers.isEmpty())
        assertTrue(userProfile.following.isEmpty())
    }

    @Test
    fun `Should throw UserNotFound exception when retrieving a non-existing user profile`() {
        // given a non-existing username
        val nonExistingUsername = UUID.randomUUID().toString()

        // mocks
        whenever(jdbiUserRepositoryMock.getUser(nonExistingUsername)).thenReturn(null)

        // when getting the user profile
        // then the user profile cannot be retrieved and throws UserNotFound Exception
        assertFailsWith<UserNotFound> { getUserProfile(nonExistingUsername) }
    }
}
