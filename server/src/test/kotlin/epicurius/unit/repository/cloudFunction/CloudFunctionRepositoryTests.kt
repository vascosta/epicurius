package epicurius.unit.repository.cloudFunction

import epicurius.domain.exceptions.ErrorOnCloudFunction
import epicurius.unit.repository.RepositoryTest
import kotlinx.coroutines.runBlocking
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith

class CloudFunctionRepositoryTests : RepositoryTest() {

    companion object {
        private val testIngredients = listOf(
            "red",
            "fruit",
            "cherry tomato",
            "natural foods",
            "tomato",
            "bush tomato",
            "vegetable",
            "produce",
            "food",
            "plum tomato"
        )

        suspend fun getIngredientsFromPicture(pictureName: String) =
            cf.cloudFunctionRepository.getIngredientsFromPicture(pictureName)
    }


    @Test
    fun `Should detect ingredients in a picture successfully`() {
        // given a picture (testTomatoPicture) in the cloud storage

        // when retrieving the ingredients from the picture
        val ingredients = runBlocking { getIngredientsFromPicture(testTomatoPicture.name) }

        // then the ingredients are detected successfully
        assertEquals(testIngredients, ingredients)
    }

    @Test
    fun `Should throw ErrorOnCloudFunction when cloud function fails`() {
        // given a picture not presented in the cloud storage
        val invalidPicture = "invalid_picture_name"

        // when retrieving the ingredients from the picture
        // then the ingredients cannot be detected and throws ErrorOnCloudFunction exception
        assertFailsWith<ErrorOnCloudFunction> { runBlocking { getIngredientsFromPicture(invalidPicture) } }
    }
}
