package epicurius.unit.repository.feed

import epicurius.domain.Diet
import epicurius.domain.Intolerance
import org.junit.jupiter.api.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class GetFeedRepositoryTests : FeedRepositoryTest() {

    val limit = 10

    @Test
    fun `Should retrieve user's empty feed`() {
        // given a user that follows no users

        // when retrieving the feed
        val feed = getFeed(anotherTestUser.user.id, emptyList(), emptyList(), null, limit)

        // then feed should be empty
        assert(feed.isEmpty())
    }

    @Test
    fun `Should retrieve user's feed order by most recent recipe`() {
        // given a user that will follow another user

        // when user follows another user
        followUser(userFollows.user.id, userFollowed.user.id)

        // when retrieving the feed
        val feed = getFeed(userFollows.user.id, emptyList(), emptyList(), null, limit)

        // then feed should contain recipes ordered by most recent
        assertTrue(feed.isNotEmpty())
        assertEquals(recipe2.id, feed.first().id)
        assertEquals(recipe1.id, feed.last().id)
    }

    @Test
    fun `Should retrieve user's feed according to intolerances and diets`() {
        // given a user with intolerances and diets that will follow another user

        // when user follows another user
        followUser(userFollows.user.id, userFollowed.user.id)

        // when retrieving the feed with intolerance and no diets
        val feedWithIntolerances = getFeed(userFollows.user.id, listOf(Intolerance.GLUTEN), emptyList(), null, limit)

        // then feed should be empty
        assertTrue(feedWithIntolerances.isEmpty())

        // when retrieving the feed with diets and no intolerances
        val feedWithDiets = getFeed(userFollows.user.id, emptyList(), listOf(Diet.LACTO_VEGETARIAN), null, limit)

        // then feed should be empty
        assertTrue(feedWithDiets.isNotEmpty())
        assertEquals(recipe2.id, feedWithDiets.first().id)
        assertEquals(recipe1.id, feedWithDiets.last().id)

        // when retrieving the feed with both intolerances and diets
        val feedWithIntolerancesAndDiets = getFeed(
            userFollows.user.id,
            listOf(Intolerance.GLUTEN),
            listOf(Diet.LACTO_VEGETARIAN),
            null,
            limit
        )

        // then feed should be empty
        assertTrue(feedWithIntolerancesAndDiets.isEmpty())
    }
}
