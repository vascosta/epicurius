package epicurius.unit.http.recipe

import epicurius.domain.Diet
import epicurius.domain.Intolerance
import epicurius.domain.exceptions.InvalidIngredient
import epicurius.domain.exceptions.NotTheRecipeAuthor
import epicurius.domain.exceptions.RecipeNotFound
import epicurius.domain.recipe.Cuisine
import epicurius.domain.recipe.Ingredient
import epicurius.domain.recipe.IngredientUnit
import epicurius.domain.recipe.MealType
import epicurius.domain.user.AuthenticatedUser
import epicurius.domain.user.User
import epicurius.http.controllers.recipe.models.input.UpdateRecipeInputModel
import epicurius.http.controllers.recipe.models.output.UpdateRecipeOutputModel
import epicurius.services.recipe.models.UpdateRecipeModel
import epicurius.utils.generateRandomRecipeDescription
import epicurius.utils.generateRandomRecipeIngredients
import epicurius.utils.generateRandomRecipeInstructions
import epicurius.utils.generateRandomRecipeName
import kotlinx.coroutines.runBlocking
import org.mockito.kotlin.whenever
import org.springframework.http.HttpStatus
import java.time.LocalDate
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith

class UpdateRecipeControllerTests : RecipeHttpTest() {

    private val updateRecipeInfo = UpdateRecipeInputModel(
        generateRandomRecipeName(),
        generateRandomRecipeDescription(),
        1,
        1,
        Cuisine.ASIAN,
        MealType.SOUP,
        setOf(Intolerance.PEANUT),
        setOf(Diet.KETOGENIC),
        generateRandomRecipeIngredients(),
        1,
        1,
        1,
        1,
        generateRandomRecipeInstructions()
    )

    @Test
    fun `Should update a recipe successfully`() {
        // given information to update a recipe (updateRecipeInfo)

        // mock
        val updateRecipeModelMock = UpdateRecipeModel(
            RECIPE_ID,
            updateRecipeInfo.name!!,
            testAuthenticatedUser.user.name,
            LocalDate.now(),
            updateRecipeInfo.description!!,
            1,
            1,
            Cuisine.ASIAN,
            MealType.SOUP,
            listOf(Intolerance.PEANUT),
            listOf(Diet.KETOGENIC),
            updateRecipeInfo.ingredients!!,
            1,
            1,
            1,
            1,
            updateRecipeInfo.instructions!!
        )
        whenever(
            runBlocking { recipeServiceMock.updateRecipe(testAuthenticatedUser.user.id, RECIPE_ID, updateRecipeInfo) }
        ).thenReturn(updateRecipeModelMock)
        whenever(authenticationRefreshHandlerMock.refreshToken(testAuthenticatedUser.token)).thenReturn(mockCookie)

        // when updating the recipe
        val response = runBlocking { updateRecipe(testAuthenticatedUser, RECIPE_ID, updateRecipeInfo, mockResponse) }

        // then the recipe is updated successfully
        assertEquals(HttpStatus.OK, response.statusCode)
        assertEquals(UpdateRecipeOutputModel(updateRecipeModelMock), response.body)
    }

    @Test
    fun `Should throw RecipeNotFound exception when updating a non-existing recipe`() {
        // given a non-existing recipe id
        val nonExistingRecipeId = 9999

        // mock
        whenever(
            runBlocking { recipeServiceMock.updateRecipe(testAuthenticatedUser.user.id, nonExistingRecipeId, updateRecipeInfo) }
        ).thenThrow(RecipeNotFound())

        // when updating the recipe
        // then the recipe is not updated and throws RecipeNotFound exception
        assertFailsWith<RecipeNotFound> {
            runBlocking { updateRecipe(testAuthenticatedUser, nonExistingRecipeId, updateRecipeInfo, mockResponse) }
        }
    }

    @Test
    fun `Should throw NotTheAuthor exception when updating a recipe that does not belong to the user`() {
        // given a user id and a recipe id (RECIPE_ID) that does not belong to him
        val notTheAuthor = AuthenticatedUser(User(9999, "", "", "", "", "", false, emptyList(), emptyList(), ""), "")

        // mock
        whenever(
            runBlocking { recipeServiceMock.updateRecipe(notTheAuthor.user.id, RECIPE_ID, updateRecipeInfo) }
        ).thenThrow(NotTheRecipeAuthor())

        // when updating the recipe
        // then the recipe is not updated and throws NotTheAuthor exception
        assertFailsWith<NotTheRecipeAuthor> {
            runBlocking { updateRecipe(notTheAuthor, RECIPE_ID, updateRecipeInfo, mockResponse) }
        }
    }

    @Test
    fun `Should throw InvalidIngredient exception when updating a recipe with an invalid ingredients`() {
        // given an invalid ingredient
        val invalidIngredient = Ingredient("invalid", 1.0, IngredientUnit.G)

        // mock
        whenever(
            runBlocking {
                recipeServiceMock.updateRecipe(
                    testAuthenticatedUser.user.id,
                    RECIPE_ID,
                    updateRecipeInfo.copy(ingredients = listOf(invalidIngredient))
                )
            }
        ).thenThrow(InvalidIngredient(invalidIngredient.name))

        // when updating the recipe
        // then the recipe is not updated and throws IllegalArgumentException
        assertFailsWith<InvalidIngredient> {
            runBlocking {
                updateRecipe(testAuthenticatedUser, RECIPE_ID, updateRecipeInfo.copy(ingredients = listOf(invalidIngredient)), mockResponse)
            }
        }
    }
}
