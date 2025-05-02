package epicurius.unit.services.ingredients

import epicurius.unit.services.ServiceTest
import kotlinx.coroutines.runBlocking
import org.junit.jupiter.api.Assertions.assertEquals
import org.mockito.kotlin.whenever
import kotlin.test.Test

class GetIngredientsServiceTests : ServiceTest() {

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
    fun `Should retrieve ingredients given a partial name successfully`() {
        // given a partial name
        val partialName = "app"

        // mock
        whenever(runBlocking { spoonacularRepositoryMock.getIngredients(partialName) }).thenReturn(testIngredients)

        // when retrieving the ingredients
        val ingredients = runBlocking { getIngredients(partialName) }

        // then the product list is retrieved successfully
        assertEquals(testIngredients, ingredients)
        assertEquals(testIngredients.size, ingredients.size)
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
