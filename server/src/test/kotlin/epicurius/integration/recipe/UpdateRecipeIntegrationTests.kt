package epicurius.integration.recipe

import epicurius.domain.Diet
import epicurius.domain.Intolerance
import epicurius.domain.exceptions.InvalidIngredient
import epicurius.domain.exceptions.NotTheRecipeAuthor
import epicurius.domain.exceptions.RecipeNotFound
import epicurius.domain.recipe.Cuisine
import epicurius.domain.recipe.Ingredient
import epicurius.domain.recipe.IngredientUnit
import epicurius.domain.recipe.MealType
import epicurius.http.controllers.recipe.models.input.UpdateRecipeInputModel
import epicurius.http.media.Problem
import epicurius.http.media.Uris
import epicurius.integration.utils.getBody
import epicurius.integration.utils.patch
import epicurius.utils.createTestRecipe
import epicurius.utils.createTestUser
import epicurius.utils.generateRandomRecipeDescription
import epicurius.utils.generateRandomRecipeInstructions
import epicurius.utils.generateRandomRecipeName
import org.junit.jupiter.api.Assertions.assertEquals
import org.springframework.http.HttpStatus
import kotlin.test.Test
import kotlin.test.assertNotNull

class UpdateRecipeIntegrationTests : RecipeIntegrationTest() {

    private val updateRecipeInputModel = UpdateRecipeInputModel(
        name = generateRandomRecipeName(),
        description = generateRandomRecipeDescription(),
        servings = 1,
        preparationTime = 1,
        cuisine = Cuisine.ASIAN,
        mealType = MealType.SOUP,
        intolerances = setOf(Intolerance.PEANUT, Intolerance.GLUTEN, Intolerance.DAIRY),
        diets = setOf(Diet.KETOGENIC),
        ingredients = listOf(Ingredient("Egg", 1.0, IngredientUnit.X)),
        calories = 1,
        protein = 1,
        fat = 1,
        carbs = 1,
        instructions = generateRandomRecipeInstructions()
    )

    private val testPrivateAuthor = createTestUser(tm, true)
    private val testPrivateRecipe = createTestRecipe(tm, testPrivateAuthor.user)

    @Test
    fun `Should update a recipe with code 200`() {
        // given an authenticated user and a recipe to update

        // when updating the recipe
        val response = updateRecipe(
            testAuthor.token,
            testRecipe.id,
            name = updateRecipeInputModel.name,
            description = updateRecipeInputModel.description,
            servings = updateRecipeInputModel.servings,
            preparationTime = updateRecipeInputModel.preparationTime,
            cuisine = updateRecipeInputModel.cuisine,
            mealType = updateRecipeInputModel.mealType,
            intolerances = updateRecipeInputModel.intolerances?.toList(),
            diets = updateRecipeInputModel.diets?.toList(),
            ingredients = updateRecipeInputModel.ingredients,
            calories = updateRecipeInputModel.calories,
            protein = updateRecipeInputModel.protein,
            fat = updateRecipeInputModel.fat,
            carbs = updateRecipeInputModel.carbs,
            instructions = updateRecipeInputModel.instructions
        )

        // then the response contains the updated recipe info
        assertNotNull(response)
        assertEquals(testRecipe.id, response.recipe.id)
        assertEquals(updateRecipeInputModel.name, response.recipe.name)
        assertEquals(updateRecipeInputModel.description, response.recipe.description)
    }

    @Test
    fun `Should fail with code 404 when updating a non-existing recipe`() {
        // given an authenticated user and a non-existing recipe ID
        val nonExistingRecipeId = 9999

        // when trying to update the non-existing recipe
        val error = patch<Problem>(
            client,
            api(Uris.Recipe.RECIPE.replace("{id}", nonExistingRecipeId.toString())),
            body = updateRecipeInputModel,
            responseStatus = HttpStatus.NOT_FOUND,
            token = testAuthor.token
        )
        assertNotNull(error)

        // then the recipe is not found
        val errorBody = getBody(error)
        assertEquals(RecipeNotFound().message, errorBody.detail)
    }

    @Test
    fun `Should fail with code 403 when updating a recipe that does not belong to the user`() {
        // given an authenticated user and a recipe that does not belong to the user
        val anotherUserRecipeId = testPrivateRecipe.id

        // when trying to update the recipe that does not belong to the user
        val error = patch<Problem>(
            client,
            api(Uris.Recipe.RECIPE.replace("{id}", anotherUserRecipeId.toString())),
            body = updateRecipeInputModel,
            responseStatus = HttpStatus.FORBIDDEN,
            token = testAuthor.token
        )
        assertNotNull(error)

        // then the user is not authorized to update the recipe
        val errorBody = getBody(error)
        assertEquals(NotTheRecipeAuthor().message, errorBody.detail)
    }

    @Test
    fun `Should fail with code 400 when updating a recipe with an invalid ingredient`() {
        // given an authenticated user and a recipe with an invalid ingredient
        val invalidIngredient = "invalid-ingredient"
        val invalidIngredients = listOf(Ingredient(invalidIngredient, 1.0, IngredientUnit.G))

        // when trying to update the recipe with the invalid ingredient
        val error = patch<Problem>(
            client,
            api(Uris.Recipe.RECIPE.replace("{id}", testRecipe.id.toString())),
            body = updateRecipeInputModel.copy(ingredients = invalidIngredients),
            responseStatus = HttpStatus.BAD_REQUEST,
            token = testAuthor.token
        )
        assertNotNull(error)

        // then the response contains the error message for invalid ingredient
        val errorBody = getBody(error)
        assertEquals(InvalidIngredient(invalidIngredient).message, errorBody.detail)
    }
}
