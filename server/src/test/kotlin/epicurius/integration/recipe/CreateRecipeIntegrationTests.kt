package epicurius.integration.recipe

import epicurius.domain.exceptions.InvalidIngredient
import epicurius.domain.exceptions.InvalidNumberOfRecipePictures
import epicurius.domain.recipe.Ingredient
import epicurius.domain.recipe.IngredientUnit
import epicurius.http.media.Problem
import epicurius.http.media.Uris
import epicurius.integration.utils.getBody
import epicurius.integration.utils.postMultiPart
import org.springframework.http.HttpStatus
import org.springframework.http.client.MultipartBodyBuilder
import org.springframework.web.multipart.MultipartFile
import org.springframework.web.reactive.function.BodyInserters
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotNull
import kotlin.test.assertTrue

class CreateRecipeIntegrationTests : RecipeIntegrationTest() {

    @Test
    fun `Should create a recipe with code 201`() {
        // given an authenticated user and information for a new recipe

        // when creating a new recipe
        val result = createRecipe(
            token = testUser.token,
            body = createRecipeInputModel,
            pictures = listOf(testPicture)
        )

        // then the recipe should be created successfully with status code 201
        assertNotNull(result)
        assertEquals(createRecipeInputModel.name, result.recipe.name)
        assertEquals(createRecipeInputModel.description, result.recipe.description)
        assertEquals(createRecipeInputModel.servings, result.recipe.servings)
        assertEquals(createRecipeInputModel.preparationTime, result.recipe.preparationTime)
        assertEquals(createRecipeInputModel.cuisine, result.recipe.cuisine)
        assertEquals(createRecipeInputModel.mealType, result.recipe.mealType)
        assertTrue(result.recipe.intolerances.containsAll(createRecipeInputModel.intolerances))
        assertTrue(result.recipe.diets.containsAll(createRecipeInputModel.diets))
        assertEquals(createRecipeInputModel.ingredients, result.recipe.ingredients)
        assertEquals(createRecipeInputModel.calories, result.recipe.calories)
        assertEquals(createRecipeInputModel.protein, result.recipe.protein)
        assertEquals(createRecipeInputModel.fat, result.recipe.fat)
        assertEquals(createRecipeInputModel.carbs, result.recipe.carbs)
        assertEquals(createRecipeInputModel.instructions, result.recipe.instructions)
        assertEquals(1, result.recipe.pictures.size)
    }

    @Test
    fun `Should fail with code 400 when creating a recipe with an invalid number of pictures`() {
        // given an authenticated user and information for a new recipe with an invalid number of pictures (0)
        val invalidPictures = listOf<MultipartFile>(testPicture, testPicture, testPicture, testPicture)

        // when trying to create a new recipe with the invalid number of pictures
        val multipartBody = MultipartBodyBuilder().apply {
            part("body", createRecipeInputModel)
            invalidPictures.forEach { picture ->
                part("pictures", picture.resource)
            }
        }

        val error = postMultiPart<Problem>(
            client,
            api(Uris.Recipe.RECIPES),
            body = BodyInserters.fromMultipartData(multipartBody.build()),
            responseStatus = HttpStatus.BAD_REQUEST,
            token = testUser.token
        )
        assertNotNull(error)

        // then the request should fail with status code 400
        val errorBody = getBody(error)
        assertNotNull(errorBody)
        assertEquals(InvalidNumberOfRecipePictures().message, errorBody.detail)
    }

    @Test
    fun `Should fail with code 400 when creating a recipe with an invalid ingredient`() {
        // given an authenticated user and information for a new recipe with an invalid ingredient
        val invalidIngredient = Ingredient("Ovos", 4.0, IngredientUnit.X) // invalid ingredient unit
        val invalidRecipeInputModel = createRecipeInputModel.copy(
            ingredients = listOf(
                invalidIngredient,
                Ingredient("Sugar", 200.0, IngredientUnit.G),
                Ingredient("Wheat Flour", 100.0, IngredientUnit.G),
                Ingredient("Milk", 500.0, IngredientUnit.ML),
                Ingredient("Butter", 50.0, IngredientUnit.G)
            )
        )

        // when trying to create a new recipe with the invalid ingredient
        val error = postMultiPart<Problem>(
            client,
            api(Uris.Recipe.RECIPES),
            body = BodyInserters.fromMultipartData(
                MultipartBodyBuilder().apply {
                    part("body", invalidRecipeInputModel)
                    part("pictures", testPicture.resource)
                }.build()
            ),
            responseStatus = HttpStatus.BAD_REQUEST,
            token = testUser.token
        )
        assertNotNull(error)

        // then the request should fail with status code 400
        val errorBody = getBody(error)
        assertNotNull(errorBody)
        assertEquals(InvalidIngredient(invalidIngredient.name).message, errorBody.detail)
    }
}
