package epicurius.unit.services.user

import epicurius.domain.PagingParams
import epicurius.domain.user.SearchUser
import epicurius.repository.jdbi.user.models.SearchUserModel
import org.mockito.kotlin.whenever
import kotlin.test.Test
import kotlin.test.assertEquals

class GetFollowersServiceTests : UserServiceTest() {

    @Test
    fun `Should retrieve the followers of an user successfully`() {
        // given a user (publicTestUser)
        val pagingParams = PagingParams()

        // mock
        val mockFollower = SearchUserModel(privateTestUsername, privateTestUser.profilePictureName)
        val mockFollowers = listOf(mockFollower)
        whenever(jdbiUserRepositoryMock.getFollowers(publicTestUser.id, pagingParams)).thenReturn(mockFollowers)

        // when retrieving the followers of the user
        val followers = getFollowers(publicTestUser.id, pagingParams)

        // then the followers are retrieved successfully
        assertEquals(mockFollowers.size, followers.size)
        assertEquals(SearchUser(mockFollower.name, null), followers.first())
    }
}
