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
import kotlin.test.assertEquals
import kotlin.test.assertNotNull

class FridgeControllerTest : HttpTest() {

    lateinit var testUserToken: String

    @BeforeEach
    fun setup() {
        val username = generateRandomUsername()
        testUserToken = signUp(username, generateEmail(username), "PT", generateSecurePassword())
    }

    @Test
    fun `Get fridge successfully with code 200`() {
        // given a user token
        val token = testUserToken

        // when getting the user's fridge
        val fridgeBody = getFridge(token)

        // then the fridge should be empty
        assertNotNull(fridgeBody)
        assertTrue(fridgeBody.products.isEmpty())
    }

    @Test
    fun `Get products list successfully with code 200`() {
        // given a user token
        val token = testUserToken

        // when getting the products list
        val productsListBody = getProductsList(token, "app")

        // then the products list should not be empty
        assertNotNull(productsListBody)
        assertTrue(productsListBody.isNotEmpty())
    }

    @Test
    fun `Add products successfully with code 200`() {
        // given a user token
        val token = testUserToken

        // when adding a product
        val expirationDate = LocalDate.now().plusDays(7)
        val newFridgeBody = getBody(addProducts(token, "apple", 1, null, expirationDate))

        // then the fridge should contain the product
        assertNotNull(newFridgeBody)
        assertTrue(newFridgeBody.products.isNotEmpty())
        assertTrue(newFridgeBody.products.first().productName == "apple")
        assertTrue(newFridgeBody.products.first().quantity == 1)
        assertTrue(newFridgeBody.products.first().openDate == null)
    }

    @Test
    fun `Add product that already exists successfully with code 200`() {
        // given a user token
        val token = testUserToken

        // when adding a product
        val expirationDate = LocalDate.now().plusDays(7)
        getBody(addProducts(token, "peach", 1, null, expirationDate))

        // and adding the same product again
        val newFridgeBody = getBody(addProducts(token, "peach", 1, null, expirationDate))

        // then the fridge should contain the product with the updated quantity
        assertNotNull(newFridgeBody)
        assertTrue(newFridgeBody.products.isNotEmpty())
        assertTrue(newFridgeBody.products.first().productName == "peach")
        assertTrue(newFridgeBody.products.first().quantity == 2)
        assertTrue(newFridgeBody.products.first().openDate == null)
    }

    @Test
    fun `Try to add product with invalid name and fails with code 400`() {
        // given a user
        val token = testUserToken

        // when trying to add a product with an invalid name
        val expirationDate = LocalDate.now().plusDays(7)
        val error = post<Problem>(
            client,
            api(Uris.Fridge.FRIDGE),
            mapOf("productName" to "invalid", "quantity" to 1, "expirationDate" to expirationDate),
            HttpStatus.BAD_REQUEST,
            token
        )
        assertNotNull(error)

        // then the request should fail with code 400
        val errorBody = getBody(error)
        assertEquals(InvalidProduct().message, errorBody.detail)
    }

    @Test
    fun `Update product successfully with code 200`() {
        // given a user token
        val token = testUserToken

        // when adding a product
        val expirationDate = LocalDate.now().plusDays(7)
        val newFridgeBody = getBody(addProducts(token, "milk", 1, null, expirationDate))

        // and updating the product
        val newExpirationDate = LocalDate.now().plusDays(14)
        val updatedFridgeBody = getBody(
            updateFridgeProduct(token, newFridgeBody.products.first().entryNumber, 2, newExpirationDate)
        )

        // then the fridge should contain the updated product
        assertNotNull(updatedFridgeBody)
        assertTrue(updatedFridgeBody.products.isNotEmpty())
        assertTrue(updatedFridgeBody.products.first().productName == "milk")
        assertTrue(updatedFridgeBody.products.first().quantity == 2)
        assertTrue(updatedFridgeBody.products.first().openDate == null)
    }

    @Test
    fun `Try to update product with invalid entry number and fails with code 404`() {
        // given a user token
        val token = testUserToken

        // when updating a product with an invalid entry number
        val newExpirationDate = LocalDate.now().plusDays(14)
        val error = patch<Problem>(
            client,
            api(Uris.Fridge.PRODUCT.take(16) + 999999),
            body = mapOf("quantity" to 2, "expirationDate" to newExpirationDate),
            responseStatus = HttpStatus.NOT_FOUND,
            token = token
        )
        assertNotNull(error)

        // then the request should fail with code 400
        val errorBody = getBody(error)
        assertEquals(ProductNotFound(999999).message, errorBody.detail)
    }

