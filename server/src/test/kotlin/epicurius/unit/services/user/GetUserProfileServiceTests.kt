package epicurius.unit.services.user

import epicurius.domain.PagingParams
import epicurius.domain.exceptions.UserNotFound
import epicurius.domain.picture.PictureDomain
import org.mockito.kotlin.whenever
import java.util.UUID
import kotlin.test.Test
import kotlin.test.assertContentEquals
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith

class GetUserProfileServiceTests : UserServiceTest() {

    @Test
    fun `Should retrieve the user profile successfully`() {
        // given a user (publicTestUser)
        val pagingParams = PagingParams()

        // mock
        whenever(jdbiUserRepositoryMock.getUser(publicTestUsername)).thenReturn(publicTestUser)
        whenever(jdbiUserRepositoryMock.getFollowers(publicTestUser.id, pagingParams)).thenReturn(emptyList())
        whenever(jdbiUserRepositoryMock.getFollowing(publicTestUser.id, pagingParams)).thenReturn(emptyList())
        whenever(pictureRepositoryMock.getPicture(publicTestUser.profilePictureName!!, PictureDomain.USERS_FOLDER))
            .thenReturn(testPicture.bytes)

        // when retrieving the user profile
        val userProfile = getUserProfile(publicTestUsername)

        // then the user profile is retrieved successfully
        assertEquals(publicTestUsername, userProfile.name)
        assertEquals(publicTestUser.country, userProfile.country)
        assertEquals(publicTestUser.privacy, userProfile.privacy)
        assertContentEquals(testPicture.bytes, userProfile.profilePicture)
        assertEquals(0, userProfile.followersCount)
        assertEquals(0, userProfile.followingCount)
    }

    @Test
    fun `Should throw UserNotFound exception when retrieving a non-existing user profile`() {
        // given a non-existing username
        val nonExistingUsername = UUID.randomUUID().toString()

        // mock
        whenever(jdbiUserRepositoryMock.getUser(nonExistingUsername)).thenReturn(null)

        // when getting the user profile
        // then the user profile cannot be retrieved and throws UserNotFound exception
        assertFailsWith<UserNotFound> { getUserProfile(nonExistingUsername) }
    }
}
