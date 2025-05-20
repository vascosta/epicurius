package epicurius.unit.services.user

import epicurius.domain.user.SearchUser
import epicurius.repository.jdbi.user.models.SearchUserModel
import org.mockito.kotlin.whenever
import kotlin.test.Test
import kotlin.test.assertEquals

class GetFollowRequestsServiceTests : UserServiceTest() {

    @Test
    fun `Should retrieve the follow requests of an user successfully`() {
        // given a user (publicTestUser)

        // mock
        val mockFollowing = SearchUserModel(privateTestUsername, privateTestUser.profilePictureName)
        val mockFollowings = listOf(mockFollowing)
        whenever(jdbiUserRepositoryMock.getFollowRequests(publicTestUser.id)).thenReturn(mockFollowings)

        // when retrieving the follow requests of the user
        val followings = getFollowRequests(publicTestUser.id)

        // then the following are retrieved successfully
        assertEquals(mockFollowings.size, followings.size)
        assertEquals(SearchUser(mockFollowing.name, null), followings.first())
    }
}
