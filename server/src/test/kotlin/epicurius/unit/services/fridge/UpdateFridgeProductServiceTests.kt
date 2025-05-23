package epicurius.unit.services.fridge

import epicurius.domain.exceptions.ProductIsAlreadyOpen
import epicurius.domain.exceptions.ProductNotFound
import epicurius.domain.fridge.Fridge
import kotlinx.coroutines.runBlocking
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import org.mockito.kotlin.whenever
import java.time.LocalDate
import kotlin.test.assertEquals

class UpdateFridgeProductServiceTests : FridgeServiceTest() {

    @Test
    fun `Should update product info`() {
        // given an existing product and a user's fridge
        val existingProduct = product
        val newFridge = Fridge(listOf(existingProduct.copy(quantity = NEW_QUANTITY, expirationDate = newExpirationDate)))

        // mock
        whenever(
            jdbiFridgeRepositoryMock.checkIfProductExistsInFridge(
                USER_ID,
                ENTRY_NUMBER,
                null
            )
        ).thenReturn(product)
        whenever(jdbiFridgeRepositoryMock.checkIfProductIsOpen(USER_ID, ENTRY_NUMBER)).thenReturn(false)
        whenever(jdbiFridgeRepositoryMock.updateProduct(USER_ID, updateProductInfo)).thenReturn(newFridge)

        // when updating the product info
        val updatedFridge =
            runBlocking {
                updateProductInfo(USER_ID, ENTRY_NUMBER, updateProductInputModel)
            }

        // then the product info is updated
        assertEquals(product.name, updatedFridge.products[0].name)
        assertEquals(product.entryNumber, updatedFridge.products[0].entryNumber)
        assertEquals(updateProductInfo.quantity, updatedFridge.products[0].quantity)
        assertEquals(updateProductInfo.expirationDate, updatedFridge.products[0].expirationDate)
    }

    @Test
    fun `Should throw ProductNotFound exception when product does not exist in fridge`() {
        // given a non-existing product ENTRY_NUMBER
        val nonExistingEntryNumber = 9999

        // mock
        whenever(
            jdbiFridgeRepositoryMock.checkIfProductExistsInFridge(
                USER_ID,
                nonExistingEntryNumber,
                null
            )
        ).thenReturn(null)

        // when updating the product info
        // then the product cannot be updated and throws ProductNotFound exception
        assertThrows<ProductNotFound> {
            runBlocking { updateProductInfo(USER_ID, nonExistingEntryNumber, updateProductInputModel) }
        }
    }

    @Test
    fun `Should throw ProductIsAlreadyOpen exception when product is already open`() {
        // given an existing product in fridge with open date
        val existingProduct = product.copy(openDate = LocalDate.now().minusDays(1))

        // mock
        whenever(
            jdbiFridgeRepositoryMock.checkIfProductExistsInFridge(
                USER_ID,
                ENTRY_NUMBER,
                null
            )
        ).thenReturn(existingProduct)
        whenever(jdbiFridgeRepositoryMock.checkIfProductIsOpen(USER_ID, ENTRY_NUMBER)).thenReturn(true)

        // when updating the product info
        // then the product cannot be updated and throws ProductIsAlreadyOpen exception
        assertThrows<ProductIsAlreadyOpen> {
            runBlocking { updateProductInfo(USER_ID, ENTRY_NUMBER, updateProductInputModel) }
        }
    }
}
