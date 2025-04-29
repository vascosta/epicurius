package epicurius.unit.services.fridge

import epicurius.domain.exceptions.ProductNotFound
import epicurius.domain.fridge.Fridge
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import org.mockito.kotlin.whenever
import kotlin.test.assertEquals

class RemoveFridgeProductServiceTests : FridgeServiceTest() {

    @Test
    fun `Should remove product from user's fridge`() {
        // given a USER_ID and an ENTRY_NUMBER
        // mock
        whenever(
            jdbiFridgeRepositoryMock.checkIfProductExistsInFridge(
                USER_ID,
                ENTRY_NUMBER,
                null
            )
        ).thenReturn(product)
        whenever(jdbiFridgeRepositoryMock.removeProduct(USER_ID, ENTRY_NUMBER)).thenReturn(Fridge(emptyList()))

        // when removing the product from user's fridge
        val fridge = removeProduct(USER_ID, ENTRY_NUMBER)

        // then the product should be removed from the fridge
        assert(fridge.products.isEmpty())
    }

    @Test
    fun `Should throw ProductNotFound exception when product is not found in fridge`() {
        // given a USER_ID and an entry number
        val entryNumber = 9999
        // mock
        whenever(
            jdbiFridgeRepositoryMock.checkIfProductExistsInFridge(
                USER_ID,
                entryNumber,
                null
            )
        ).thenReturn(null)

        // when removing the product from user's fridge
        val exception = assertThrows<ProductNotFound> { removeProduct(USER_ID, entryNumber) }

        // then the exception should be thrown
        assertEquals(ProductNotFound(entryNumber).message, exception.message)
    }
}
