package epicurius.integration.recipe

import epicurius.utils.createTestRecipe
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotNull
import kotlin.test.assertTrue

class GetUserRecipesIntegrationTests : RecipeIntegrationTest() {

    @Test
    fun `Should retrieve the user's recipes successfully`() {
        // given an authenticated user and a limit for the number of recipes to retrieve
        createTestRecipe(tm, testAuthor.user)
        val limit = 10

        // when retrieving the user's recipes
        val response = getUserRecipes(testAuthor.token, null, limit)

        // then the recipes are retrieved successfully
        assertNotNull(response)
        assertTrue(response.recipes.isNotEmpty())
        assertEquals(2, response.recipes.size)
        assertTrue(response.recipes.all { it.authorUsername == testAuthor.user.name })
    }
}
