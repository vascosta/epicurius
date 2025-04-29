package epicurius.unit.services.ingredients

import kotlinx.coroutines.runBlocking
import org.junit.jupiter.api.Assertions.assertEquals
import org.mockito.kotlin.whenever
import kotlin.test.Test

class GetProductListServiceTests : IngredientsServiceTest() {

    @Test
    fun `Should retrieve a list of products successfully`() {
        // given a partial product name and a list of products

        // mock
        whenever(runBlocking { spoonacularRepositoryMock.getIngredients(PARTIAL) }).thenReturn(productsList)

        // when retrieving the product list
        val retrievedProductList = runBlocking { getIngredientsList(PARTIAL) }

        // then the product list is retrieved successfully
        assertEquals(productsList, retrievedProductList)
        assertEquals(productsList.size, retrievedProductList.size)
        assertEquals(productsList[0], retrievedProductList[0])
        assertEquals(productsList[1], retrievedProductList[1])
        assertEquals(productsList[2], retrievedProductList[2])
        assertEquals(productsList[3], retrievedProductList[3])
        assertEquals(productsList[4], retrievedProductList[4])
        assertEquals(productsList[5], retrievedProductList[5])
        assertEquals(productsList[6], retrievedProductList[6])
        assertEquals(productsList[7], retrievedProductList[7])
        assertEquals(productsList[8], retrievedProductList[8])
        assertEquals(productsList[9], retrievedProductList[9])
    }

    @Test
    fun `Should retrieve an empty list of products successfully`() {
        // given a partial product name and an empty list of products
        val partial = "nonexistent"

        // mock
        whenever(runBlocking { spoonacularRepositoryMock.getIngredients(partial) }).thenReturn(emptyList())

        // when retrieving the product list
        val retrievedProductList = runBlocking { getIngredientsList(partial) }

        // then the product list is retrieved successfully
        assertEquals(emptyList<String>(), retrievedProductList)
    }
}
