package epicurius.unit.repository.feed

import epicurius.domain.PagingParams
import org.junit.jupiter.api.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class GetFeedRepositoryTests : FeedRepositoryTest() {

    @Test
    fun `Should retrieve user's empty feed`() {
        // given a user that follows no users and pagination params
        val pagingParams = PagingParams(0, 10)

        // when retrieving the feed
        val feed = getFeed(anotherTestUser.id, pagingParams)

        // then feed should be empty
        assert(feed.isEmpty())
    }

    @Test
    fun `Should retrieve user's feed order by most recent recipe`() {
        // given a user that will follow another user and pagination params
        val pagingParams = PagingParams(0, 10)

        // when user follows another user
        followUser(userFollows.id, userFollowed.id)

        // when retrieving the feed
        val feed = getFeed(userFollows.id, pagingParams)

        // then feed should contain recipes ordered by most recent
        assertTrue(feed.isNotEmpty())
        assertEquals(recipe2.id, feed.first().id)
        assertEquals(recipe1.id, feed.last().id)
    }
}
