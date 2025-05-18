package epicurius.unit.http.recipe

import epicurius.domain.exceptions.InvalidNumberOfRecipePictures
import epicurius.domain.exceptions.NotTheRecipeAuthor
import epicurius.domain.exceptions.RecipeNotFound
import epicurius.domain.user.AuthenticatedUser
import epicurius.domain.user.User
import epicurius.http.controllers.recipe.models.output.UpdateRecipePicturesOutputModel
import epicurius.services.recipe.models.UpdateRecipePicturesModel
import kotlinx.coroutines.runBlocking
import org.mockito.kotlin.whenever
import org.springframework.http.HttpStatus
import org.springframework.web.multipart.MultipartFile
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith

class UpdateRecipePicturesControllerTests : RecipeControllerTest() {

    @Test
    fun `Should maintain the pictures when updating the recipe pictures with the same ones successfully`() {
        // given the same pictures (recipePictures)

        // mock
        val mockUpdateRecipePicturesModel = UpdateRecipePicturesModel(recipePictures.map { it.bytes })
        whenever(recipeServiceMock.updateRecipePictures(testAuthenticatedUser.user.id, RECIPE_ID, recipePictures.toSet()))
            .thenReturn(mockUpdateRecipePicturesModel)

        // when updating the recipe pictures with the same ones
        val response = runBlocking {
            updateRecipePictures(testAuthenticatedUser, RECIPE_ID, recipePictures)
        }
        val body = response.body as UpdateRecipePicturesOutputModel

        // then the recipe maintains the same pictures
        assertEquals(HttpStatus.OK, response.statusCode)
        assertEquals(recipePictures.map { it.bytes }, body.pictures)
    }

    @Test
    fun `Should add more pictures to a recipe successfully`() {
        // given new pictures to add to the recipe
        val newPictures = listOf(testPicture2, testPicture, testTomatoPicture)

        // mock
        val mockUpdateRecipePicturesModel = UpdateRecipePicturesModel(newPictures.map { it.bytes })
        whenever(recipeServiceMock.updateRecipePictures(testAuthenticatedUser.user.id, RECIPE_ID, newPictures.toSet()))
            .thenReturn(mockUpdateRecipePicturesModel)

        // when adding the new pictures to the recipe
        val response = runBlocking {
            updateRecipePictures(testAuthenticatedUser, RECIPE_ID, newPictures)
        }
        val body = response.body as UpdateRecipePicturesOutputModel

        // then the recipe is updated successfully
        assertEquals(HttpStatus.OK, response.statusCode)
        assertEquals(newPictures.map { it.bytes }, body.pictures)
    }

    @Test
    fun `Should remove some pictures of a recipe successfully`() {
        // given a new number of pictures, lower than the current number of pictures
        val newPictures = listOf(testPicture2, testTomatoPicture)

        // mock
        val mockUpdateRecipePicturesModel = UpdateRecipePicturesModel(newPictures.map { it.bytes })
        whenever(recipeServiceMock.updateRecipePictures(testAuthenticatedUser.user.id, RECIPE_ID, newPictures.toSet()))
            .thenReturn(mockUpdateRecipePicturesModel)

        // when removing some pictures of the recipe
        val response = runBlocking {
            updateRecipePictures(testAuthenticatedUser, RECIPE_ID, newPictures)
        }
        val body = response.body as UpdateRecipePicturesOutputModel

        // then the recipe is updated successfully
        assertEquals(HttpStatus.OK, response.statusCode)
        assertEquals(newPictures.map { it.bytes }, body.pictures)
    }

    @Test
    fun `Should change the order of the recipe pictures successfully`() {
        // given a new order for the recipe pictures
        val newPictures = listOf(testPicture2, testTomatoPicture, testPicture)

        // mock
        val mockUpdateRecipePicturesModel = UpdateRecipePicturesModel(newPictures.map { it.bytes })
        whenever(recipeServiceMock.updateRecipePictures(testAuthenticatedUser.user.id, RECIPE_ID, newPictures.toSet()))
            .thenReturn(mockUpdateRecipePicturesModel)

        // when changing the order of the recipe pictures
        val response = runBlocking {
            updateRecipePictures(testAuthenticatedUser, RECIPE_ID, newPictures)
        }
        val body = response.body as UpdateRecipePicturesOutputModel

        // then the recipe is updated successfully
        assertEquals(HttpStatus.OK, response.statusCode)
        assertEquals(newPictures.map { it.bytes }, body.pictures)
    }

    @Test
    fun `Should throw InvalidNumberOfRecipePictures exception when updating the recipe pictures with an invalid number of pictures`() {
        // given an invalid number of pictures
        val invalidNumberOfRecipePicturesSet = emptyList<MultipartFile>()

        // mock
        whenever(recipeServiceMock.updateRecipePictures(testAuthenticatedUser.user.id, RECIPE_ID, invalidNumberOfRecipePicturesSet.toSet()))
            .thenThrow(InvalidNumberOfRecipePictures())

        // when updating the recipe pictures with an invalid number of pictures
        // then the recipe is not updated and throws InvalidNumberOfRecipePictures exception
        assertFailsWith<InvalidNumberOfRecipePictures> {
            runBlocking { updateRecipePictures(testAuthenticatedUser, RECIPE_ID, invalidNumberOfRecipePicturesSet) }
        }
    }

    @Test
    fun `Should throw RecipeNotFound exception when updating the pictures of a non-existing recipe`() {
        // given a non-existing recipe id
        val nonExistingRecipeId = 9999

        // mock
        whenever(recipeServiceMock.updateRecipePictures(testAuthenticatedUser.user.id, nonExistingRecipeId, recipePictures.toSet()))
            .thenThrow(RecipeNotFound())

        // when updating the pictures of a non-existing recipe
        // then the recipe is not updated and throws RecipeNotFound exception
        assertFailsWith<RecipeNotFound> {
            runBlocking { updateRecipePictures(testAuthenticatedUser, nonExistingRecipeId, recipePictures) }
        }
    }

    @Test
    fun `Should throw NotTheAuthor exception when updating the recipe pictures that does not belong to the user`() {
        // given a user id and a recipe id (RECIPE_ID) that does not belong to him
        val notTheAuthor = AuthenticatedUser(User(9999, "", "", "", "", "", false, emptyList(), emptyList(), ""), "")

        // mock
        whenever(recipeServiceMock.updateRecipePictures(notTheAuthor.user.id, RECIPE_ID, recipePictures.toSet()))
            .thenThrow(NotTheRecipeAuthor())

        // when updating the recipe pictures that does not belong to the user
        // then the recipe is not updated and throws NotTheAuthor exception
        assertFailsWith<NotTheRecipeAuthor> {
            runBlocking { updateRecipePictures(notTheAuthor, RECIPE_ID, recipePictures) }
        }
    }
}
