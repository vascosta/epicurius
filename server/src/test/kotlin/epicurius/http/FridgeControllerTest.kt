package epicurius.http

import epicurius.domain.exceptions.InvalidProduct
import epicurius.domain.exceptions.ProductIsAlreadyOpen
import epicurius.domain.exceptions.ProductNotFound
import epicurius.http.utils.Problem
import epicurius.http.utils.Uris
import epicurius.http.utils.delete
import epicurius.http.utils.getBody
import epicurius.http.utils.patch
import epicurius.http.utils.post
import epicurius.utils.generateEmail
import epicurius.utils.generateRandomUsername
import epicurius.utils.generateSecurePassword
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.springframework.http.HttpStatus
import java.time.LocalDate
import java.time.Period
import java.time.ZoneId
import java.util.Date
import kotlin.test.assertEquals
import kotlin.test.assertNotNull

class FridgeControllerTest : HttpTest() {

    lateinit var user: String

    @BeforeEach
    fun setup() {
        val username = generateRandomUsername()
        val email = generateEmail(username)
        val country = "PT"
        val password = generateSecurePassword()
        user = signUp(username, email, country, password)
    }

    @Test
    fun `Get fridge successfully with code 200`() {
        // given a user token
        val token = user

        // when getting the user's fridge
        val fridge = getFridge(token)

        // then the fridge should be empty
        assertNotNull(fridge)
        assertTrue(fridge.products.isEmpty())
    }

    @Test
    fun `Get products list successfully with code 200`() {
        // given a user token
        val token = user

        // when getting the products list
        val productsList = getProductsList(token, "app")

        // then the products list should not be empty
        assertNotNull(productsList)
        assertTrue(productsList.isNotEmpty())
    }

    @Test
    fun `Add products successfully with code 200`() {
        // given a user token
        val token = user

        // when adding a product
        val expirationDate = Date.from(
            LocalDate.now().plusDays(7).atStartOfDay(ZoneId.systemDefault()).toInstant()
        )
        val newFridge = getBody(addProducts(token, "apple", 1, null, expirationDate))

        // then the fridge should contain the product
        assertNotNull(newFridge)
        assertTrue(newFridge.products.isNotEmpty())
        assertTrue(newFridge.products.first().productName == "apple")
        assertTrue(newFridge.products.first().quantity == 1)
        assertTrue(newFridge.products.first().openDate == null)
    }

    @Test
    fun `Add product that already exists successfully with code 200`() {
        // given a user token
        val token = user

        // when adding a product
        val expirationDate = Date.from(
            LocalDate.now().plusDays(7).atStartOfDay(ZoneId.systemDefault()).toInstant()
        )
        getBody(addProducts(token, "peach", 1, null, expirationDate))

        // and adding the same product again
        val newFridge = getBody(addProducts(token, "peach", 1, null, expirationDate))

        // then the fridge should contain the product with the updated quantity
        assertNotNull(newFridge)
        assertTrue(newFridge.products.isNotEmpty())
        assertTrue(newFridge.products.first().productName == "peach")
        assertTrue(newFridge.products.first().quantity == 2)
        assertTrue(newFridge.products.first().openDate == null)
    }

    @Test
    fun `Try to add product with invalid name and fails with code 400`() {
        // given a user
        val token = user

        // when trying to add a product with an invalid name
        val expirationDate = Date.from(
            LocalDate.now().plusDays(7).atStartOfDay(ZoneId.systemDefault()).toInstant()
        )
        val error = post<Problem>(
            client,
            api(Uris.Fridge.FRIDGE),
            mapOf("productName" to "invalid", "quantity" to 1, "expirationDate" to expirationDate),
            HttpStatus.BAD_REQUEST,
            token
        )

        // then the request should fail with code 400
        assertNotNull(error)
        val errorBody = getBody(error)
        assertEquals(InvalidProduct().message, errorBody.detail)
    }

    @Test
    fun `Update product successfully with code 200`() {
        // given a user token
        val token = user

        // when adding a product
        val expirationDate = Date.from(
            LocalDate.now().plusDays(7).atStartOfDay(ZoneId.systemDefault()).toInstant()
        )
        val newFridge = getBody(addProducts(token, "milk", 1, null, expirationDate))

        // and updating the product
        val newExpirationDate = Date.from(
            LocalDate.now().plusDays(14).atStartOfDay(ZoneId.systemDefault()).toInstant()
        )
        val updatedFridge = getBody(
            updateFridgeProduct(token, newFridge.products.first().entryNumber, 2, newExpirationDate)
        )

        // then the fridge should contain the updated product
        assertNotNull(updatedFridge)
        assertTrue(updatedFridge.products.isNotEmpty())
        assertTrue(updatedFridge.products.first().productName == "milk")
        assertTrue(updatedFridge.products.first().quantity == 2)
        assertTrue(updatedFridge.products.first().openDate == null)
    }

    @Test
    fun `Try to update product with invalid entry number and fails with code 404`() {
        // given a user token
        val token = user

        // when updating a product with an invalid entry number
        val newExpirationDate = Date.from(
            LocalDate.now().plusDays(14).atStartOfDay(ZoneId.systemDefault()).toInstant()
        )
        val error = patch<Problem>(
            client,
            api(Uris.Fridge.PRODUCT.take(16) + 999999),
            mapOf("quantity" to 2, "expirationDate" to newExpirationDate),
            HttpStatus.NOT_FOUND,
            token
        )

        // then the request should fail with code 400
        assertNotNull(error)
        val errorBody = getBody(error)
        assertEquals(ProductNotFound(999999).message, errorBody.detail)
    }

