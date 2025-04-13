package epicurius.unit.services

import epicurius.domain.exceptions.InvalidProduct
import epicurius.domain.exceptions.ProductIsAlreadyOpen
import epicurius.domain.exceptions.ProductNotFound
import epicurius.http.fridge.models.input.OpenProductInputModel
import epicurius.http.fridge.models.input.ProductInputModel
import epicurius.http.fridge.models.input.UpdateProductInputModel
import epicurius.utils.createTestUser
import kotlinx.coroutines.runBlocking
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import java.time.LocalDate
import java.time.Period
import java.time.ZoneId
import java.util.Date
import kotlin.test.assertTrue

class FridgeServiceTest : ServiceTest() {

    private var publicTestUser = createTestUser(tm)
    private var privateTestUser = createTestUser(tm, false)

    @Test
    fun `Get fridge successfully`() {
        // given a user
        val user = privateTestUser

        // when getting the user's fridge
        val fridge = getFridge(user.id)

        // then the fridge should be empty
        assertTrue(fridge.products.isEmpty())
    }

    @Test
    fun `Get product list successfully`() {
        // given a partial product name
        val partial = "app"

        // when getting the list of products
        val products = runBlocking { getProductsList(partial) }

        // then the list should contain the product
        assertTrue(products.contains("apple"))
    }

    @Test
    fun `Add product to fridge successfully`() {
        // given a user
        val user = publicTestUser

        // when adding a product to the user's fridge
        val expirationDate = Date.from(
            LocalDate.now().plusDays(7).atStartOfDay(ZoneId.systemDefault()).toInstant()
        )
        val fridge = runBlocking {
            addProduct(
                user.id,
                ProductInputModel(
                    productName = "apple",
                    quantity = 1,
                    openDate = null,
                    expirationDate = expirationDate
                )
            )
        }

        // then the fridge should contain the product
        assertTrue(
            fridge.products.any {
                it.productName == "apple" && it.quantity == 1 && it.openDate == null && it.expirationDate == expirationDate
            }
        )
    }

    @Test
    fun `Add product that already exists in fridge successfully`() {
        // given a user
        val user = publicTestUser

        // and a product in the user's fridge
        val expirationDate = Date.from(
            LocalDate.now().plusDays(7).atStartOfDay(ZoneId.systemDefault()).toInstant()
        )
        val product = ProductInputModel(
            productName = "lettuce",
            quantity = 1,
            openDate = null,
            expirationDate = expirationDate
        )
        runBlocking { addProduct(user.id, product) }

        // when adding the same product to the user's fridge
        val fridge = runBlocking { addProduct(user.id, product) }

        // then the fridge should contain the product with updated quantity
        assertTrue(
            fridge.products.any {
                it.productName == "lettuce" && it.quantity == 2 && it.openDate == null && it.expirationDate == expirationDate
            }
        )
    }

    @Test
    fun `Try to add product but product is invalid`() {
        // given a user
        val user = publicTestUser

        // when adding an invalid product to the user's fridge
        val expirationDate = Date.from(
            LocalDate.now().plusDays(7).atStartOfDay(ZoneId.systemDefault()).toInstant()
        )
        val product = ProductInputModel(
            productName = "invalid",
            quantity = 1,
            openDate = null,
            expirationDate = expirationDate
        )

        // then an InvalidProduct Exception is thrown
        assertThrows<InvalidProduct> {
            runBlocking { addProduct(user.id, product) }
        }
    }

    @Test
    fun `Try to add product to fridge but product already exists`() {
        // given a user
        val user = publicTestUser

        // and a product in the user's fridge
        val expirationDate = Date.from(
            LocalDate.now().plusDays(7).atStartOfDay(ZoneId.systemDefault()).toInstant()
        )
        val product = ProductInputModel(
            productName = "milk",
            quantity = 1,
            openDate = null,
            expirationDate = expirationDate
        )
        runBlocking { addProduct(user.id, product) }

        // when adding the same product to the user's fridge
        val fridge = runBlocking { addProduct(user.id, product) }

        // then the fridge should contain the product with updated quantity
        assertTrue(
            fridge.products.any {
                it.productName == "milk" && it.quantity == 2 && it.openDate == null && it.expirationDate == expirationDate
            }
        )
    }