    @Test
    fun `Try to update a product expiration date but it is already open, fails with 400`() {
        // given a user token
        val token = testUserToken

        // when adding a product
        val openDate = LocalDate.now()
        val expirationDate = LocalDate.now().plusDays(7)
        val newFridgeBody = getBody(addProducts(token, "cream", 1, openDate, expirationDate))

        // and trying to update the product
        val newExpirationDate = LocalDate.now().plusDays(14)
        val error = patch<Problem>(
            client,
            api(Uris.Fridge.PRODUCT.take(16) + newFridgeBody.products.first().entryNumber),
            body = mapOf("quantity" to 2, "expirationDate" to newExpirationDate),
            responseStatus = HttpStatus.BAD_REQUEST,
            token = token
        )
        assertNotNull(error)

        // then the request should fail with code 400
        val errorBody = getBody(error)
        assertEquals(ProductIsAlreadyOpen().message, errorBody.detail)
    }

    @Test
    fun `Open fridge product successfully with code 200`() {
        // given a user token
        val token = testUserToken

        // when adding a product
        val expirationDate = LocalDate.now().plusDays(7)
        val newFridgeBody = getBody(addProducts(token, "peach", 1, null, expirationDate))

        // and opening the product
        val openDate = LocalDate.now()
        val duration = Period.ofDays(3)
        val openProductBody = getBody(
            openFridgeProduct(token, newFridgeBody.products.first().entryNumber, openDate, duration)
        )

        // then the fridge should contain the updated product
        assertNotNull(openProductBody)
        assertTrue(openProductBody.products.isNotEmpty())
        assertTrue(openProductBody.products.first().productName == "peach")
        assertTrue(openProductBody.products.first().quantity == 1)
    }

    @Test
    fun `Open that already exists in the fridge with the same expiration date successfully with code 200`() {
        // given a user token
        val token = testUserToken

        // when adding a product
        val expirationDate = LocalDate.now().plusDays(7)
        val newFridgeBody = getBody(addProducts(token, "orange", 2, null, expirationDate))

        // and opening the product
        val openDate = LocalDate.now()
        val duration = Period.ofDays(3)
        openFridgeProduct(token, newFridgeBody.products.first().entryNumber, openDate, duration)
        val openProductBody = getBody(
            openFridgeProduct(token, newFridgeBody.products.first().entryNumber, openDate, duration)
        )

        // then the fridge should contain the updated product
        assertNotNull(openProductBody)
        assertTrue(openProductBody.products.isNotEmpty())
        assertTrue(openProductBody.products.first().productName == "orange")
        assertTrue(openProductBody.products.first().quantity == 2)
        assertNotNull(openProductBody.products.first().openDate)
    }

    @Test
    fun `Try to open product but entry number is invalid, fails with 404`() {
        // given a user token
        val token = testUserToken

        // when opening a product with an invalid entry number
        val openDate = LocalDate.now()
        val duration = Period.ofDays(3)
        val error = patch<Problem>(
            client,
            api(Uris.Fridge.OPEN_PRODUCT.take(13) + 999999),
            body = mapOf("openDate" to openDate, "duration" to duration),
            responseStatus = HttpStatus.NOT_FOUND,
            token = token
        )
        assertNotNull(error)

        // then the request should fail with code 404
        val errorBody = getBody(error)
        assertEquals(ProductNotFound(999999).message, errorBody.detail)
    }

    @Test
    fun `Try to open product but product is already open, fails with 400`() {
        // given a user token
        val token = testUserToken

        // when adding a product
        val openDate = LocalDate.now()
        val expirationDate = LocalDate.now().plusDays(7)
        val newFridgeBody = getBody(addProducts(token, "tomato", 1, openDate, expirationDate))

        // and trying to open the product
        val duration = Period.ofDays(3)
        val error = patch<Problem>(
            client,
            api(Uris.Fridge.OPEN_PRODUCT.take(13) + newFridgeBody.products.first().entryNumber),
            body = mapOf("openDate" to openDate, "duration" to duration),
            responseStatus = HttpStatus.BAD_REQUEST,
            token = token
        )
        assertNotNull(error)

        // then the request should fail with code 400
        val errorBody = getBody(error)
        assertEquals(ProductIsAlreadyOpen().message, errorBody.detail)
    }

    @Test
    fun `Remove product successfully with code 200`() {
        // given a user token
        val token = testUserToken

        // when adding a product
        val expirationDate = LocalDate.now().plusDays(7)
        val newFridgeBody = getBody(addProducts(token, "banana", 1, null, expirationDate))

        // and removing the product
        val removedFridgeBody = getBody(removeProduct(token, newFridgeBody.products.first().entryNumber))

        // then the fridge should be empty
        assertNotNull(removedFridgeBody)
        assertTrue(removedFridgeBody.products.isEmpty())
    }

    @Test
    fun `Try to remove product with invalid entry number and fails with code 404`() {
        // given a user token
        val token = testUserToken

        // when removing a product with an invalid entry number
        val error = delete<Problem>(
            client,
            api(Uris.Fridge.PRODUCT.take(16) + 999999),
            HttpStatus.NOT_FOUND,
            token
        )
        assertNotNull(error)

        // then the request should fail with code 404
        val errorBody = getBody(error)
        assertEquals(ProductNotFound(999999).message, errorBody.detail)
    }
}
