package epicurius.unit.http.recipe

import epicurius.domain.Diet
import epicurius.domain.Intolerance
import epicurius.domain.exceptions.NotTheAuthor
import epicurius.domain.exceptions.RecipeNotFound
import epicurius.domain.recipe.Cuisine
import epicurius.domain.recipe.MealType
import epicurius.domain.user.AuthenticatedUser
import epicurius.domain.user.User
import epicurius.http.recipe.models.input.UpdateRecipeInputModel
import epicurius.http.recipe.models.output.UpdateRecipeOutputModel
import epicurius.services.recipe.models.UpdateRecipeModel
import epicurius.utils.generateRandomRecipeDescription
import epicurius.utils.generateRandomRecipeIngredients
import epicurius.utils.generateRandomRecipeInstructions
import epicurius.utils.generateRandomRecipeName
import kotlinx.coroutines.runBlocking
import org.mockito.kotlin.whenever
import org.springframework.http.HttpStatus
import java.time.Instant
import java.util.Date
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith

class UpdateRecipeHttpTests : RecipeHttpTest() {

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
            Date.from(Instant.now()),
            updateRecipeInfo.description!!,
            1,
            1,
            Cuisine.ASIAN,
            MealType.SOUP,
            setOf(Intolerance.PEANUT),
            setOf(Diet.KETOGENIC),
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

        // when updating the recipe
        val response = runBlocking { updateRecipe(testAuthenticatedUser, RECIPE_ID, updateRecipeInfo) }

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
        val exception = assertFailsWith<RecipeNotFound> {
            runBlocking { updateRecipe(testAuthenticatedUser, nonExistingRecipeId, updateRecipeInfo) }
        }

        // then an exception is thrown
        assertEquals(RecipeNotFound().message, exception.message)
    }

    @Test
    fun `Should throw NotTheAuthor exception when updating a recipe that does not belong to the user`() {
        // given a user id and a recipe id (RECIPE_ID) that does not belong to him
        val notTheAuthorUser = AuthenticatedUser(
            User(9999, "", "", "", "", "", false, emptySet(), emptySet(), ""),
            ""
        )

        // mock
        whenever(
            runBlocking { recipeServiceMock.updateRecipe(notTheAuthorUser.user.id, RECIPE_ID, updateRecipeInfo) }
        ).thenThrow(NotTheAuthor())

        // when updating the recipe
        val exception = assertFailsWith<NotTheAuthor> {
            runBlocking { updateRecipe(notTheAuthorUser, RECIPE_ID, updateRecipeInfo) }
        }

        // then an exception is thrown
        assertEquals(NotTheAuthor().message, exception.message)
    }
}
