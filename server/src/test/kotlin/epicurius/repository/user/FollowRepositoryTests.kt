package epicurius.repository.user

import epicurius.domain.FollowingStatus
import epicurius.domain.user.SearchUserModel
import epicurius.domain.user.User
import epicurius.repository.RepositoryTest
import epicurius.utils.createTestUser
import org.junit.jupiter.api.BeforeEach
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue

class FollowRepositoryTests : RepositoryTest() {

    private lateinit var publicTestUser: User
    private lateinit var privateTestUser: User

    @BeforeEach
    fun setup() {
        publicTestUser = createTestUser(tm)
        privateTestUser = createTestUser(tm, false)
    }

    @Test
    fun `Follow a public user, unfollows him and then retrieve its followers and following successfully`() {
        // given two existing users
        val publicUser = publicTestUser
        val privateUser = privateTestUser

        // when following a public user
        follow(privateUser.id, publicUser.id, FollowingStatus.ACCEPTED.ordinal)

        // then the user is followed successfully
        val publicUserFollowers = getFollowers(publicUser.id)
        val privateUserFollowing = getFollowing(privateUser.id)
        assertTrue(publicUserFollowers.isNotEmpty())
        assertTrue(privateUserFollowing.isNotEmpty())
        assertEquals(1, publicUserFollowers.size)
        assertEquals(1, privateUserFollowing.size)
        assertTrue(publicUserFollowers.contains(SearchUserModel(privateUser.username, privateUser.profilePictureName)))
        assertTrue(privateUserFollowing.contains(SearchUserModel(publicUser.username, publicUser.profilePictureName)))

        // when unfollowing the user
        unfollow(privateUser.id, publicUser.id)

        // then the user is unfollowed successfully
        val publicUserFollowersAfterUnfollow = getFollowers(publicUser.id)
        val privateUserFollowingAfterUnfollow = getFollowing(privateUser.id)
        assertTrue(publicUserFollowersAfterUnfollow.isEmpty())
        assertTrue(privateUserFollowingAfterUnfollow.isEmpty())
    }

    @Test
    fun `Try to follow a private user, get added to its follow requests and then cancel the request successfully`() {
        // given two existing users
        val publicUser = publicTestUser
        val privateUser = privateTestUser

        // when following a private user
        follow(publicUser.id, privateUser.id, FollowingStatus.PENDING.ordinal)

        // then the follow request is sent successfully
        val privateUserFollowRequests = getFollowRequests(privateUser.id)
        assertTrue(privateUserFollowRequests.isNotEmpty())
        assertEquals(1, privateUserFollowRequests.size)
        assertTrue(privateUserFollowRequests.contains(SearchUserModel(publicUser.username, publicUser.profilePictureName)))

        // when cancelling the follow request
        cancelFollowRequest(privateUser.id, publicUser.id)

        // then the follow request is cancelled successfully
        val privateUserFollowRequestsAfterCancel = getFollowRequests(privateUser.id)
        assertTrue(privateUserFollowRequestsAfterCancel.isEmpty())
    }

    @Test
    fun `Check if an user is being followed by other user successfully`() {
        // given 2 existing users
        val publicUser = publicTestUser
        val privateUser = privateTestUser

        // when checking if the user is being followed by the other user
        val userBeingFollowedBy = checkIfUserIsBeingFollowedBy(privateUser.id, publicUser.id)

        // then the user is not being followed by the other user
        assertFalse(userBeingFollowedBy)
    }

    @Test
    fun `Check if an user already sent a follow request to other user successfully`() {
        // given 2 existing users
        val privateUser = privateTestUser
        val privateUser2 = createTestUser(tm, true)
        follow(privateUser.id, privateUser2.id, FollowingStatus.PENDING.ordinal)

        // when checking if the user already sent a follow request to the other user
        val userAlreadySentFollowRequest = checkIfUserAlreadySentFollowRequest(privateUser2.id, privateUser.id)

        // then a follow request was already sent
        assertTrue(userAlreadySentFollowRequest)
    }
}
