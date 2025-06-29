package epicurius.unit.repository.user

import epicurius.domain.user.FollowingStatus
import epicurius.repository.jdbi.user.models.SearchUserModel
import epicurius.utils.createTestUser
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotNull
import kotlin.test.assertTrue

class FollowRequestRepositoryTests : UserRepositoryTest() {

    @Test
    fun `Should try to follow a private user, get added to its follow requests and then cancel the request successfully`() {
        // given two users
        val publicTestUser = createTestUser(tm)
        val privateTestUser = createTestUser(tm, false)

        // when following a private user
        follow(publicTestUser.user.id, privateTestUser.user.id, FollowingStatus.PENDING.ordinal)

        // then the follow request is sent successfully
        val privateUserFollowRequests = getFollowRequests(privateTestUser.user.id)
        assertTrue(privateUserFollowRequests.isNotEmpty())
        assertEquals(1, privateUserFollowRequests.size)
        assertNotNull(privateUserFollowRequests.firstOrNull { it.id == publicTestUser.user.id })

        // when cancelling the follow request
        cancelFollowRequest(privateTestUser.user.id, publicTestUser.user.id)

        // then the follow request is cancelled successfully
        val privateUserFollowRequestsAfterCancel = getFollowRequests(privateTestUser.user.id)
        assertTrue(privateUserFollowRequestsAfterCancel.isEmpty())
    }

    @Test
    fun `Should try to follow a private user, get added to its follow requests and then get accept successfully`() {
        // given two users
        val publicTestUser = createTestUser(tm)
        val privateTestUser = createTestUser(tm, false)

        // when following a private user
        follow(publicTestUser.user.id, privateTestUser.user.id, FollowingStatus.PENDING.ordinal)

        // then the follow request is sent successfully
        val privateUserFollowRequests = getFollowRequests(privateTestUser.user.id)
        assertTrue(privateUserFollowRequests.isNotEmpty())
        assertEquals(1, privateUserFollowRequests.size)
        assertNotNull(privateUserFollowRequests.firstOrNull { it.id == publicTestUser.user.id })

        // when the private user accepts the follow request
        acceptFollowRequest(privateTestUser.user.id, publicTestUser.user.id)

        // then the follow request is accepted successfully
        val privateUserFollowersAfterAccept = getFollowers(privateTestUser.user.id, null, null, 10)
        assertTrue(
            privateUserFollowersAfterAccept.contains(
                SearchUserModel(
                    publicTestUser.user.id,
                    publicTestUser.user.name,
                    publicTestUser.user.profilePictureName
                )
            )
        )
    }

    @Test
    fun `Should try to follow a private user, get added to its follow requests and then get rejected successfully`() {
        // given two users
        val publicTestUser = createTestUser(tm)
        val privateTestUser = createTestUser(tm, false)

        // when following a private user
        follow(publicTestUser.user.id, privateTestUser.user.id, FollowingStatus.PENDING.ordinal)

        // then the follow request is sent successfully
        val privateUserFollowRequests = getFollowRequests(privateTestUser.user.id)
        assertTrue(privateUserFollowRequests.isNotEmpty())
        assertEquals(1, privateUserFollowRequests.size)
        assertNotNull(privateUserFollowRequests.firstOrNull { it.id == publicTestUser.user.id })

        // when the private user rejects the follow request
        rejectFollowRequest(privateTestUser.user.id, publicTestUser.user.id)

        // then the follow request is rejected successfully
        val privateUserFollowersAfterReject = getFollowers(privateTestUser.user.id, null, null, 10)
        assertTrue(privateUserFollowersAfterReject.isEmpty())
    }
}
