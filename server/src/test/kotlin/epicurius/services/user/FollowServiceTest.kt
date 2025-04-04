package epicurius.services.user

import epicurius.domain.exceptions.FollowRequestAlreadyBeenSent
import epicurius.domain.exceptions.FollowRequestNotFound
import epicurius.domain.exceptions.UserAlreadyBeingFollowed
import epicurius.domain.exceptions.UserNotFollowed
import epicurius.domain.exceptions.UserNotFound
import epicurius.domain.user.FollowUser
import epicurius.domain.user.FollowingUser
import epicurius.domain.user.User
import epicurius.services.ServiceTest
import epicurius.utils.createTestUser
import org.junit.jupiter.api.BeforeEach
import java.util.UUID
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith
import kotlin.test.assertTrue

class FollowServiceTest : ServiceTest() {

    private lateinit var publicTestUser: User
    private lateinit var privateTestUser: User

    @BeforeEach
    fun setup() {
        publicTestUser = createTestUser(tm)
        privateTestUser = createTestUser(tm, true)
    }

    @Test
    fun `Follow a public user, unfollows him and then retrieve its followers and following successfully`() {
        // given two existing users
        val publicUser = publicTestUser
        val privateUser = privateTestUser

        // when following a public user
        follow(privateUser.id, publicUser.username)

        // then the user is followed successfully
        val publicUserFollowers = getFollowers(publicUser.id)
        val privateUserFollowing = getFollowing(privateUser.id)
        assertTrue(publicUserFollowers.isNotEmpty())
        assertTrue(privateUserFollowing.isNotEmpty())
        assertEquals(1, publicUserFollowers.size)
        assertEquals(1, privateUserFollowing.size)
        assertTrue(publicUserFollowers.contains(FollowUser(privateUser.username, null)))
        assertTrue(privateUserFollowing.contains(FollowingUser(publicUser.username, null)))

        // when unfollowing the user
        unfollow(privateUser.id, publicUser.username)

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
        follow(publicUser.id, privateUser.username)

        // then the follow request is sent successfully
        val privateUserFollowRequests = getFollowRequests(privateUser.id)
        assertTrue(privateUserFollowRequests.isNotEmpty())
        assertEquals(1, privateUserFollowRequests.size)
        assertTrue(privateUserFollowRequests.contains(FollowUser(publicUser.username, null)))

        // when cancelling the follow request
        followRequest(publicUser.id, privateUser.username)

        // then the follow request is cancelled successfully
        val privateUserFollowRequestsAfterCancel = getFollowRequests(privateUser.id)
        assertTrue(privateUserFollowRequestsAfterCancel.isEmpty())
    }

    @Test
    fun `Try to follow a non-existing user and throws UserNotFound Exception`() {
        // given an existing users
        val publicUser = publicTestUser

        // when following a non-existing user
        // then the user cannot be followed and throws UserNotFound Exception
        assertFailsWith<UserNotFound> { follow(publicUser.id, UUID.randomUUID().toString()) }
    }

    @Test
    fun `Try to follow a user twice and throws UserAlreadyBeingFollowed Exception`() {
        // given two existing users
        val publicUser1 = publicTestUser
        val publicUser2 = createTestUser(tm)

        // when following a user twice
        follow(publicUser1.id, publicUser2.username)

        // then the user cannot be followed and throws UserAlreadyBeingFollowed Exception
        assertFailsWith<UserAlreadyBeingFollowed> { follow(publicUser1.id, publicUser2.username) }
    }

    @Test
    fun `Try to follow a private user twice and throws FollowRequestAlreadyBeenSent Exception`() {
        // given two existing users
        val publicUser = publicTestUser
        val privateUser = privateTestUser

        // when trying to follow a private user twice
        follow(publicUser.id, privateUser.username)

        // then another follow request cannot be sent and throws FollowRequestAlreadyBeenSent Exception
        assertFailsWith<FollowRequestAlreadyBeenSent> {
            follow(publicUser.id, privateUser.username)
        }
    }

    @Test
    fun `Try to unfollow a non-existing user and throws UserNotFound Exception`() {
        // given an existing users
        val publicUser = publicTestUser

        // when following a non-existing user
        // then the user cannot be followed and throws UserNotFound Exception
        assertFailsWith<UserNotFound> { unfollow(publicUser.id, UUID.randomUUID().toString()) }
    }

    @Test
    fun `Try to unfollow a user that is not being followed and throws UserNotFollowed Exception`() {
        // given two existing users
        val publicUser = publicTestUser
        val privateUser = privateTestUser

        // when trying to unfollow a user that is not being followed
        // then the user cannot be unfollowed and throws UserNotFollowed Exception
        assertFailsWith<UserNotFollowed> { unfollow(publicUser.id, privateUser.username) }
    }

    @Test
    fun `Try to cancel a follow request to a non-existing user and throws UserNotFound Exception`() {
        // given an existing user
        val publicUser = publicTestUser

        // when cancelling a follow request to a non-existing user
        // then the follow request cannot be cancelled and throws UserNotFound Exception
        assertFailsWith<UserNotFound> { followRequest(publicUser.id, UUID.randomUUID().toString()) }
    }

    @Test
    fun `Try to cancel a non-existing follow request and throws FollowRequestNotFound Exception`() {
        // given an existing user
        val publicUser = publicTestUser
        val privateUsername = privateTestUser.username

        // when cancelling a follow request to a non-existing user
        // then the follow request cannot be cancelled and throws UserNotFound Exception
        assertFailsWith<FollowRequestNotFound> { followRequest(publicUser.id, privateUsername) }
    }
}
