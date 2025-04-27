package epicurius.unit.repository.user

import epicurius.domain.user.FollowingStatus
import epicurius.repository.jdbi.user.models.SearchUserModel
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class FollowRepositoryTests : UserRepositoryTest() {

    @Test
    fun `Should follow a public user, unfollows him and then retrieve its followers and following successfully`() {
        // given two users (publicTestUser and privateTestUser)

        // when following a public user
        follow(privateTestUser.id, publicTestUser.id, FollowingStatus.ACCEPTED.ordinal)

        // then the user is followed successfully
        val publicUserFollowers = getFollowers(publicTestUser.id)
        val privateUserFollowing = getFollowing(privateTestUser.id)
        assertTrue(publicUserFollowers.isNotEmpty())
        assertTrue(privateUserFollowing.isNotEmpty())
        assertEquals(1, publicUserFollowers.size)
        assertEquals(1, privateUserFollowing.size)
        assertTrue(publicUserFollowers.contains(SearchUserModel(privateTestUser.name, privateTestUser.profilePictureName)))
        assertTrue(privateUserFollowing.contains(SearchUserModel(publicTestUser.name, publicTestUser.profilePictureName)))

        // when unfollowing the user
        unfollow(privateTestUser.id, publicTestUser.id)

        // then the user is unfollowed successfully
        val publicUserFollowersAfterUnfollow = getFollowers(publicTestUser.id)
        val privateUserFollowingAfterUnfollow = getFollowing(privateTestUser.id)
        assertTrue(publicUserFollowersAfterUnfollow.isEmpty())
        assertTrue(privateUserFollowingAfterUnfollow.isEmpty())
    }
}
