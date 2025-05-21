package epicurius.unit.services.ingredients

import epicurius.domain.picture.PictureDomain.Companion.INGREDIENTS_FOLDER
import epicurius.unit.services.ServiceTest
import kotlinx.coroutines.runBlocking
import org.mockito.kotlin.verify
import org.mockito.kotlin.whenever
import java.util.UUID.randomUUID
import kotlin.test.Test
import kotlin.test.assertEquals

class IdentifyIngredientsInPictureServiceTests : ServiceTest() {

    private val testIngredients = listOf("tomato")

    @Test
    fun `Should detect ingredients in a picture successfully`() {
        // given a picture (testTomatoPicture)

        // mock
        val mockPictureName = randomUUID().toString() + "." + testTomatoPicture.contentType?.substringAfter("/")
        whenever(pictureDomainMock.generatePictureName()).thenReturn(mockPictureName.substringBefore("."))
        whenever(
            runBlocking {
                cloudFunctionRepositoryMock.getIngredientsFromPicture(mockPictureName)
            }
        ).thenReturn(testIngredients)
        testIngredients.forEach {
            whenever(
                runBlocking {
                    spoonacularRepositoryMock.getIngredients(it)
                }
            ).thenReturn(listOf(it))
        }

        // when retrieving the ingredients from the picture
        val ingredients = runBlocking { identifyIngredientsInPicture(testTomatoPicture) }

        // then the ingredients are detected successfully
        verify(pictureDomainMock).validatePicture(testTomatoPicture)
        verify(pictureRepositoryMock).updatePicture(mockPictureName, testTomatoPicture, INGREDIENTS_FOLDER)
        verify(pictureRepositoryMock).deletePicture(mockPictureName, INGREDIENTS_FOLDER)
        assertEquals(testIngredients, ingredients)
    }

    @Test
    fun `Should return an empty list for a picture with no ingredients`() {
        // given a picture with no ingredients (testPicture)

        // mock
        val mockPictureName = randomUUID().toString() + "." + testPicture.contentType?.substringAfter("/")
        whenever(pictureDomainMock.generatePictureName()).thenReturn(mockPictureName.substringBefore("."))
        whenever(
            runBlocking {
                cloudFunctionRepositoryMock.getIngredientsFromPicture(mockPictureName)
            }
        ).thenReturn(emptyList())

        // when retrieving the ingredients from the picture
        val ingredients = runBlocking { identifyIngredientsInPicture(testPicture) }

        // then the ingredients are empty
        verify(pictureDomainMock).validatePicture(testPicture)
        verify(pictureRepositoryMock).updatePicture(mockPictureName, testPicture, INGREDIENTS_FOLDER)
        verify(pictureRepositoryMock).deletePicture(mockPictureName, INGREDIENTS_FOLDER)
        assertEquals(emptyList(), ingredients)
    }
}
