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
        whenever(runBlocking { spoonacularRepositoryMock.getIngredients(PARTIAL_NAME) }).thenReturn(productsList)

        // when retrieving the ingredients
        val ingredients = runBlocking { getIngredientsList(PARTIAL_NAME) }

        // then the product list is retrieved successfully
        assertEquals(productsList, ingredients)
        assertEquals(productsList.size, ingredients.size)
        assertEquals(productsList[0], ingredients[0])
        assertEquals(productsList[1], ingredients[1])
        assertEquals(productsList[2], ingredients[2])
        assertEquals(productsList[3], ingredients[3])
        assertEquals(productsList[4], ingredients[4])
        assertEquals(productsList[5], ingredients[5])
        assertEquals(productsList[6], ingredients[6])
        assertEquals(productsList[7], ingredients[7])
        assertEquals(productsList[8], ingredients[8])
        assertEquals(productsList[9], ingredients[9])
    }

    @Test
    fun `Should retrieve an empty list of ingredients when no ingredients are found`() {
        // given a partial name
        val partial = "nonexistent"

        // mock
        whenever(runBlocking { spoonacularRepositoryMock.getIngredients(partial) }).thenReturn(emptyList())

        // when retrieving the ingredients
        val ingredients = runBlocking { getIngredientsList(partial) }

        // then an empty list is retrieved successfully
        assertEquals(emptyList<String>(), ingredients)
    }
}
