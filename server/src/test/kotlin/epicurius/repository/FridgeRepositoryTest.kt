package epicurius.repository

import epicurius.domain.fridge.ProductInfo
import epicurius.domain.fridge.UpdateProductInfo
import org.junit.jupiter.api.Test
import java.time.LocalDate
import java.time.ZoneId
import java.util.Date
import kotlin.test.assertEquals
import kotlin.test.assertNotNull
import kotlin.test.assertNull
import kotlin.test.assertTrue

class FridgeRepositoryTest : RepositoryTest() {

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
            expirationDate = Date.from(
                LocalDate.now().plusDays(7).atStartOfDay(ZoneId.systemDefault()).toInstant()
            )
        )
        val fridge = addProduct(user.id, product)

        // then the fridge should contain the added product
        assertTrue(fridge.products.isNotEmpty())

        val productInfoList = fridge.products.map {
            ProductInfo(it.productName, it.quantity, it.openDate, it.expirationDate)
        }

        assertTrue(productInfoList.contains(product))
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
            expirationDate = Date.from(
                LocalDate.now().plusDays(7).atStartOfDay(ZoneId.systemDefault()).toInstant()
            )
        )
        val fridge = addProduct(user.id, product)

        val entryNumber = fridge.products.first {
            it.productName == product.productName && it.quantity == product.quantity &&
            it.openDate == product.openDate && it.expirationDate == product.expirationDate
        }.entryNumber

        // when updating the product in the user's fridge
        val newQuantity = 2
        val newExpirationDate = Date.from(
            LocalDate.now().plusDays(6).atStartOfDay(ZoneId.systemDefault()).toInstant()
        )
        val updatedProduct = UpdateProductInfo(
            entryNumber = entryNumber,
            quantity = newQuantity,
            expirationDate = newExpirationDate
        )
        val updatedFridge = updateProduct(user.id, updatedProduct)

        // then the fridge should contain the updated product
        assertTrue(fridge.products.isNotEmpty())

        val productInfoList = updatedFridge.products.map {
            ProductInfo(it.productName, it.quantity, it.openDate, it.expirationDate)
        }

        assertTrue(
            productInfoList.contains(
                ProductInfo(
                    productName = product.productName,
                    quantity = newQuantity,
                    openDate = null,
                    expirationDate = newExpirationDate
                )
            )
        )
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
            expirationDate = Date.from(
                LocalDate.now().plusDays(7).atStartOfDay(ZoneId.systemDefault()).toInstant()
            )
        )
        val fridge = addProduct(user.id, product)

        val entryNumber = fridge.products.first {
            it.productName == product.productName && it.quantity == product.quantity &&
            it.openDate == product.openDate && it.expirationDate == product.expirationDate
        }.entryNumber

        // when removing the product from the user's fridge
        val removedFridge = removeProduct(user.id, entryNumber)

        // then the fridge should be empty

        val productInfoList = removedFridge.products.map {
            ProductInfo(it.productName, it.quantity, it.openDate, it.expirationDate)
        }

        assertTrue(!productInfoList.contains(product))
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
            expirationDate = Date.from(
                LocalDate.now().plusDays(7).atStartOfDay(ZoneId.systemDefault()).toInstant()
            )
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
                expirationDate = Date.from(
                    LocalDate.now().plusDays(7).atStartOfDay(ZoneId.systemDefault()).toInstant()
                )
            )
        )

        // then the existing product should be found
        assertNotNull(existingProduct)
        assertEquals(existingProduct.productName, product.productName)
        assertEquals(existingProduct.quantity, product.quantity)
        assertEquals(existingProduct.openDate, product.openDate)
        assertEquals(existingProduct.expirationDate, product.expirationDate)

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
            expirationDate = Date.from(
                LocalDate.now().plusDays(7).atStartOfDay(ZoneId.systemDefault()).toInstant()
            )
        )
        val fridge = addProduct(user.id, product)

        val entryNumber = fridge.products.first {
            it.productName == product.productName && it.quantity == product.quantity &&
            it.openDate == product.openDate && it.expirationDate == product.expirationDate
        }.entryNumber

        // when checking if the product exists in the user's fridge
        val existingProduct = checkIfProductExistsInFridge(user.id, entryNumber, null)
        val nonExistingProduct = checkIfProductExistsInFridge(user.id, 9999999, null)

        // then the existing product should be found
        assertNotNull(existingProduct)
        assertEquals(existingProduct.productName, product.productName)
        assertEquals(existingProduct.quantity, product.quantity)
        assertEquals(existingProduct.openDate, product.openDate)
        assertEquals(existingProduct.expirationDate, product.expirationDate)

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
            openDate = Date.from(
                LocalDate.now().minusDays(1).atStartOfDay(ZoneId.systemDefault()).toInstant()
            ),
            expirationDate = Date.from(
                LocalDate.now().plusDays(7).atStartOfDay(ZoneId.systemDefault()).toInstant()
            )
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
