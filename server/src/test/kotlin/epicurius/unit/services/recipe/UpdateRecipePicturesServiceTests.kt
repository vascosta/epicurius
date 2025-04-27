package epicurius.unit.services.recipe

import epicurius.domain.exceptions.InvalidNumberOfRecipePictures
import epicurius.domain.exceptions.NotTheAuthor
import epicurius.domain.exceptions.RecipeNotFound
import epicurius.domain.picture.PictureDomain
import epicurius.repository.jdbi.recipe.models.JdbiUpdateRecipeModel
import kotlinx.coroutines.runBlocking
import org.mockito.kotlin.verify
import org.mockito.kotlin.whenever
import org.springframework.web.multipart.MultipartFile
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith

class UpdateRecipePicturesServiceTests: RecipeServiceTest() {

    @Test
    fun `Should maintain the pictures when updating the recipe pictures with the same ones successfully`() {
        // given the same pictures (recipePictures)

        // mock
        whenever(jdbiRecipeRepositoryMock.getRecipe(RECIPE_ID)).thenReturn(jdbiRecipeModel)
        whenever(pictureRepositoryMock.getPicture(recipePicturesNames.first(), PictureDomain.RECIPES_FOLDER))
            .thenReturn(recipePictures.first().bytes)

        // when updating the recipe pictures with the same ones
        val updatedPictures = runBlocking { updateRecipePictures(AUTHOR_ID, RECIPE_ID, recipePictures) }

        // then the recipe maintains the same pictures
        assertEquals(recipePictures.map { it.bytes }, updatedPictures.pictures)
    }

    @Test
    fun `Should add more pictures to a recipe successfully`() {
        // given new pictures to add to the recipe
        val newPictures = setOf(testPicture2, testPicture, testTomatoPicture)

        // mock
        val mockRecipePicturesNames = listOf(testPicture2.name, recipePicturesNames.first(), testTomatoPicture.name)
        val mockJdbiUpdateRecipeModel = JdbiUpdateRecipeModel(RECIPE_ID, picturesNames = mockRecipePicturesNames)
        whenever(jdbiRecipeRepositoryMock.getRecipe(RECIPE_ID)).thenReturn(jdbiRecipeModel)
        whenever(pictureRepositoryMock.getPicture(recipePicturesNames.first(), PictureDomain.RECIPES_FOLDER))
            .thenReturn(recipePictures.first().bytes)
        whenever(pictureDomainMock.generatePictureName()).thenReturn(testPicture2.name, testTomatoPicture.name)
        whenever(jdbiRecipeRepositoryMock.updateRecipe(mockJdbiUpdateRecipeModel))
            .thenReturn(jdbiRecipeModel.copy(picturesNames = mockRecipePicturesNames))

        // when adding the new pictures to the recipe
        val updatedPictures = runBlocking { updateRecipePictures(AUTHOR_ID, RECIPE_ID, newPictures) }

        // then the recipe is updated successfully
        verify(pictureRepositoryMock).updatePicture(testPicture2.name, testPicture2, PictureDomain.RECIPES_FOLDER)
        verify(pictureRepositoryMock).updatePicture(testTomatoPicture.name, testTomatoPicture, PictureDomain.RECIPES_FOLDER)
        assertEquals(newPictures.map { it.bytes }, updatedPictures.pictures)
    }

    @Test
    fun `Should remove some pictures of a recipe successfully`() {
        // given a new number of pictures, lower than the current number of pictures
        val oldPictureNames = listOf(testPicture2.name, recipePicturesNames.first(), testTomatoPicture.name)
        val newPictures = setOf(testTomatoPicture, testPicture)

        // mock
        val mockRecipePicturesNames = listOf(testTomatoPicture.name, recipePicturesNames.first())
        val mockJdbiUpdateRecipeModel = JdbiUpdateRecipeModel(RECIPE_ID, picturesNames = mockRecipePicturesNames)

        whenever(jdbiRecipeRepositoryMock.getRecipe(RECIPE_ID)).thenReturn(jdbiRecipeModel.copy(picturesNames = oldPictureNames))
        whenever(pictureRepositoryMock.getPicture(testPicture2.name, PictureDomain.RECIPES_FOLDER))
            .thenReturn(testPicture2.bytes)
        whenever(pictureRepositoryMock.getPicture(recipePicturesNames.first(), PictureDomain.RECIPES_FOLDER))
            .thenReturn(recipePictures.first().bytes)
        whenever(pictureRepositoryMock.getPicture(testTomatoPicture.name, PictureDomain.RECIPES_FOLDER))
            .thenReturn(testTomatoPicture.bytes)
        whenever(jdbiRecipeRepositoryMock.updateRecipe(mockJdbiUpdateRecipeModel))
            .thenReturn(jdbiRecipeModel.copy(picturesNames = mockRecipePicturesNames))

        // when removing some pictures from the recipe
        val updatedPictures = runBlocking { updateRecipePictures(AUTHOR_ID, RECIPE_ID, newPictures) }

        // then the recipe is updated successfully
        verify(pictureRepositoryMock).deletePicture(testPicture2.name, PictureDomain.RECIPES_FOLDER)
        assertEquals(newPictures.map { it.bytes }, updatedPictures.pictures)
    }

