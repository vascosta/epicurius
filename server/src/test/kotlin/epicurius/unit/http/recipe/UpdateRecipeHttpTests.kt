package epicurius.unit.http.recipe

import epicurius.domain.Diet
import epicurius.domain.Intolerance
import epicurius.domain.recipe.*
import epicurius.http.recipe.models.output.UpdateRecipeOutputModel
import epicurius.services.recipe.models.UpdateRecipeModel
import kotlinx.coroutines.runBlocking
import org.mockito.kotlin.whenever
import org.springframework.http.HttpStatus
import java.time.Instant
import java.util.*
import kotlin.test.Test
import kotlin.test.assertEquals

class UpdateRecipeHttpTests: RecipeHttpTest() {

    @Test
    fun `Should update a recipe successfully`() {
        // given information to update a recipe (updateRecipeInfo)

        //
        val updateRecipeModelMock = UpdateRecipeModel(
            RECIPE_ID,
            "name",
            authenticatedUser.user.name,
            Date.from(Instant.now()),
            "description",
            1,
            1,
            Cuisine.ASIAN,
            MealType.SOUP,
            listOf(Intolerance.PEANUT),
            listOf(Diet.KETOGENIC),
            listOf(
                Ingredient("Ingredient1", 1, IngredientUnit.TSP),
                Ingredient("Ingredient2", 1, IngredientUnit.TSP)
            ),
            1,
            1,
            1,
            1,
            Instructions(mapOf("1" to "Step1", "2" to "Step2"))
        )
        whenever(runBlocking {
            recipeServiceMock.updateRecipe(authenticatedUser.user.id, RECIPE_ID, updateRecipeInfo)
        }).thenReturn(updateRecipeModelMock)

        // when updating the recipe
        val response = runBlocking { recipeController.updateRecipe(authenticatedUser, RECIPE_ID, updateRecipeInfo) }

        // then the recipe is updated successfully
        assertEquals(HttpStatus.OK, response.statusCode)
        assertEquals(UpdateRecipeOutputModel(updateRecipeModelMock), response.body)
    }
}