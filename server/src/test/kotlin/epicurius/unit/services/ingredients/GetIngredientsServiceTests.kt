package epicurius.unit.services.ingredients

import kotlinx.coroutines.runBlocking
import org.junit.jupiter.api.Assertions.assertEquals
import org.mockito.kotlin.whenever
import kotlin.test.Test

class GetIngredientsServiceTests : IngredientsServiceTest() {

    @Test
    fun `Should retrieve ingredients given a partial name successfully`() {
        // given a partial name (PARTIAL_NAME)

        // mock
        whenever(runBlocking { spoonacularRepositoryMock.getIngredients(PARTIAL_NAME) }).thenReturn(testIngredients)

        // when retrieving the ingredients
        val ingredients = runBlocking { getIngredients(PARTIAL_NAME) }

        // then the product list is retrieved successfully
        assertEquals(testIngredients, ingredients)
        assertEquals(testIngredients.size, ingredients.size)
        assertEquals(testIngredients[0], ingredients[0])
        assertEquals(testIngredients[1], ingredients[1])
        assertEquals(testIngredients[2], ingredients[2])
        assertEquals(testIngredients[3], ingredients[3])
        assertEquals(testIngredients[4], ingredients[4])
        assertEquals(testIngredients[5], ingredients[5])
        assertEquals(testIngredients[6], ingredients[6])
        assertEquals(testIngredients[7], ingredients[7])
        assertEquals(testIngredients[8], ingredients[8])
        assertEquals(testIngredients[9], ingredients[9])
    }

    @Test
    fun `Should retrieve an empty list of ingredients when no ingredients are found`() {
        // given a partial name
        val partial = "nonexistent"

        // mock
        whenever(runBlocking { spoonacularRepositoryMock.getIngredients(partial) }).thenReturn(emptyList())

        // when retrieving the ingredients
        val ingredients = runBlocking { getIngredients(partial) }

        // then an empty list is retrieved successfully
        assertEquals(emptyList<String>(), ingredients)
    }
}