    @Test
    fun `Should change the order of the recipe pictures successfully`() {
        // given a new order of pictures
        val oldPictureNames = listOf(testPicture2.name, recipePicturesNames.first(), testTomatoPicture.name)
        val newPictures = setOf(recipePictures.first(), testTomatoPicture, testPicture2)

        // mock
        val mockPictureNames = listOf(recipePicturesNames.first(), testTomatoPicture.name, testPicture2.name)
        val mockJdbiUpdateRecipeModel = JdbiUpdateRecipeModel(RECIPE_ID, picturesNames = mockPictureNames)
        whenever(jdbiRecipeRepositoryMock.getRecipe(RECIPE_ID)).thenReturn(jdbiRecipeModel.copy(picturesNames = oldPictureNames))
        whenever(pictureRepositoryMock.getPicture(recipePicturesNames.first(), PictureDomain.RECIPES_FOLDER))
            .thenReturn(recipePictures.first().bytes)
        whenever(pictureRepositoryMock.getPicture(testTomatoPicture.name, PictureDomain.RECIPES_FOLDER))
            .thenReturn(testTomatoPicture.bytes)
        whenever(pictureRepositoryMock.getPicture(testPicture2.name, PictureDomain.RECIPES_FOLDER))
            .thenReturn(testPicture2.bytes)
        whenever(jdbiRecipeRepositoryMock.updateRecipe(mockJdbiUpdateRecipeModel))
            .thenReturn(jdbiRecipeModel.copy(picturesNames = mockPictureNames))

        // when removing some pictures from the recipe
        val updatedPictures = runBlocking { updateRecipePictures(AUTHOR_ID, RECIPE_ID, newPictures) }

        // then the recipe is updated successfully
        assertEquals(newPictures.map { it.bytes }, updatedPictures.pictures)
    }

    @Test
    fun `Should throw InvalidNumberOfRecipePictures exception when updating the recipe pictures with an invalid number of pictures`() {
        // given an invalid number of pictures
        val invalidPictures = emptySet<MultipartFile>()

        // when creating the recipe with invalid number of pictures
        // then the recipe is not created and throws InvalidNumberOfRecipePictures exception
        assertFailsWith<InvalidNumberOfRecipePictures> {
            runBlocking { updateRecipePictures(AUTHOR_ID, RECIPE_ID, invalidPictures) }
        }
    }

    @Test
    fun `Should throw RecipeNotFound exception when updating the pictures of a non-existing recipe`() {
        // given a non-existing recipe id
        val nonExistingRecipeId = 9999

        // mock
        whenever(jdbiRecipeRepositoryMock.getRecipe(nonExistingRecipeId)).thenReturn(null)

        // when updating the recipe
        // then the recipe is not updated and throws RecipeNotFound exception
        assertFailsWith<RecipeNotFound> {
            runBlocking { updateRecipePictures(AUTHOR_ID, nonExistingRecipeId, recipePictures) }
        }
    }

    @Test
    fun `Should throw NotTheAuthor exception when updating the recipe pictures that does not belong to the user`() {
        // given a user id and a recipe id (RECIPE_ID) that does not belong to him
        val userId = 9999

        // mock
        whenever(jdbiRecipeRepositoryMock.getRecipe(RECIPE_ID)).thenReturn(jdbiRecipeModel)

        // when updating the recipe
        // then the recipe is not updated and throws NotTheAuthor exception
        assertFailsWith<NotTheAuthor> { runBlocking { updateRecipePictures(userId, RECIPE_ID, recipePictures) } }
    }
}