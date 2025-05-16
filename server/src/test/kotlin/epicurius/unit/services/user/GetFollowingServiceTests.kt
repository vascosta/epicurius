package epicurius.unit.services.user

import epicurius.domain.PagingParams
import epicurius.domain.user.SearchUser
import epicurius.repository.jdbi.user.models.SearchUserModel
import org.mockito.kotlin.whenever
import kotlin.test.Test
import kotlin.test.assertEquals

class GetFollowingServiceTests : UserServiceTest() {

    @Test
    fun `Should retrieve the following of an user successfully`() {
        // given a user (publicTestUser)
        val pagingParams = PagingParams()

        // mock
        val mockFollowing = SearchUserModel(privateTestUsername, privateTestUser.profilePictureName)
        val mockFollowings = listOf(mockFollowing)
        whenever(jdbiUserRepositoryMock.getFollowing(publicTestUser.id, pagingParams)).thenReturn(mockFollowings)

        // when retrieving the following of the user
        val followings = getFollowing(publicTestUser.id, pagingParams)

        // then the following are retrieved successfully
        assertEquals(mockFollowings.size, followings.size)
        assertEquals(SearchUser(mockFollowing.name, null), followings.first())
    }
}
