package epicurius.unit.services.ingredients

import epicurius.domain.exceptions.InvalidIngredient
import kotlinx.coroutines.runBlocking
import org.mockito.kotlin.whenever
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith

class GetSubstituteIngredientsServiceTests: IngredientsServiceTest() {

    @Test
    fun `Should retrieve substitute ingredients for a valid ingredient successfully`() {
        // given a valid ingredient
        val ingredient = "apple"

        // mock
        whenever(runBlocking { spoonacularRepositoryMock.getSubstituteIngredients(ingredient) })
            .thenReturn(testSubstituteIngredients)

        // when retrieving substitute ingredients
        val substituteIngredients = runBlocking { getSubstituteIngredients(ingredient) }

        // then the substitute ingredients are retrieved successfully
        assertEquals(testSubstituteIngredients, substituteIngredients)
    }

    @Test
    fun `Should return an empty list for an ingredient with no substitutes`() {
        // given an ingredient with no substitutes
        val ingredientWithNoSubstitutes = "water"

        // mock
        whenever(runBlocking { spoonacularRepositoryMock.getSubstituteIngredients(ingredientWithNoSubstitutes) })
            .thenReturn(emptyList())

        // when retrieving substitute ingredients
        val substituteIngredients = runBlocking { getSubstituteIngredients(ingredientWithNoSubstitutes) }

        // then the substitute ingredients are empty
        assertEquals(emptyList(), substituteIngredients)
    }

    @Test
    fun `Should throw InvalidIngredient exception for an invalid ingredient`() {
        // given an invalid ingredient
        val invalidIngredient = "invalid-ingredient"

        // mock
        whenever(runBlocking { spoonacularRepositoryMock.getSubstituteIngredients(invalidIngredient) })
            .thenThrow(InvalidIngredient(invalidIngredient))

        // when retrieving substitute ingredients
        // then the substitute ingredients cannot be retrieved and throws InvalidIngredient exception
        assertFailsWith<InvalidIngredient> { runBlocking { getSubstituteIngredients(invalidIngredient) } }
    }
}