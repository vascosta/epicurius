package epicurius.integration.ingredients

import epicurius.domain.exceptions.InvalidIngredient
import epicurius.http.media.Problem
import epicurius.http.media.Uris
import epicurius.integration.utils.get
import epicurius.utils.createTestUser
import org.springframework.http.HttpStatus
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotNull

class GetSubstituteIngredientsIntegrationTests : IngredientsIntegrationTest() {

    private val testUser = createTestUser(tm)
    private val testSubstituteIngredients = listOf("1 cup quinces", "1 cup pears")

    @Test
    fun `Should retrieve substitute ingredients for a valid ingredient with code 200`() {
        // given a valid ingredient
        val ingredient = "apple"

        // when retrieving substitute ingredients
        val body = getSubstituteIngredients(testUser.token, ingredient)

        // then the substitute ingredients are retrieved successfully with code 200
        assertNotNull(body)
        assertEquals(testSubstituteIngredients, body.ingredients)
    }

    @Test
    fun `Should return an empty list for an ingredient with no substitutes with code 200`() {
        // given an ingredient with no substitutes
        val ingredientWithNoSubstitutes = "water"

        // when retrieving substitute ingredients
        val body = getSubstituteIngredients(testUser.token, ingredientWithNoSubstitutes)

        // then the substitute ingredients are empty with code 200
        assertNotNull(body)
        assertEquals(emptyList<String>(), body.ingredients)
    }

    @Test
    fun `Should fail with code 400 when retrieving substitute ingredients for an invalid ingredient`() {
        // given an invalid ingredient
        val invalidIngredient = "invalid-ingredient"

        // when retrieving substitute ingredients
        val error = get<Problem>(
            client,
            api("${Uris.Ingredients.INGREDIENTS_SUBSTITUTES}?name=$invalidIngredient"),
            responseStatus = HttpStatus.BAD_REQUEST,
            token = testUser.token
        )

        // then the request fails with code 400
        assertNotNull(error)
        assertEquals(InvalidIngredient(invalidIngredient).message, error.detail)
    }
}
