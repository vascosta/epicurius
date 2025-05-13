package epicurius.unit.repository.fridge

import epicurius.domain.fridge.ProductInfo
import epicurius.utils.createTestUser
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertNotNull
import org.junit.jupiter.api.Assertions.assertNull
import org.junit.jupiter.api.Assertions.assertTrue
import java.time.LocalDate
import kotlin.test.Test

class ChecksFridgeRepositoryTests : FridgeRepositoryTest() {

    @Test
    fun `Check if product exists in user fridge successfully without entry number`() {
        // given a user
        val user = createTestUser(tm)

        // and a product in the user's fridge
        val product = ProductInfo(
            productName = "Cream",
            quantity = 1,
            openDate = null,
            expirationDate = LocalDate.now().plusDays(7)
        )
        addProduct(user.id, product)

        // when checking if the product exists in the user's fridge
        val existingProduct = checkIfProductExistsInFridge(user.id, null, product)
        val nonExistingProduct = checkIfProductExistsInFridge(
            user.id,
            null,
            ProductInfo(
                productName = "Eggs",
                quantity = 1,
                openDate = null,
                expirationDate = LocalDate.now().plusDays(7)
            )
        )

        // then the existing product should be found
        assertNotNull(existingProduct)
        assertEquals(product.productName, existingProduct!!.name)
        assertEquals(product.quantity, existingProduct.quantity)
        assertEquals(product.openDate, existingProduct.openDate)
        assertEquals(product.expirationDate, existingProduct.expirationDate)

        // and the non-existing product should not be found
        assertNull(nonExistingProduct)
    }

    @Test
    fun `Check if product exists in user fridge successfully with entry number`() {
        // given a user
        val user = createTestUser(tm)

        // and a product in the user's fridge
        val product = ProductInfo(
            productName = "Milk",
            quantity = 1,
            openDate = null,
            expirationDate = LocalDate.now().plusDays(7)
        )
        val fridge = addProduct(user.id, product)

        val entryNumber = fridge.products.first().entryNumber

        // when checking if the product exists in the user's fridge
        val existingProduct = checkIfProductExistsInFridge(user.id, entryNumber, null)
        val nonExistingProduct = checkIfProductExistsInFridge(user.id, 9999999, null)

        // then the existing product should be found
        assertNotNull(existingProduct)
        assertEquals(product.productName, existingProduct!!.name)
        assertEquals(product.quantity, existingProduct.quantity)
        assertEquals(product.openDate, existingProduct.openDate)
        assertEquals(product.expirationDate, existingProduct.expirationDate)

        // and the non-existing product should not be found
        assertNull(nonExistingProduct)
    }

    @Test
    fun `Check if product is open successfully`() {
        // given a user
        val user = createTestUser(tm)

        // and a product in the user's fridge
        val product = ProductInfo(
            productName = "Orange",
            quantity = 1,
            openDate = LocalDate.now().minusDays(1),
            expirationDate = LocalDate.now().plusDays(7)
        )
        addProduct(user.id, product)

        // when retrieving the product and checking if the product is open
        val check = checkIfProductExistsInFridge(user.id, null, product)
        assertNotNull(check)

        val isOpen = checkIfProductIsOpen(user.id, check!!.entryNumber)

        // then the product should be open
        assertTrue(isOpen)
    }
}
