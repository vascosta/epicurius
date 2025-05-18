package epicurius.unit.repository.user

import epicurius.domain.user.FollowingStatus
import epicurius.repository.jdbi.user.models.SearchUserModel
import epicurius.utils.createTestUser
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class CancelFollowRequestRepositoryTests : UserRepositoryTest() {

    val publicTestUser = createTestUser(tm)
    val privateTestUser = createTestUser(tm, false)

    @Test
    fun `Should try to follow a private user, get added to its follow requests and then cancel the request successfully`() {
        // given two users (publicTestUser and privateTestUser)

        // when following a private user
        follow(publicTestUser.user.id, privateTestUser.user.id, FollowingStatus.PENDING.ordinal)

        // then the follow request is sent successfully
        val privateUserFollowRequests = getFollowRequests(privateTestUser.user.id)
        assertTrue(privateUserFollowRequests.isNotEmpty())
        assertEquals(1, privateUserFollowRequests.size)
        assertTrue(privateUserFollowRequests.contains(SearchUserModel(publicTestUser.user.name, publicTestUser.user.profilePictureName)))

        // when cancelling the follow request
        cancelFollowRequest(privateTestUser.user.id, publicTestUser.user.id)

        // then the follow request is cancelled successfully
        val privateUserFollowRequestsAfterCancel = getFollowRequests(privateTestUser.user.id)
        assertTrue(privateUserFollowRequestsAfterCancel.isEmpty())
    }
}
