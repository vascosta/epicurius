package epicurius.integration.feed

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotNull
import kotlin.test.assertTrue

class GetFeedIntegrationTests : FeedIntegrationTest() {

    @Test
    fun `Should retrieve user's empty feed successfully with code 200`() {
        // given a user (testUser))

        // when getting the user's feed
        val feedBody = getFeed(testUser.token)

        // then the feed should contain any recipes
        assertNotNull(feedBody)
        assertTrue(feedBody.feed.isEmpty())
    }

    @Test
    fun `Should retrieve user's feed with test recipe successfully with code 200`() {
        // given a user (testUser) and a recipe created by another user (testAuthorUser)

        // when the test user follows the author of the recipe
        follow(testUser.token, testAuthorUser.user.name)

        // and getting the user's feed
        val feedBody = getFeed(testUser.token)

        // then the feed should contain the test recipe
        assertNotNull(feedBody)
        assertTrue(feedBody.feed.isNotEmpty())
        assertEquals(testRecipe.id, feedBody.feed[0].id)
        assertEquals(testRecipe.name, feedBody.feed[0].name)
        assertEquals(testRecipe.servings, feedBody.feed[0].servings)
        assertEquals(testRecipe.mealType, feedBody.feed[0].mealType)
        assertEquals(testRecipe.cuisine, feedBody.feed[0].cuisine)
    }
}
