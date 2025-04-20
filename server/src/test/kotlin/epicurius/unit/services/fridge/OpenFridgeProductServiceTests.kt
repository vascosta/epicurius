package epicurius.unit.services.fridge

import epicurius.domain.exceptions.ProductIsAlreadyOpen
import epicurius.domain.exceptions.ProductNotFound
import epicurius.domain.fridge.Fridge
import epicurius.domain.fridge.Product
import kotlinx.coroutines.runBlocking
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import org.mockito.kotlin.whenever

class OpenFridgeProductServiceTests : FridgeServiceTest() {

    @Test
    fun `Should open product and add new one in fridge successfully`() {
        // given an existing product with ENTRY_NUMBER in the fridge and a new product info
        val openedProduct = productInfo.copy(openDate = open, expirationDate = newExpiration)
        val decreasedQuantity = updateProductInfo.copy(quantity = 0)
        val newFridge = Fridge(listOf(openedProduct.toProduct(NEW_ENTRY_NUMBER)))

        // mock
        whenever(
            jdbiFridgeRepositoryMock.checkIfProductExistsInFridge(
                USER_ID,
                ENTRY_NUMBER,
                null
            )
        ).thenReturn(product)
        whenever(jdbiFridgeRepositoryMock.checkIfProductIsOpen(USER_ID, ENTRY_NUMBER)).thenReturn(false)
        whenever(jdbiFridgeRepositoryMock.updateProduct(USER_ID, decreasedQuantity)).thenReturn(Fridge(emptyList()))
        whenever(fridgeDomainMock.calculateExpirationDate(open, duration)).thenReturn(newExpiration)
        whenever(jdbiFridgeRepositoryMock.addProduct(USER_ID, openedProduct)).thenReturn(newFridge)

        // when opening the product
        val updatedFridge = runBlocking { fridgeService.openProduct(USER_ID, ENTRY_NUMBER, openProductInputModel) }

        // then the product is opened
        assertEquals(product.productName, updatedFridge.products[0].productName)
        assertEquals(NEW_ENTRY_NUMBER, updatedFridge.products[0].entryNumber)
        assertEquals(product.quantity, updatedFridge.products[0].quantity)
        assertEquals(openProductInputModel.openDate, updatedFridge.products[0].openDate)
        assertEquals(newExpiration, updatedFridge.products[0].expirationDate)
    }

    @Test // needs correction
    fun `Should open product and update existing product in fridge successfully`() {
        // given an existing product with ENTRY_NUMBER in the fridge and a new product info
        val openedInfoProduct = productInfo.copy(openDate = open, expirationDate = newExpiration)
        val updatedFridge = Fridge(listOf(Product(PRODUCT_NAME, NEW_ENTRY_NUMBER, PRODUCT_QUANTITY + 1, open, newExpiration)))

        // mock
        whenever(
            jdbiFridgeRepositoryMock.checkIfProductExistsInFridge(
                USER_ID,
                ENTRY_NUMBER,
                null
            )
        ).thenReturn(product)
        whenever(jdbiFridgeRepositoryMock.checkIfProductIsOpen(USER_ID, ENTRY_NUMBER)).thenReturn(false)
        whenever(jdbiFridgeRepositoryMock.updateProduct(USER_ID, decreaseQuantity)).thenReturn(Fridge(emptyList()))
        whenever(fridgeDomainMock.calculateExpirationDate(open, duration)).thenReturn(newExpiration)
        whenever(
            jdbiFridgeRepositoryMock.checkIfProductExistsInFridge(
                USER_ID,
                null,
                openedProductInfo
            )
        ).thenReturn(openedProduct)
        whenever(jdbiFridgeRepositoryMock.updateProduct(USER_ID, increaseQuantity)).thenReturn(updatedFridge)

        // when opening the product
        val newFridge = fridgeService.openProduct(USER_ID, ENTRY_NUMBER, openProductInputModel)

        // then the product is opened
        assertEquals(product.productName, newFridge.products[0].productName)
        assertEquals(ENTRY_NUMBER, newFridge.products[0].entryNumber)
        assertEquals(product.quantity, newFridge.products[0].quantity)
        assertEquals(openProductInputModel.openDate, newFridge.products[0].openDate)
        assertEquals(newExpiration, newFridge.products[0].expirationDate)
    }

    @Test
    fun `Should throw ProductNotFound exception when product does not exist in fridge`() {
        // given a non-existing product
        val entryNumber = 999

        // mock
        whenever(jdbiFridgeRepositoryMock.checkIfProductExistsInFridge(USER_ID, entryNumber, null)).thenReturn(null)

        // when opening the product
        val exception = assertThrows<ProductNotFound> {
            runBlocking { fridgeService.openProduct(USER_ID, entryNumber, openProductInputModel) }
        }

        // then the exception is thrown
        assertEquals(ProductNotFound(entryNumber).message, exception.message)
    }

    @Test
    fun `Should throw ProductIsAlreadyOpen exception when product is already open is fridge`() {
        // given an existing product with ENTRY_NUMBER in the fridge
        val entryNumber = 1

        // mock
        whenever(jdbiFridgeRepositoryMock.checkIfProductExistsInFridge(USER_ID, entryNumber, null)).thenReturn(product)
        whenever(jdbiFridgeRepositoryMock.checkIfProductIsOpen(USER_ID, entryNumber)).thenReturn(true)

        // when opening the product
        val exception = assertThrows<ProductIsAlreadyOpen> {
            runBlocking { fridgeService.openProduct(USER_ID, entryNumber, openProductInputModel) }
        }

        // then the exception is thrown
        assertEquals(ProductIsAlreadyOpen().message, exception.message)
    }
}
