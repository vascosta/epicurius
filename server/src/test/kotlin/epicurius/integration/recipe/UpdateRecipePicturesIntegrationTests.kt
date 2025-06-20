package epicurius.integration.recipe

import epicurius.domain.exceptions.InvalidNumberOfRecipePictures
import epicurius.domain.exceptions.NotTheRecipeAuthor
import epicurius.domain.exceptions.RecipeNotFound
import epicurius.http.media.Problem
import epicurius.http.media.Uris
import epicurius.integration.utils.getBody
import epicurius.integration.utils.patchMultiPart
import org.springframework.http.HttpStatus
import org.springframework.http.client.MultipartBodyBuilder
import org.springframework.web.reactive.function.BodyInserters
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotNull
import kotlin.test.assertTrue

class UpdateRecipePicturesIntegrationTests : RecipeIntegrationTest() {

    @Test
    fun `Should maintain the pictures when updating the recipe pictures with the same ones with code 200`() {
        // given the same pictures (testPicture)

        // when updating the recipe pictures with the same ones
        val response = updateRecipePictures(
            testAuthor.token,
            testRecipe.id,
            pictures = listOf(testPicture)
        )

        // then the recipe maintains the same pictures
        assertNotNull(response)
        assertEquals(1, response.pictures.size)
        assertTrue(response.pictures.any { it.contentEquals(testPicture.bytes) })
    }

    @Test
    fun `Should add more pictures to a recipe with code 200`() {
        // given new pictures to add to the recipe
        val newPictures = listOf(testPicture, testPicture2, testTomatoPicture)

        // when adding the new pictures to the recipe
        val response = updateRecipePictures(
            testAuthor.token,
            testRecipe.id,
            pictures = newPictures
        )

        // then the recipe is updated successfully
        assertNotNull(response)
        assertEquals(newPictures.size, response.pictures.size)
        newPictures.map { it.bytes }.zip(response.pictures).forEach { (expected, actual) ->
            assertTrue(actual.contentEquals(expected))
        }
    }

    @Test
    fun `Should remove some pictures of a recipe with code 200`() {
        // given a recipe with 3 pictures, and a new number of pictures, lower than the current number of pictures
        val createRecipeWith3Pictures = createRecipe(
            testAuthor.token,
            body = createRecipeInputModel,
            pictures = listOf(testPicture, testPicture2, testTomatoPicture)
        )
        assertNotNull(createRecipeWith3Pictures)
        assertEquals(3, createRecipeWith3Pictures.recipe.pictures.size)
        val testRecipe = createRecipeWith3Pictures.recipe
        val newPictures = listOf(testPicture)

        // when removing some pictures of the recipe
        val response = updateRecipePictures(
            testAuthor.token,
            testRecipe.id,
            pictures = newPictures
        )

        // then the recipe is updated successfully
        assertNotNull(response)
        assertEquals(1, response.pictures.size)
        assertTrue(response.pictures.any { it.contentEquals(testPicture.bytes) })
    }

    @Test
    fun `Should change the order of the recipe pictures with code 200`() {
        // given a recipe with 3 pictures, and a new order of the pictures
        val createRecipeWith3Pictures = createRecipe(
            testAuthor.token,
            body = createRecipeInputModel,
            pictures = listOf(testPicture, testPicture2, testTomatoPicture)
        )
        assertNotNull(createRecipeWith3Pictures)
        val testRecipe = createRecipeWith3Pictures.recipe
        val newPictures = listOf(testTomatoPicture, testPicture2, testPicture)

        // when changing the order of the recipe pictures
        val response = updateRecipePictures(
            testAuthor.token,
            testRecipe.id,
            pictures = newPictures
        )

        // then the recipe is updated successfully with the new order
        assertNotNull(response)
        assertEquals(newPictures.size, response.pictures.size)
        newPictures.map { it.bytes }.zip(response.pictures).forEach { (expected, actual) ->
            assertTrue(actual.contentEquals(expected))
        }
    }

    @Test
    fun `Should fail with code 400 when updating the recipe pictures with an invalid number of pictures`() {
        // given an invalid number of pictures (more than 10)
        val invalidPictures = List(11) { testPicture }

        // when trying to update the recipe pictures with the invalid number of pictures
        val multipartBody = MultipartBodyBuilder().apply {
            invalidPictures.forEach { invalidPicture ->
                part("pictures", invalidPicture.resource)
            }
        }
        val error = patchMultiPart<Problem>(
            client,
            api(Uris.Recipe.RECIPE_PICTURES.replace("{id}", testRecipe.id.toString())),
            BodyInserters.fromMultipartData(multipartBody.build()),
            responseStatus = HttpStatus.BAD_REQUEST,
            token = testAuthor.token
        )
        assertNotNull(error)

        // then the response contains an error message
        val errorBody = getBody(error)
        assertEquals(InvalidNumberOfRecipePictures().message, errorBody.detail)
    }

    @Test
    fun `Should fail with code 401 when updating the pictures of a non-existing recipe`() {
        // given a non-existing recipe id and some pictures to update
        val nonExistingRecipeId = 9999
        val pictures = listOf(testPicture, testPicture2)

        // when trying to update the pictures of the non-existing recipe
        val multipartBody = MultipartBodyBuilder().apply {
            pictures.forEach { picture ->
                part("pictures", picture.resource)
            }
        }
        val error = patchMultiPart<Problem>(
            client,
            api(Uris.Recipe.RECIPE_PICTURES.replace("{id}", nonExistingRecipeId.toString())),
            BodyInserters.fromMultipartData(multipartBody.build()),
            responseStatus = HttpStatus.NOT_FOUND,
            token = testAuthor.token
        )
        assertNotNull(error)

        // then the response contains an error message
        val errorBody = getBody(error)
        assertEquals(RecipeNotFound().message, errorBody.detail)
    }

    @Test
    fun `Should fail with code 403 when updating the recipe pictures that does not belong to the user`() {
        // given a recipe that does not belong to the user and some pictures to update
        val pictures = listOf(testPicture, testPicture2)

        // when trying to update the pictures of the recipe that does not belong to the user
        val multipartBody = MultipartBodyBuilder().apply {
            pictures.forEach { picture ->
                part("pictures", picture.resource)
            }
        }
        val error = patchMultiPart<Problem>(
            client,
            api(Uris.Recipe.RECIPE_PICTURES.replace("{id}", testRecipe.id.toString())),
            BodyInserters.fromMultipartData(multipartBody.build()),
            responseStatus = HttpStatus.FORBIDDEN,
            token = testUser.token
        )
        assertNotNull(error)

        // then the response contains an error message
        val errorBody = getBody(error)
        assertEquals(NotTheRecipeAuthor().message, errorBody.detail)
    }
}