    @Test
    fun `Update product in fridge successfully`() {
        // given a user
        val user = publicTestUser

        // and a product in the user's fridge
        val expirationDate = Date.from(
            LocalDate.now().plusDays(7).atStartOfDay(ZoneId.systemDefault()).toInstant()
        )
        val product = ProductInputModel(
            productName = "peach",
            quantity = 1,
            openDate = null,
            expirationDate = expirationDate
        )
        val fridge = runBlocking { addProduct(user.id, product) }

        val entryNumber = fridge.products.first {
            it.productName == "peach" && it.quantity == 1 && it.openDate == null && it.expirationDate == expirationDate
        }.entryNumber

        // when updating the product in the user's fridge
        val newExpirationDate = Date.from(
            LocalDate.now().plusDays(14).atStartOfDay(ZoneId.systemDefault()).toInstant()
        )
        val updatedProduct = UpdateProductInputModel(quantity = 4, expirationDate = newExpirationDate)
        val updatedFridge = updateProductInfo(user.id, entryNumber, updatedProduct)

        // then the fridge should contain the updated product
        assertTrue(
            updatedFridge.products.any {
                it.productName == "peach" && it.quantity == 4 && it.openDate == null && it.expirationDate == newExpirationDate
            }
        )
    }

    @Test
    fun `Try to update product but throws ProductNotFound`() {
        // given a user
        val user = publicTestUser

        // and a product in the user's fridge
        val expirationDate = Date.from(
            LocalDate.now().plusDays(7).atStartOfDay(ZoneId.systemDefault()).toInstant()
        )
        val product = ProductInputModel(
            productName = "egg",
            quantity = 1,
            openDate = null,
            expirationDate = expirationDate
        )
        runBlocking { addProduct(user.id, product) }

        // when updating a product that does not exist in the user's fridge
        val updatedProduct = UpdateProductInputModel(quantity = 1, expirationDate = null)

        // then a ProductNotFound exception is thrown
        assertThrows<ProductNotFound> {
            updateProductInfo(user.id, 0, updatedProduct)
        }
    }

    @Test
    fun `Try to update product expiration date but throws ProductIsAlreadyOpen`() {
        // given a user
        val user = publicTestUser

        // and a product in the user's fridge
        val openDate = Date.from(LocalDate.now().atStartOfDay(ZoneId.systemDefault()).toInstant())
        val expirationDate = Date.from(
            LocalDate.now().plusDays(7).atStartOfDay(ZoneId.systemDefault()).toInstant()
        )
        val product = ProductInputModel(
            productName = "carrot",
            quantity = 1,
            openDate = openDate,
            expirationDate = expirationDate
        )
        runBlocking { addProduct(user.id, product) }

        val entryNumber = getFridge(user.id).products.first {
            it.productName == "carrot" && it.quantity == 1 && it.openDate == openDate && it.expirationDate == expirationDate
        }.entryNumber

        // when updating the product with an expiration date
        val newExpirationDate = Date.from(
            LocalDate.now().plusDays(8).atStartOfDay(ZoneId.systemDefault()).toInstant()
        )
        val updatedProduct = UpdateProductInputModel(quantity = 2, expirationDate = newExpirationDate)

        // then a ProductIsAlreadyOpen exception is thrown
        assertThrows<ProductIsAlreadyOpen> {
            updateProductInfo(user.id, entryNumber, updatedProduct)
        }
    }

    @Test
    fun `Open product successfully`() {
        // given a user
        val user = publicTestUser

        // and a product in the user's fridge
        val expirationDate = Date.from(
            LocalDate.now().plusDays(7).atStartOfDay(ZoneId.systemDefault()).toInstant()
        )
        val product = ProductInputModel(
            productName = "orange",
            quantity = 1,
            openDate = null,
            expirationDate = expirationDate
        )
        val fridge = runBlocking { addProduct(user.id, product) }

        val entryNumber = fridge.products.first {
            it.productName == "orange" && it.quantity == 1 && it.openDate == null && it.expirationDate == expirationDate
        }.entryNumber

        // when opening the product in the user's fridge
        val openDate = Date.from(LocalDate.now().atStartOfDay(ZoneId.systemDefault()).toInstant())
        val duration = Period.ofDays(7)
        val openedFridge = openProduct(user.id, entryNumber, OpenProductInputModel(openDate, duration))

        // then the fridge should contain the opened product
        val expectedExpirationDate = fridgeDomain.calculateExpirationDate(openDate, duration)
        assertTrue(
            openedFridge.products.any {
                it.productName == "orange" && it.quantity == 1 && it.openDate == openDate && it.expirationDate == expectedExpirationDate
            }
        )
    }

