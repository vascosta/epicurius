package epicurius.unit.repository.fridge

import epicurius.domain.fridge.ProductInfo
import epicurius.domain.fridge.UpdateProductInfo
import epicurius.domain.user.User
import epicurius.unit.repository.RepositoryTest
import epicurius.utils.createTestUser
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import java.time.LocalDate
import kotlin.test.assertEquals
import kotlin.test.assertNotNull
import kotlin.test.assertNull
import kotlin.test.assertTrue

class FridgeRepositoryTest : RepositoryTest() {

    private lateinit var publicTestUser: User
    private lateinit var privateTestUser: User

    @BeforeEach
    fun setup() {
        publicTestUser = createTestUser(tm)
        privateTestUser = createTestUser(tm, true)
    }

    @Test
    fun `Retrieve user fridge successfully`() {
        // given a user
        val user = privateTestUser

        // when retrieving the user's fridge
        val fridge = getFridge(user.id)

        // then the fridge should be empty
        assertTrue(fridge.products.isEmpty())
    }

    @Test
    fun `Add product to user fridge successfully`() {
        // given a user
        val user = publicTestUser

        // when adding a product to the user's fridge
        val product = ProductInfo(
            productName = "Milk",
            quantity = 1,
            openDate = null,
            expirationDate = LocalDate.now().plusDays(7)
        )
        val fridge = addProduct(user.id, product)

        // then the fridge should contain the added product
        assertEquals(1, fridge.products.size)
        assertEquals(product.productName, fridge.products.first().productName)
        assertEquals(product.quantity, fridge.products.first().quantity)
        assertEquals(product.openDate, fridge.products.first().openDate)
        assertEquals(product.expirationDate, fridge.products.first().expirationDate)
    }

    @Test
    fun `Update product in user fridge successfully`() {
        // given a user
        val user = publicTestUser

        // and a product in the user's fridge
        val product = ProductInfo(
            productName = "Apple",
            quantity = 1,
            openDate = null,
            expirationDate = LocalDate.now().plusDays(7)
        )
        val fridge = addProduct(user.id, product)

        val entryNumber = fridge.products.first().entryNumber

        // when updating the product in the user's fridge
        val newQuantity = 2
        val newExpirationDate = LocalDate.now().plusDays(6)
        val updatedProduct = UpdateProductInfo(
            entryNumber = entryNumber,
            quantity = newQuantity,
            expirationDate = newExpirationDate
        )
        val updatedFridge = updateProduct(user.id, updatedProduct)

        // then the fridge should contain the updated product
        assertEquals(1, updatedFridge.products.size)
        assertEquals(product.productName, updatedFridge.products.first().productName)
        assertEquals(newQuantity, updatedFridge.products.first().quantity)
        assertEquals(product.openDate, updatedFridge.products.first().openDate)
        assertEquals(newExpirationDate, updatedFridge.products.first().expirationDate)
    }

    @Test
    fun `Remove product from user fridge successfully`() {
        // given a user
        val user = publicTestUser

        // and a product in the user's fridge
        val product = ProductInfo(
            productName = "Peach",
            quantity = 1,
            openDate = null,
            expirationDate = LocalDate.now().plusDays(7)
        )
        val fridge = addProduct(user.id, product)

        val entryNumber = fridge.products.first().entryNumber

        // when removing the product from the user's fridge
        val removedFridge = removeProduct(user.id, entryNumber)

        // then the fridge should be empty
        assertTrue(removedFridge.products.isEmpty())
    }

    @Test
    fun `Check if product exists in user fridge successfully without entry number`() {
        // given a user
        val user = publicTestUser

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
        assertEquals(product.productName, existingProduct.productName)
        assertEquals(product.quantity, existingProduct.quantity)
        assertEquals(product.openDate, existingProduct.openDate)
        assertEquals(product.expirationDate, existingProduct.expirationDate)

        // and the non-existing product should not be found
        assertNull(nonExistingProduct)
    }

    @Test
    fun `Check if product exists in user fridge successfully with entry number`() {
        // given a user
        val user = publicTestUser

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
        assertEquals(product.productName, existingProduct.productName)
        assertEquals(product.quantity, existingProduct.quantity)
        assertEquals(product.openDate, existingProduct.openDate)
        assertEquals(product.expirationDate, existingProduct.expirationDate)

        // and the non-existing product should not be found
        assertNull(nonExistingProduct)
    }

    @Test
    fun `Check if product is open successfully`() {
        // given a user
        val user = publicTestUser

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

        val isOpen = checkIfProductIsOpen(user.id, check.entryNumber)

        // then the product should be open
        assertTrue(isOpen)
    }
}
