package epicurius.unit.repository.spoonacular

import epicurius.domain.exceptions.InvalidIngredient
import kotlinx.coroutines.runBlocking
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith

class GetSubstituteIngredientsRepositoryTests: SpoonacularRepositoryTest() {

    private val testSubstituteIngredients = listOf("1 cup quinces", "1 cup pears")

    @Test
    fun `Should retrieve substitute ingredients for a valid ingredient successfully`() {
        // given a valid ingredient
        val ingredient = "apple"

        // when retrieving substitute ingredients
        val substituteIngredients = runBlocking { getSubstituteIngredients(ingredient) }

        // then the substitute ingredients are retrieved successfully
        assertEquals(testSubstituteIngredients, substituteIngredients)
    }

    @Test
    fun `Should return an empty list for an ingredient with no substitutes`() {
        // given an ingredient with no substitutes
        val ingredient = "water"

        // when retrieving substitute ingredients
        val substituteIngredients = runBlocking { getSubstituteIngredients(ingredient) }

        // then the substitute ingredients are empty
        assertEquals(emptyList(), substituteIngredients)
    }

    @Test
    fun `Should throw InvalidIngredient exception for an invalid ingredient`() {
        // given an invalid ingredient
        val ingredient = "invalid-ingredient"

        // when retrieving substitute ingredients
        // then the substitute ingredients cannot be retrieved and throws InvalidIngredient exception
        assertFailsWith<InvalidIngredient> { runBlocking { getSubstituteIngredients(ingredient) } }
    }
}