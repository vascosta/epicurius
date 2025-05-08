package epicurius.unit.http.fridge

import epicurius.domain.exceptions.ProductIsAlreadyOpen
import epicurius.domain.exceptions.ProductNotFound
import epicurius.domain.fridge.Fridge
import epicurius.http.fridge.models.input.UpdateProductInputModel
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import org.mockito.kotlin.whenever
import org.springframework.http.HttpStatus
import java.time.LocalDate
import kotlin.test.assertFailsWith
import kotlin.test.assertNotNull

class OpenFridgeProductControllerTests : FridgeHttpTest() {

    @Test
    fun `Should open an existing product without duration successfully`() {
        // given a user with a fridge and an existing product to open
        val open = openProductInputModel.openDate
        assertNotNull(open)
        val openInput = UpdateProductInputModel(openDate = LocalDate.now())
        val openedProduct = product.copy(
            entryNumber = ENTRY_NUMBER,
            openDate = open
        )

        // mock
        whenever(
            fridgeServiceMock.updateProductInfo(testAuthenticatedUser.user.id, ENTRY_NUMBER, openInput)
        ).thenReturn(Fridge(listOf(openedProduct)))
        whenever(authenticationRefreshHandlerMock.refreshToken(testAuthenticatedUser.token)).thenReturn(mockCookie)

        // when opening the product
        val response = updateProduct(testAuthenticatedUser, ENTRY_NUMBER, openInput, mockResponse)

        // then the product is opened successfully
        assertEquals(HttpStatus.OK, response.statusCode)
        assertEquals(Fridge(listOf(openedProduct)), response.body)
    }

    @Test
    fun `Should open an existing product with duration successfully`() {
        // given a user with a fridge and an existing product to open
        val open = openProductInputModel.openDate
        assertNotNull(open)
        val openedProduct = product.copy(
            entryNumber = ENTRY_NUMBER,
            openDate = open,
            expirationDate = open.plus(openProductInputModel.duration)
        )

        // mock
        whenever(
            fridgeServiceMock.updateProductInfo(testAuthenticatedUser.user.id, ENTRY_NUMBER, openProductInputModel)
        ).thenReturn(Fridge(listOf(openedProduct)))
        whenever(authenticationRefreshHandlerMock.refreshToken(testAuthenticatedUser.token)).thenReturn(mockCookie)

        // when opening the product
        val response = updateProduct(testAuthenticatedUser, ENTRY_NUMBER, openProductInputModel, mockResponse)

        // then the product is opened successfully
        assertEquals(HttpStatus.OK, response.statusCode)
        assertEquals(Fridge(listOf(openedProduct)), response.body)
    }

    @Test
    fun `Should open an existing product and update its quantity successfully`() {
        // given a user with a fridge, an existing product to open and an existing open product
        val open = openProductInputModel.openDate
        assertNotNull(open)
        val productToOpen = product.copy(quantity = 2)
        val openedProduct = product.copy(
            entryNumber = NEW_ENTRY_NUMBER,
            quantity = 1,
            openDate = openProductInputModel.openDate,
            expirationDate = open.plus(openProductInputModel.duration)
        )
        val newFridge = Fridge(listOf(productToOpen.copy(quantity = 1), openedProduct.copy(quantity = 2)))

        // mock
        whenever(
            fridgeServiceMock.updateProductInfo(testAuthenticatedUser.user.id, ENTRY_NUMBER, openProductInputModel)
        ).thenReturn(newFridge)
        whenever(authenticationRefreshHandlerMock.refreshToken(testAuthenticatedUser.token)).thenReturn(mockCookie)

        // when opening the product
        val response = updateProduct(testAuthenticatedUser, ENTRY_NUMBER, openProductInputModel, mockResponse)

        // then the product is opened successfully and its quantity is updated
        assertEquals(HttpStatus.OK, response.statusCode)
        assertEquals(newFridge, response.body)
    }

    @Test
    fun `Should throw ProductNotFound exception when opening a non-existing product`() {
        // given a user with a fridge and a non-existing product to open
        val open = openProductInputModel.openDate
        assertNotNull(open)
        val entryNumber = 999

        // mock
        whenever(
            fridgeServiceMock.updateProductInfo(testAuthenticatedUser.user.id, entryNumber, openProductInputModel)
        ).thenThrow(ProductNotFound(entryNumber))

        // when opening the product
        val response = assertFailsWith<ProductNotFound> {
            updateProduct(testAuthenticatedUser, entryNumber, openProductInputModel, mockResponse)
        }

        // then an exception is thrown
        assertEquals(ProductNotFound(entryNumber).message, response.message)
    }

    @Test
    fun `Should throw ProductIsAlreadyOpen exception when opening an already open product`() {
        // given a user with a fridge and an already open product
        val open = openProductInputModel.openDate
        assertNotNull(open)

        // mock
        whenever(
            fridgeServiceMock.updateProductInfo(testAuthenticatedUser.user.id, NEW_ENTRY_NUMBER, openProductInputModel)
        ).thenThrow(ProductIsAlreadyOpen())

        // when opening the already open product
        val response = assertFailsWith<ProductIsAlreadyOpen> {
            updateProduct(testAuthenticatedUser, NEW_ENTRY_NUMBER, openProductInputModel, mockResponse)
        }

        // then an exception is thrown
        assertEquals(ProductIsAlreadyOpen().message, response.message)
    }
}
