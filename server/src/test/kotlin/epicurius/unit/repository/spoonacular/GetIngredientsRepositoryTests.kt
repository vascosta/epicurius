package epicurius.unit.repository.spoonacular

import kotlinx.coroutines.runBlocking
import kotlin.test.Test
import kotlin.test.assertContains
import kotlin.test.assertEquals

class GetIngredientsRepositoryTests: SpoonacularRepositoryTest() {

    private val testIngredients = listOf(
        "apple",
        "applesauce",
        "apple juice",
        "apple cider",
        "apple jelly",
        "apple butter",
        "apple pie spice",
        "apple pie filling",
        "apple cider vinegar",
        "applewood smoked bacon"
    )

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