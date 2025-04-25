package epicurius.unit.http.fridge

import epicurius.domain.exceptions.ProductNotFound
import epicurius.domain.fridge.Fridge
import kotlinx.coroutines.runBlocking
import org.junit.jupiter.api.Test
import org.mockito.kotlin.whenever
import org.springframework.http.HttpStatus
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith

class RemoveFridgeProductControllerTests : FridgeHttpTest() {

    @Test
    fun `Should remove product from user's fridge successfully`() {
        // given a user with a fridge and a product to remove

        // mock
        whenever(
            fridgeServiceMock.removeProduct(
                testAuthenticatedUser.user.id,
                ENTRY_NUMBER
            )
        ).thenReturn(Fridge(emptyList()))

        // when removing the product from the fridge
        val response = runBlocking { removeFridgeProduct(testAuthenticatedUser, ENTRY_NUMBER) }

        // then the product is removed successfully
        assertEquals(HttpStatus.OK, response.statusCode)
        assertEquals(Fridge(emptyList()), response.body)
    }

    @Test
    fun `Should throw ProductNotFound exception when removing non-existing product`() {
        // given a user with a fridge and a non-existing product to remove
        val nonExistingEntryNumber = 9999

        // mock
        whenever(
            fridgeServiceMock.removeProduct(
                testAuthenticatedUser.user.id,
                nonExistingEntryNumber
            )
        ).thenThrow(ProductNotFound(nonExistingEntryNumber))

        // when removing the non-existing product from the fridge
        val exception = assertFailsWith<ProductNotFound> {
            removeFridgeProduct(testAuthenticatedUser, nonExistingEntryNumber)
        }

        // then the exception is thrown
        assertEquals(ProductNotFound(nonExistingEntryNumber).message, exception.message)
    }
}
