package epicurius.unit.http.fridge

import epicurius.domain.exceptions.InvalidProduct
import epicurius.domain.fridge.Fridge
import epicurius.http.controllers.fridge.models.input.AddProductInputModel
import kotlinx.coroutines.runBlocking
import org.junit.jupiter.api.Test
import org.mockito.kotlin.whenever
import org.springframework.http.HttpStatus
import java.time.LocalDate
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith

class AddProductFridgeControllerTests : FridgeHttpTest() {

    @Test
    fun `Should add new product to user's fridge successfully`() {
        // given a user with a fridge, a product to add and the user's fridge
        val fridge = Fridge(listOf(product))

        // mock
        whenever(
            runBlocking {
                fridgeServiceMock.addProduct(testAuthenticatedUser.user.id, addProductInputModel)
            }
        ).thenReturn(fridge)

        // when adding the product to the fridge
        val response = runBlocking { addProduct(testAuthenticatedUser, addProductInputModel) }

        // then the product is added successfully
        assertEquals(HttpStatus.CREATED, response.statusCode)
        assertEquals(fridge, response.body)
    }

    @Test
    fun `Should add existing product to user's fridge successfully`() {
        // given a user with a fridge and an existing product to add

        // mock
        whenever(
            runBlocking {
                fridgeServiceMock.addProduct(testAuthenticatedUser.user.id, addProductInputModel)
            }
        ).thenReturn(Fridge(listOf(product)))

        // when adding the product to the fridge
        val oldFridge = runBlocking { addProduct(testAuthenticatedUser, addProductInputModel) }

        // then the existing product is added successfully
        assertEquals(HttpStatus.CREATED, oldFridge.statusCode)
        assertEquals(Fridge(listOf(product)), oldFridge.body)

        // mock
        whenever(
            runBlocking {
                fridgeServiceMock.addProduct(testAuthenticatedUser.user.id, addProductInputModel)
            }
        ).thenReturn(Fridge(listOf(product.copy(quantity = 2))))

        // when adding the existing product to the fridge
        val newFridge = runBlocking { addProduct(testAuthenticatedUser, addProductInputModel) }

        // then the product is updated successfully
        assertEquals(HttpStatus.CREATED, newFridge.statusCode)
        assertEquals(Fridge(listOf(product.copy(quantity = 2))), newFridge.body)
    }

    @Test
    fun `Should throw InvalidProduct exception when adding invalid product`() {
        // given a user with a fridge and an invalid product to add
        val invalidProductInputModel = AddProductInputModel(
            name = "invalid product",
            quantity = 1,
            expirationDate = LocalDate.now().plusDays(10)
        )

        // mock
        whenever(
            runBlocking {
                fridgeServiceMock.addProduct(testAuthenticatedUser.user.id, invalidProductInputModel)
            }
        ).thenThrow(InvalidProduct())

        // when adding the invalid product to the fridge
        // then the product cannot be added and throws InvalidProduct exception
        assertFailsWith<InvalidProduct> {
            runBlocking { addProduct(testAuthenticatedUser, invalidProductInputModel) }
        }
    }
}
