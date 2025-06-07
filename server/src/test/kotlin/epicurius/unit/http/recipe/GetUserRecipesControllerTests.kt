package epicurius.unit.http.recipe

import epicurius.domain.recipe.RecipeInfo
import epicurius.http.controllers.recipe.models.output.GetUserRecipesOutputModel
import kotlinx.coroutines.runBlocking
import org.mockito.kotlin.whenever
import org.springframework.http.HttpStatus
import kotlin.test.Test
import kotlin.test.assertEquals

class GetUserRecipesControllerTests : RecipeControllerTest() {

    @Test
    fun `Should retrieve the user's recipes successfully`() {
        // given a user id
        val limit = 10

        // mock
        val recipeInfoMock = RecipeInfo(
            id = RECIPE_ID,
            name = testRecipe.name,
            authorUsername = testRecipe.authorUsername,
            rating = 0.0,
            cuisine = testRecipe.cuisine,
            mealType = testRecipe.mealType,
            preparationTime = testRecipe.preparationTime,
            servings = testRecipe.servings,
            picture = testRecipe.pictures.first(),
            isInCollection = false
        )
        whenever(
            runBlocking {
                recipeServiceMock.getUserRecipes(testAuthenticatedUser.user.id, null, limit)
            }
        ).thenReturn(listOf(recipeInfoMock))

        // when retrieving the user's recipes
        val response = runBlocking { getUserRecipes(testAuthenticatedUser, null, limit) }
        val body = response.body as GetUserRecipesOutputModel

        // then the recipes are retrieved successfully
        assertEquals(HttpStatus.OK, response.statusCode)
        assertEquals(listOf(recipeInfoMock), body.recipes)
    }
}
