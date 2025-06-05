package epicurius.integration.ingredients

import epicurius.utils.createTestUser
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotNull

class IdentifyIngredientsInPictureIntegrationTests : IngredientsIntegrationTest() {

    private val testUser = createTestUser(tm)
    private val testIngredients = listOf("cherry tomato", "tomato", "plum tomato")

    @Test
    fun `Should detect ingredients in a picture with code 200`() {
        // given a picture with ingredients (testTomatoPicture)

        // when identifying ingredients in the picture
        val body = identifyIngredientsInPicture(testUser.token, testTomatoPicture)

        // then the ingredients are detected successfully with code 200
        assertNotNull(body)
        assertEquals(testIngredients, body.ingredients)
    }

    @Test
    fun `Should return an empty list for a picture with no ingredients with code 200`() {
        // given a picture with no ingredients (testPicture)

        // when identifying ingredients in the picture
        val body = identifyIngredientsInPicture(testUser.token, testPicture)

        // then the ingredients are empty with code 200
        assertNotNull(body)
        assertEquals(emptyList<String>(), body.ingredients)
    }
}