    @Test
    fun `Try to update a product expiration date but it is already open, fails with 400`() {
        // given a user token
        val token = user

        // when adding a product
        val openDate = Date.from(
            LocalDate.now().atStartOfDay(ZoneId.systemDefault()).toInstant()
        )
        val expirationDate = Date.from(
            LocalDate.now().plusDays(7).atStartOfDay(ZoneId.systemDefault()).toInstant()
        )
        val newFridge = getBody(addProducts(token, "cream", 1, openDate, expirationDate))

        // and trying to update the product
        val newExpirationDate = Date.from(
            LocalDate.now().plusDays(14).atStartOfDay(ZoneId.systemDefault()).toInstant()
        )
        val error = patch<Problem>(
            client,
            api(Uris.Fridge.PRODUCT.take(16) + newFridge.products.first().entryNumber),
            mapOf("quantity" to 2, "expirationDate" to newExpirationDate),
            HttpStatus.BAD_REQUEST,
            token
        )

        // then the request should fail with code 400
        assertNotNull(error)
        val errorBody = getBody(error)
        assertEquals(ProductIsAlreadyOpen().message, errorBody.detail)
    }

    @Test
    fun `Open fridge product successfully with code 200`() {
        // given a user token
        val token = user

        // when adding a product
        val expirationDate = Date.from(
            LocalDate.now().plusDays(7).atStartOfDay(ZoneId.systemDefault()).toInstant()
        )
        val newFridge = getBody(addProducts(token, "peach", 1, null, expirationDate))

        // and opening the product
        val openDate = Date.from(
            LocalDate.now().atStartOfDay(ZoneId.systemDefault()).toInstant()
        )
        val duration = Period.ofDays(3)
        val openProduct = getBody(
            openFridgeProduct(token, newFridge.products.first().entryNumber, openDate, duration)
        )

        // then the fridge should contain the updated product
        assertNotNull(openProduct)
        assertTrue(openProduct.products.isNotEmpty())
        assertTrue(openProduct.products.first().productName == "peach")
        assertTrue(openProduct.products.first().quantity == 1)
    }

    @Test
    fun `Try to open product but entry number is invalid, fails with 404`() {
        // given a user token
        val token = user

        // when opening a product with an invalid entry number
        val openDate = Date.from(
            LocalDate.now().atStartOfDay(ZoneId.systemDefault()).toInstant()
        )
        val duration = Period.ofDays(3)
        val error = patch<Problem>(
            client,
            api(Uris.Fridge.OPEN_PRODUCT.take(13) + 999999),
            mapOf("openDate" to openDate, "duration" to duration),
            HttpStatus.NOT_FOUND,
            token
        )

        // then the request should fail with code 404
        assertNotNull(error)
        val errorBody = getBody(error)
        assertEquals(ProductNotFound(999999).message, errorBody.detail)
    }

    @Test
    fun `Try to open product but product is already open, fails with 400`() {
        // given a user token
        val token = user

        // when adding a product
        val openDate = Date.from(
            LocalDate.now().atStartOfDay(ZoneId.systemDefault()).toInstant()
        )
        val expirationDate = Date.from(
            LocalDate.now().plusDays(7).atStartOfDay(ZoneId.systemDefault()).toInstant()
        )
        val newFridge = getBody(addProducts(token, "tomato", 1, openDate, expirationDate))

        // and trying to open the product
        val duration = Period.ofDays(3)
        val error = patch<Problem>(
            client,
            api(Uris.Fridge.OPEN_PRODUCT.take(13) + newFridge.products.first().entryNumber),
            mapOf("openDate" to openDate, "duration" to duration),
            HttpStatus.BAD_REQUEST,
            token
        )

        // then the request should fail with code 400
        assertNotNull(error)
        val errorBody = getBody(error)
        assertEquals(ProductIsAlreadyOpen().message, errorBody.detail)
    }

    @Test
    fun `Remove product successfully with code 200`() {
        // given a user token
        val token = user

        // when adding a product
        val expirationDate = Date.from(
            LocalDate.now().plusDays(7).atStartOfDay(ZoneId.systemDefault()).toInstant()
        )
        val newFridge = getBody(addProducts(token, "banana", 1, null, expirationDate))

        // and removing the product
        val removedFridge = getBody(removeProduct(token, newFridge.products.first().entryNumber))

        // then the fridge should be empty
        assertNotNull(removedFridge)
        assertTrue(removedFridge.products.isEmpty())
    }

    @Test
    fun `Try to remove product with invalid entry number and fails with code 404`() {
        // given a user token
        val token = user

        // when removing a product with an invalid entry number
        val error = delete<Problem>(
            client,
            api(Uris.Fridge.PRODUCT.take(16) + 999999),
            HttpStatus.NOT_FOUND,
            token
        )

        // then the request should fail with code 404
        assertNotNull(error)
        val errorBody = getBody(error)
        assertEquals(ProductNotFound(999999).message, errorBody.detail)
    }
}
