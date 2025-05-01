package epicurius.unit.repository.feed

import epicurius.domain.Diet
import epicurius.domain.Intolerance
import epicurius.domain.PagingParams
import epicurius.domain.user.FollowingStatus
import epicurius.services.feed.models.GetFeedModel
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

        fun getFeed(
            userId: Int,
            intolerances: List<Intolerance>,
            diets: List<Diet>,
            pagingParams: PagingParams
        ) = tm.run {
            it.feedRepository.getFeed(GetFeedModel(userId, intolerances, diets, pagingParams))
        }

        fun followUser(userId: Int, userIdToFollow: Int) {
            tm.run { it.userRepository.follow(userId, userIdToFollow, FollowingStatus.ACCEPTED.ordinal) }
        }
    }
}
