package epicurius.unit.repository.recipe

import epicurius.domain.PagingParams
import epicurius.utils.createTestRecipe
import epicurius.utils.createTestUser
import kotlin.test.Test
import kotlin.test.assertNotNull
import kotlin.test.assertTrue

class GetUserRecipesRepositoryTests : RecipeRepositoryTest() {

    @Test
    fun `Should retrieve the user's recipes successfully`() {
        // given a user with recipes
        val user = createTestUser(tm)
        val userRecipe = createTestRecipe(tm, fs, user.user)

        // when retrieving the user's recipes
        val retrievedRecipes = getUserRecipes(user.user.id, PagingParams())

        // then the recipes are retrieved successfully
        assertNotNull(retrievedRecipes.find { it.id == userRecipe.id })
    }

    @Test
    fun `Should retrieve an empty list when the user has no recipes successfully`() {
        // given a user with recipes
        val user = createTestUser(tm)

        // when retrieving the user's recipes
        val retrievedRecipes = getUserRecipes(user.user.id, PagingParams())

        // then the recipes are retrieved successfully
        assertTrue(retrievedRecipes.isEmpty())
    }
}
