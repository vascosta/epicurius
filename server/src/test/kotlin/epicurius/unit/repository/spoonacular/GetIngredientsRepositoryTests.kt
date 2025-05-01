package epicurius.unit.repository.spoonacular

import kotlinx.coroutines.runBlocking
import kotlin.test.Test
import kotlin.test.assertContains
import kotlin.test.assertEquals

class GetIngredientsRepositoryTests: SpoonacularRepositoryTest() {

    @Test
    fun `Should retrieve ingredients successfully`() {
        // given a partial name
        val partialName = "apple"

        // when retrieving ingredients
        val ingredients = runBlocking { getIngredients(partialName) }

        // then the ingredients are retrieved successfully
        assertEquals(testIngredients, ingredients)
        assertContains(ingredients, partialName)
    }

    @Test
    fun `Should a empty list when no ingredients are found`() {
        // given a partial name
        val partialName = "nonexistent"

        // when retrieving ingredients
        val ingredients = runBlocking { getIngredients(partialName) }

        // then the ingredients are retrieved successfully
        assertEquals(emptyList(), ingredients)
    }
}