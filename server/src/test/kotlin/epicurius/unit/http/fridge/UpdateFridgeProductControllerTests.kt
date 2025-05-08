package epicurius.unit.http.fridge

import epicurius.domain.exceptions.ProductIsAlreadyOpen
import epicurius.domain.exceptions.ProductNotFound
import epicurius.domain.fridge.Fridge
import org.junit.jupiter.api.Assertions.assertEquals
import org.mockito.kotlin.whenever
import org.springframework.http.HttpStatus
import kotlin.test.Test
import kotlin.test.assertFailsWith

class UpdateFridgeProductControllerTests : FridgeHttpTest() {

    @Test
    fun `Should update product quantity successfully`() {
        // given a user with a fridge and an existing product to update
        val updateInput = updateProductInputModel.copy(expirationDate = null)
        val updatedProduct = product.copy(
            entryNumber = ENTRY_NUMBER,
            quantity = NEW_QUANTITY
        )

        // mock
        whenever(
            fridgeServiceMock.updateProductInfo(testAuthenticatedUser.user.id, ENTRY_NUMBER, updateInput)
        ).thenReturn(Fridge(listOf(updatedProduct)))
        whenever(authenticationRefreshHandlerMock.refreshToken(testAuthenticatedUser.token)).thenReturn(mockCookie)

        // when updating the product in the fridge
        val response = updateProduct(testAuthenticatedUser, ENTRY_NUMBER, updateInput, mockResponse)

        // then the product is updated successfully
        assertEquals(HttpStatus.OK, response.statusCode)
        assertEquals(Fridge(listOf(updatedProduct)), response.body)
    }

    @Test
    fun `Should update product expiration date successfully`() {
        // given a user with a fridge and an existing product to update
        val updateInput = updateProductInputModel.copy(quantity = null)
        val updatedProduct = product.copy(
            entryNumber = ENTRY_NUMBER,
            expirationDate = newExpirationDate
        )

        // mock
        whenever(
            fridgeServiceMock.updateProductInfo(testAuthenticatedUser.user.id, ENTRY_NUMBER, updateInput)
        ).thenReturn(Fridge(listOf(updatedProduct)))
        whenever(authenticationRefreshHandlerMock.refreshToken(testAuthenticatedUser.token)).thenReturn(mockCookie)

        // when updating the product in the fridge
        val response = updateProduct(testAuthenticatedUser, ENTRY_NUMBER, updateInput, mockResponse)

        // then the product is updated successfully
        assertEquals(HttpStatus.OK, response.statusCode)
        assertEquals(Fridge(listOf(updatedProduct)), response.body)
    }

    @Test
    fun `Should update a product in user's fridge successfully`() {
        // given a user with a fridge and an existing product to update
        val updatedProduct = product.copy(
            entryNumber = ENTRY_NUMBER,
            quantity = NEW_QUANTITY,
            expirationDate = newExpirationDate
        )

        // mock
        whenever(
            fridgeServiceMock.updateProductInfo(testAuthenticatedUser.user.id, ENTRY_NUMBER, updateProductInputModel)
        ).thenReturn(Fridge(listOf(updatedProduct)))
        whenever(authenticationRefreshHandlerMock.refreshToken(testAuthenticatedUser.token)).thenReturn(mockCookie)

        // when updating the product in the fridge
        val response = updateProduct(testAuthenticatedUser, ENTRY_NUMBER, updateProductInputModel, mockResponse)

        // then the product is updated successfully
        assertEquals(HttpStatus.OK, response.statusCode)
        assertEquals(Fridge(listOf(updatedProduct)), response.body)
    }

    @Test
    fun `Should throw ProductNotFound exception when updating a non-existing product`() {
        // given a user with a fridge and a non-existing product to update
        val nonExistingEntryNumber = 999

        // mock
        whenever(
            fridgeServiceMock.updateProductInfo(testAuthenticatedUser.user.id, nonExistingEntryNumber, updateProductInputModel)
        ).thenThrow(ProductNotFound(nonExistingEntryNumber))

        // when trying to update the product in the fridge
        val exception = assertFailsWith<ProductNotFound> {
            updateProduct(testAuthenticatedUser, nonExistingEntryNumber, updateProductInputModel, mockResponse)
        }

        // then the exception is thrown
        assertEquals(ProductNotFound(nonExistingEntryNumber).message, exception.message)
    }

    @Test
    fun `Should throw ProductIsAlreadyOpen when updating expiration date of an open product`() {
        // given a user with a fridge and an open product to update
        // mock
        whenever(
            fridgeServiceMock.updateProductInfo(testAuthenticatedUser.user.id, NEW_ENTRY_NUMBER, updateProductInputModel)
        ).thenThrow(ProductIsAlreadyOpen())

        // when trying to update the product in the fridge
        val exception = assertFailsWith<ProductIsAlreadyOpen> {
            updateProduct(testAuthenticatedUser, NEW_ENTRY_NUMBER, updateProductInputModel, mockResponse)
        }

        // then the exception is thrown
        assertEquals(ProductIsAlreadyOpen().message, exception.message)
    }
}
