package epicurius.unit.repository.user

import epicurius.domain.PagingParams
import epicurius.domain.user.FollowingStatus
import epicurius.repository.jdbi.user.models.SearchUserModel
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class FollowRepositoryTests : UserRepositoryTest() {

    @Test
    fun `Should follow a public user, unfollows him and then retrieve its followers and following successfully`() {
        // given two users (publicTestUser and privateTestUser)
        val pagingParams = PagingParams()

        // when following a public user
        follow(privateTestUser.id, publicTestUser.id, FollowingStatus.ACCEPTED.ordinal)

        // then the user is followed successfully
        val publicUserFollowers = getFollowers(publicTestUser.id, pagingParams)
        val publicUserFollowersCount = getFollowersCount(publicTestUser.id)
        val privateUserFollowing = getFollowing(privateTestUser.id, pagingParams)
        val privateUserFollowingCount = getFollowingCount(privateTestUser.id)
        assertTrue(publicUserFollowers.isNotEmpty())
        assertTrue(privateUserFollowing.isNotEmpty())
        assertEquals(1, publicUserFollowers.size)
        assertEquals(1, publicUserFollowersCount)
        assertEquals(1, privateUserFollowing.size)
        assertEquals(1, privateUserFollowingCount)
        assertTrue(publicUserFollowers.contains(SearchUserModel(privateTestUser.name, privateTestUser.profilePictureName)))
        assertTrue(privateUserFollowing.contains(SearchUserModel(publicTestUser.name, publicTestUser.profilePictureName)))

        // when unfollowing the user
        unfollow(privateTestUser.id, publicTestUser.id)

        // then the user is unfollowed successfully
        val publicUserFollowersAfterUnfollow = getFollowers(publicTestUser.id, pagingParams)
        val publicUserFollowersAfterUnfollowCount = getFollowersCount(publicTestUser.id)
        val privateUserFollowingAfterUnfollow = getFollowing(privateTestUser.id, pagingParams)
        val privateUserFollowingAfterUnfollowCount = getFollowingCount(privateTestUser.id)
        assertTrue(publicUserFollowersAfterUnfollow.isEmpty())
        assertEquals(0, publicUserFollowersAfterUnfollowCount)
        assertTrue(privateUserFollowingAfterUnfollow.isEmpty())
        assertEquals(0, privateUserFollowingAfterUnfollowCount)
    }
}