    @Test
    fun `Open product that already exists in the fridge with the same expiration date`() {
        // given a user
        val user = publicTestUser

        // and a product in the user's fridge
        val expirationDate = Date.from(
            LocalDate.now().plusDays(7).atStartOfDay(ZoneId.systemDefault()).toInstant()
        )
        val product = ProductInputModel(
            productName = "yogurt",
            quantity = 3,
            openDate = null,
            expirationDate = expirationDate
        )
        val fridge = runBlocking { addProduct(user.id, product) }

        val entryNumber = fridge.products.first {
            it.productName == "yogurt" && it.quantity == 3 && it.openDate == null && it.expirationDate == expirationDate
        }.entryNumber

        // when opening the product in the user's fridge
        val openDate = Date.from(LocalDate.now().atStartOfDay(ZoneId.systemDefault()).toInstant())
        val duration = Period.ofDays(7)
        openProduct(user.id, entryNumber, OpenProductInputModel(openDate, duration))
        val openedFridge = openProduct(user.id, entryNumber, OpenProductInputModel(openDate, duration))

        // then the fridge should contain the opened product
        val expectedExpirationDate = fridgeDomain.calculateExpirationDate(openDate, duration)
        assertTrue(
            openedFridge.products.any {
                it.productName == "yogurt" && it.quantity == 2 && it.openDate == openDate && it.expirationDate == expectedExpirationDate
            }
        )
        assertTrue(
            openedFridge.products.any {
                it.productName == "yogurt" && it.quantity == 1 && it.openDate == null && it.expirationDate == expirationDate
            }
        )
    }

    @Test
    fun `Try to open product but throws ProductNotFound`() {
        // given a user
        val user = publicTestUser

        // and a product in the user's fridge
        val expirationDate = Date.from(
            LocalDate.now().plusDays(7).atStartOfDay(ZoneId.systemDefault()).toInstant()
        )
        val product = ProductInputModel(
            productName = "banana",
            quantity = 1,
            openDate = null,
            expirationDate = expirationDate
        )
        runBlocking { addProduct(user.id, product) }

        // when opening a product that does not exist in the user's fridge
        val openDate = Date.from(LocalDate.now().atStartOfDay(ZoneId.systemDefault()).toInstant())
        val duration = Period.ofDays(7)

        // then a ProductNotFound exception is thrown
        assertThrows<ProductNotFound> {
            openProduct(user.id, 0, OpenProductInputModel(openDate, duration))
        }
    }

    @Test
    fun `Try to open product but throws ProductIsAlreadyOpen`() {
        // given a user
        val user = publicTestUser

        // and a product in the user's fridge
        val openDate = Date.from(LocalDate.now().atStartOfDay(ZoneId.systemDefault()).toInstant())
        val expirationDate = Date.from(
            LocalDate.now().plusDays(7).atStartOfDay(ZoneId.systemDefault()).toInstant()
        )
        val product = ProductInputModel(
            productName = "cheese",
            quantity = 1,
            openDate = openDate,
            expirationDate = expirationDate
        )
        runBlocking { addProduct(user.id, product) }

        val entryNumber = getFridge(user.id).products.first {
            it.productName == "cheese" && it.quantity == 1 && it.openDate == openDate && it.expirationDate == expirationDate
        }.entryNumber

        // when opening the product that is already open
        val newOpenDate = Date.from(LocalDate.now().atStartOfDay(ZoneId.systemDefault()).toInstant())
        val duration = Period.ofDays(7)

        // then a ProductIsAlreadyOpen exception is thrown
        assertThrows<ProductIsAlreadyOpen> {
            openProduct(user.id, entryNumber, OpenProductInputModel(newOpenDate, duration))
        }
    }

    @Test
    fun `Remove product from fridge successfully`() {
        // given a user
        val user = privateTestUser

        // and a product in the user's fridge
        val expirationDate = Date.from(
            LocalDate.now().plusDays(7).atStartOfDay(ZoneId.systemDefault()).toInstant()
        )
        val product = ProductInputModel(
            productName = "tomato",
            quantity = 1,
            openDate = null,
            expirationDate = expirationDate
        )
        val fridge = runBlocking { addProduct(user.id, product) }

        val entryNumber = fridge.products.first {
            it.productName == "tomato" && it.quantity == 1 && it.openDate == null && it.expirationDate == expirationDate
        }.entryNumber

        // when removing the product from the user's fridge
        val updatedFridge = runBlocking { removeProduct(user.id, entryNumber) }

        // then the fridge should be empty
        assertTrue(updatedFridge.products.isEmpty())
    }

    @Test
    fun `Try to remove product but throws ProductNotFound`() {
        // given a user
        val user = publicTestUser

        // and a product in the user's fridge
        val expirationDate = Date.from(
            LocalDate.now().plusDays(7).atStartOfDay(ZoneId.systemDefault()).toInstant()
        )
        val product = ProductInputModel(
            productName = "potato",
            quantity = 1,
            openDate = null,
            expirationDate = expirationDate
        )
        runBlocking { addProduct(user.id, product) }

        // when removing a product that does not exist in the user's fridge
        // then a ProductNotFound exception is thrown
        assertThrows<ProductNotFound> { removeProduct(user.id, 0) }
    }
}
