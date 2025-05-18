package epicurius.unit.repository.user

import epicurius.domain.PagingParams
import epicurius.domain.user.FollowingStatus
import epicurius.repository.jdbi.user.models.SearchUserModel
import epicurius.utils.createTestUser
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class FollowRepositoryTests : UserRepositoryTest() {

    val publicTestUser = createTestUser(tm)
    val privateTestUser = createTestUser(tm, false)

    @Test
    fun `Should follow a public user, unfollows him and then retrieve its followers and following successfully`() {
        // given two users (publicTestUser and privateTestUser)
        val pagingParams = PagingParams()

        // when following a public user
        follow(privateTestUser.user.id, publicTestUser.user.id, FollowingStatus.ACCEPTED.ordinal)

        // then the user is followed successfully
        val publicUserFollowers = getFollowers(publicTestUser.user.id, pagingParams)
        val publicUserFollowersCount = getFollowersCount(publicTestUser.user.id)
        val privateUserFollowing = getFollowing(privateTestUser.user.id, pagingParams)
        val privateUserFollowingCount = getFollowingCount(privateTestUser.user.id)
        assertTrue(publicUserFollowers.isNotEmpty())
        assertTrue(privateUserFollowing.isNotEmpty())
        assertEquals(1, publicUserFollowers.size)
        assertEquals(1, publicUserFollowersCount)
        assertEquals(1, privateUserFollowing.size)
        assertEquals(1, privateUserFollowingCount)
        assertTrue(publicUserFollowers.contains(SearchUserModel(privateTestUser.user.name, privateTestUser.user.profilePictureName)))
        assertTrue(privateUserFollowing.contains(SearchUserModel(publicTestUser.user.name, publicTestUser.user.profilePictureName)))

        // when unfollowing the user
        unfollow(privateTestUser.user.id, publicTestUser.user.id)

        // then the user is unfollowed successfully
        val publicUserFollowersAfterUnfollow = getFollowers(publicTestUser.user.id, pagingParams)
        val publicUserFollowersAfterUnfollowCount = getFollowersCount(publicTestUser.user.id)
        val privateUserFollowingAfterUnfollow = getFollowing(privateTestUser.user.id, pagingParams)
        val privateUserFollowingAfterUnfollowCount = getFollowingCount(privateTestUser.user.id)
        assertTrue(publicUserFollowersAfterUnfollow.isEmpty())
        assertEquals(0, publicUserFollowersAfterUnfollowCount)
        assertTrue(privateUserFollowingAfterUnfollow.isEmpty())
        assertEquals(0, privateUserFollowingAfterUnfollowCount)
    }
}
