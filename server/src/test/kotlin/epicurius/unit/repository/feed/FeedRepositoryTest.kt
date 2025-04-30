package epicurius.unit.repository.feed

import epicurius.domain.PagingParams
import epicurius.domain.user.FollowingStatus
import epicurius.unit.repository.RepositoryTest
import epicurius.utils.createTestRecipe
import epicurius.utils.createTestUser

open class FeedRepositoryTest : RepositoryTest() {
    companion object {
        val anotherTestUser = createTestUser(tm)

        val userFollows = createTestUser(tm)
        val userFollowed = createTestUser(tm)
        val recipe1 = createTestRecipe(tm, fs, userFollowed)
        val recipe2 = createTestRecipe(tm, fs, userFollowed)

        fun getFeed(userId: Int, pagingParams: PagingParams) =
            tm.run { it.feedRepository.getFeed(userId, pagingParams) }

        fun followUser(userId: Int, userIdToFollow: Int) {
            tm.run { it.userRepository.follow(userId, userIdToFollow, FollowingStatus.ACCEPTED.ordinal) }
        }
    }
}
