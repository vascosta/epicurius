package epicurius.unit.http.ingredients

import epicurius.domain.exceptions.InvalidIngredient
import epicurius.domain.user.AuthenticatedUser
import epicurius.domain.user.User
import epicurius.http.controllers.ingredients.models.output.GetSubstituteIngredientsOutputModel
import epicurius.unit.http.HttpTest
import epicurius.utils.generateEmail
import epicurius.utils.generateRandomUsername
import kotlinx.coroutines.runBlocking
import org.mockito.kotlin.whenever
import org.springframework.http.HttpStatus
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith

class GetSubstituteIngredientsControllerTests : HttpTest() {

    private val testSubstituteIngredients = listOf("1 cup quinces", "1 cup pears")

    private val testUsername = generateRandomUsername()
    private val testAuthenticatedUser = AuthenticatedUser(
        User(1, testUsername, generateEmail(testUsername), "", "", "PT", false, emptyList(), emptyList(), ""),
        ""
    )

    @Test
    fun `Should retrieve substitute ingredients for a valid ingredient successfully`() {
        // given a valid ingredient
        val ingredient = "apple"

        // mock
        whenever(
            runBlocking { ingredientsServiceMock.getSubstituteIngredients(ingredient) }
        ).thenReturn(testSubstituteIngredients)

        // when retrieving substitute ingredients
        val response = runBlocking { getSubstituteIngredients(testAuthenticatedUser, ingredient) }
        val body = response.body as GetSubstituteIngredientsOutputModel

        // then the substitute ingredients are retrieved successfully
        assertEquals(HttpStatus.OK, response.statusCode)
        assertEquals(testSubstituteIngredients, body.ingredients)
    }

    @Test
    fun `Should return an empty list for an ingredient with no substitutes`() {
        // given an ingredient with no substitutes
        val ingredientWithNoSubstitutes = "water"

        // mock
        whenever(
            runBlocking { ingredientsServiceMock.getSubstituteIngredients(ingredientWithNoSubstitutes) }
        ).thenReturn(emptyList())

        // when retrieving substitute ingredients
        val response = runBlocking { getSubstituteIngredients(testAuthenticatedUser, ingredientWithNoSubstitutes) }
        val body = response.body as GetSubstituteIngredientsOutputModel

        // then the substitute ingredients are empty
        assertEquals(HttpStatus.OK, response.statusCode)
        assertEquals(emptyList(), body.ingredients)
    }

    @Test
    fun `Should throw InvalidIngredient exception for an invalid ingredient`() {
        // given an invalid ingredient
        val invalidIngredient = "invalid-ingredient"

        // mock
        whenever(
            runBlocking { ingredientsServiceMock.getSubstituteIngredients(invalidIngredient) }
        ).thenThrow(InvalidIngredient(invalidIngredient))

        // when retrieving substitute ingredients
        // then the substitute ingredients cannot be retrieved and throws InvalidIngredient exception
        assertFailsWith<InvalidIngredient> {
            runBlocking { getSubstituteIngredients(testAuthenticatedUser, invalidIngredient) }
        }
    }
}
