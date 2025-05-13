package epicurius.unit.http.recipe

import epicurius.domain.exceptions.RecipeNotAccessible
import epicurius.domain.exceptions.RecipeNotFound
import epicurius.domain.user.AuthenticatedUser
import epicurius.domain.user.User
import epicurius.http.controllers.recipe.models.output.GetRecipeOutputModel
import kotlinx.coroutines.runBlocking
import org.mockito.kotlin.whenever
import org.springframework.http.HttpStatus
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith

class GetRecipeControllerTests : RecipeHttpTest() {

    @Test
    fun `Should the author retrieve the recipe successfully`() {
        // given a recipe id (RECIPE_ID)

        // mock
        whenever(runBlocking { recipeServiceMock.getRecipe(RECIPE_ID, testAuthorAuthenticatedUser.user.name) }).thenReturn(testRecipe)

        // when retrieving the recipe
        val response = runBlocking { getRecipe(testAuthorAuthenticatedUser, RECIPE_ID) }
        val body = response.body as GetRecipeOutputModel

        // then the recipe is retrieved successfully
        assertEquals(HttpStatus.OK, response.statusCode)
        assertEquals(testRecipe, body.recipe)
    }

    @Test
    fun `Should a follower of the author retrieve the recipe successfully`() {
        // given a recipe id and a user following the author (RECIPE_ID, testAuthenticatedUser)

        // mock
        whenever(runBlocking { recipeServiceMock.getRecipe(RECIPE_ID, testAuthenticatedUser.user.name) }).thenReturn(testRecipe)

        // when retrieving the recipe
        val response = runBlocking { getRecipe(testAuthenticatedUser, RECIPE_ID) }
        val body = response.body as GetRecipeOutputModel

        // then the recipe is retrieved successfully
        assertEquals(HttpStatus.OK, response.statusCode)
        assertEquals(testRecipe, body.recipe)
    }

    @Test
    fun `Should throw RecipeNotFound exception when retrieving a non-existing recipe`() {
        // given a non-existing recipe id
        val nonExistingRecipeId = 9999

        // mock
        whenever(runBlocking { recipeServiceMock.getRecipe(nonExistingRecipeId, testAuthenticatedUser.user.name) }).thenThrow(RecipeNotFound())

        // when retrieving the recipe
        // then the recipe is not retrieved and throws RecipeNotFound exception
        assertFailsWith<RecipeNotFound> { runBlocking { getRecipe(testAuthenticatedUser, nonExistingRecipeId) } }
    }

    @Test
    fun `Should throw RecipeNotAccessible exception when retrieving a recipe from a private user not followed`() {
        // given a user not following the author and a recipe id (RECIPE_ID)
        val user = AuthenticatedUser(User(9999, "user", "", "", "", "", false, emptyList(), emptyList(), ""), "")

        // mock
        whenever(runBlocking { recipeServiceMock.getRecipe(RECIPE_ID, user.user.name) }).thenThrow(RecipeNotAccessible())

        // when retrieving the recipe
        // then the recipe is not retrieved and throws RecipeNotAccessible exception
        assertFailsWith<RecipeNotAccessible> { runBlocking { getRecipe(user, RECIPE_ID) } }
    }
}
