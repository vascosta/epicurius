package epicurius.unit.http.user

import epicurius.domain.PagingParams
import epicurius.domain.exceptions.UserNotFound
import epicurius.domain.user.UserProfile
import epicurius.http.controllers.user.models.output.GetUserProfileOutputModel
import org.mockito.kotlin.whenever
import org.springframework.http.HttpStatus
import java.util.UUID
import kotlin.test.Test
import kotlin.test.assertContentEquals
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith

class GetUserProfileControllerTests : UserControllerTest() {

    @Test
    fun `Should retrieve the user profile successfully`() {
        // given a user (publicTestUser)
        val pagingParams = PagingParams()

        // mock
        whenever(userServiceMock.getProfilePicture(publicTestUser.user.profilePictureName)).thenReturn(testPicture.bytes)
        whenever(userServiceMock.getFollowers(publicTestUser.user.id, pagingParams)).thenReturn(emptyList())
        whenever(userServiceMock.getFollowing(publicTestUser.user.id, pagingParams)).thenReturn(emptyList())

        // when retrieving the user profile
        val response = getUserProfile(publicTestUser, publicTestUsername)
        val body = response.body as GetUserProfileOutputModel

        // then the user profile is retrieved successfully
        assertEquals(HttpStatus.OK, response.statusCode)
        assertEquals(publicTestUsername, body.userProfile.name)
        assertEquals(publicTestUser.user.country, body.userProfile.country)
        assertEquals(publicTestUser.user.privacy, body.userProfile.privacy)
        assertContentEquals(testPicture.bytes, body.userProfile.profilePicture)
        assertEquals(0, body.userProfile.followersCount)
        assertEquals(0, body.userProfile.followingCount)
    }

    @Test
    fun `Should retrieve another user profile successfully`() {
        // given two users (publicTestUser and privateTestUser)

        // mock
        val mockUserProfile = UserProfile(
            privateTestUsername,
            privateTestUser.user.country,
            privateTestUser.user.privacy,
            testPicture.bytes,
            0,
            0
        )
        whenever(userServiceMock.getUserProfile(privateTestUsername)).thenReturn(mockUserProfile)

        // when retrieving the other user profile
        val response = getUserProfile(publicTestUser, privateTestUsername)
        val body = response.body as GetUserProfileOutputModel

        // then the user profile is retrieved successfully
        assertEquals(HttpStatus.OK, response.statusCode)
        assertEquals(mockUserProfile.name, body.userProfile.name)
        assertEquals(mockUserProfile.country, body.userProfile.country)
        assertEquals(mockUserProfile.privacy, body.userProfile.privacy)
        assertContentEquals(mockUserProfile.profilePicture, body.userProfile.profilePicture)
        assertEquals(mockUserProfile.followersCount, body.userProfile.followersCount)
        assertEquals(mockUserProfile.followingCount, body.userProfile.followingCount)
    }

    @Test
    fun `Should throw UserNotFound exception when retrieving a non-existing user profile`() {
        // given a user (publicTestUser) and non-existing username
        val nonExistingUsername = UUID.randomUUID().toString()

        // mock
        whenever(userServiceMock.getUserProfile(nonExistingUsername)).thenThrow(UserNotFound(nonExistingUsername))

        // when getting the user profile
        // then the user profile cannot be retrieved and throws UserNotFound exception
        assertFailsWith<UserNotFound> { getUserProfile(publicTestUser, nonExistingUsername) }
    }
}
