package epicurius.unit.services.fridge

import epicurius.domain.exceptions.InvalidProduct
import epicurius.domain.fridge.Fridge
import epicurius.domain.fridge.UpdateProductInfo
import kotlinx.coroutines.runBlocking
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import org.mockito.kotlin.whenever
import kotlin.test.assertEquals

class AddFridgeProductServiceTests : FridgeServiceTest() {

    @Test
    fun `Should add a new product to user's fridge successfully`() {
        // given a new fridge and a valid
        val newFridge = Fridge(listOf(product))
        val validProductName = newFridge.products[0].name

        // mock
        whenever(runBlocking { spoonacularRepositoryMock.getIngredients(validProductName) }).thenReturn(productsList)
        whenever(jdbiFridgeRepositoryMock.addProduct(USER_ID, productInfo)).thenReturn(newFridge)

        // when adding the new product to fridge
        val retrievedFridge = runBlocking { addProduct(USER_ID, productInputModel) }

        // then the product is added to the fridge
        assertEquals(newFridge.products.size, retrievedFridge.products.size)
        assertEquals(newFridge.products[0].name, retrievedFridge.products[0].name)
        assertEquals(newFridge.products[0].quantity, retrievedFridge.products[0].quantity)
        assertEquals(newFridge.products[0].openDate, retrievedFridge.products[0].openDate)
        assertEquals(newFridge.products[0].expirationDate, retrievedFridge.products[0].expirationDate)
    }

    @Test
    fun `Should add an existing product to user's successfully`() {
        // given a product info, a new product info and a fridge with an existing product
        val existingProduct = productInfo
        val newProduct = productInfo.copy(quantity = 2)
        val oldFridge = Fridge(listOf(product))
        val newFridge = Fridge(listOf(product.copy(quantity = 3)))
        val validProductName = newFridge.products[0].name
        val updateProductInfo = UpdateProductInfo(
            entryNumber = ENTRY_NUMBER,
            quantity = existingProduct.quantity + newProduct.quantity
        )

        // mock
        whenever(runBlocking { spoonacularRepositoryMock.getIngredients(validProductName) }).thenReturn(productsList)
        whenever(
            jdbiFridgeRepositoryMock.checkIfProductExistsInFridge(
                USER_ID,
                null,
                newProduct
            )
        ).thenReturn(product)
        whenever(jdbiFridgeRepositoryMock.addProduct(USER_ID, productInfo)).thenReturn(oldFridge)
        whenever(jdbiFridgeRepositoryMock.addProduct(USER_ID, newProduct)).thenReturn(newFridge)
        whenever(jdbiFridgeRepositoryMock.updateProduct(USER_ID, updateProductInfo)).thenReturn(newFridge)

        // when adding the new product to fridge
        val retrievedFridge = runBlocking { addProduct(USER_ID, productInputModel) }

        // when adding the existing product to fridge
        val retrievedExistingProduct =
            runBlocking {
                addProduct(USER_ID, productInputModel.copy(quantity = 2))
            }

        // then the existing product in the fridge is updated
        assertEquals(retrievedFridge.products.size, retrievedExistingProduct.products.size)
        assertEquals(retrievedFridge.products[0].name, retrievedExistingProduct.products[0].name)
        assertEquals(existingProduct.quantity + newProduct.quantity, retrievedExistingProduct.products[0].quantity)
        assertEquals(retrievedFridge.products[0].openDate, retrievedExistingProduct.products[0].openDate)
        assertEquals(retrievedFridge.products[0].expirationDate, retrievedExistingProduct.products[0].expirationDate)
    }

    @Test
    fun `Should throw InvalidProduct exception when adding an invalid product to fridge`() {
        // given an invalid product name and the product input model
        val invalidProductName = "invalid-product-name"
        val invalidProductInputModel = productInputModel.copy(productName = invalidProductName)

        // mock
        whenever(runBlocking { spoonacularRepositoryMock.getIngredients(invalidProductName) }).thenReturn(emptyList())

        // when adding the invalid product to fridge
        // then the product cannot be added and throws InvalidProduct exception
        assertThrows<InvalidProduct> {
            runBlocking { addProduct(USER_ID, invalidProductInputModel) }
        }
    }
}
