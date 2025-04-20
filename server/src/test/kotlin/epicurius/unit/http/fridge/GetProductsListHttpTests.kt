package epicurius.unit.http.fridge

import kotlinx.coroutines.runBlocking
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import org.mockito.kotlin.whenever
import org.springframework.http.HttpStatus

class GetProductsListHttpTests : FridgeHttpTest() {

    @Test
    fun `Should get products list successfully`() {
        // given a user with a fridge and a partial product name
        val authenticatedUser = testAuthenticatedUser
        val partialName = "app"

        // mock
        whenever(runBlocking { fridgeServiceMock.getProductsList(partialName) }).thenReturn(productsList)

        // when getting the products list
        val response = runBlocking { getProductsList(authenticatedUser, partialName) }

        // then the products list is retrieved successfully
        assertEquals(HttpStatus.OK, response.statusCode)
        assertEquals(productsList, response.body)
    }

    @Test
    fun `Should get empty products list when searching for non-existing product`() {
        // given a user with a fridge and a partial product name
        val authenticatedUser = testAuthenticatedUser
        val partialName = "non-existing-product"

        // mock
        whenever(runBlocking { fridgeServiceMock.getProductsList(partialName) }).thenReturn(emptyList())

        // when getting the products list
        val response = runBlocking { getProductsList(authenticatedUser, partialName) }

        // then the products list is empty
        assertEquals(HttpStatus.OK, response.statusCode)
        assertEquals(emptyList<String>(), response.body)
    }
}
