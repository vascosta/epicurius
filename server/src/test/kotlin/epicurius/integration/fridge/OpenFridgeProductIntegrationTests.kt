package epicurius.integration.fridge

import epicurius.domain.exceptions.ProductIsAlreadyOpen
import epicurius.domain.exceptions.ProductNotFound
import epicurius.http.utils.Problem
import epicurius.http.utils.Uris.Fridge.PRODUCT
import epicurius.integration.utils.getBody
import epicurius.integration.utils.patch
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertNotNull
import org.junit.jupiter.api.Assertions.assertTrue
import org.springframework.http.HttpStatus
import java.time.LocalDate
import java.time.Period
import kotlin.test.Test

class OpenFridgeProductIntegrationTests : FridgeIntegrationTest() {

    @Test
    fun `Open fridge product successfully with code 200`() {
        // given a user (testUser)

        // when adding a product
        val expirationDate = LocalDate.now().plusDays(7)
        val newFridgeBody = getBody(addProducts(testUser.token, "peach", 1, null, expirationDate))

        // and opening the product
        val openDate = LocalDate.now()
        val duration = Period.ofDays(3)
        val openProductBody = getBody(
            openFridgeProduct(testUser.token, newFridgeBody.products.first().entryNumber, openDate, duration)
        )

        // then the fridge should contain the updated product
        assertNotNull(openProductBody)
        assertTrue(openProductBody.products.isNotEmpty())
        assertTrue(openProductBody.products.first().name == "peach")
        assertTrue(openProductBody.products.first().quantity == 1)
    }

    @Test
    fun `Open product that already exists in the fridge with the same expiration date successfully with code 200`() {
        // given a user (testUser)

        // when adding a product
        val expirationDate = LocalDate.now().plusDays(7)
        val newFridgeBody = getBody(addProducts(testUser.token, "orange", 2, null, expirationDate))

        // and opening the product
        val openDate = LocalDate.now()
        val duration = Period.ofDays(3)
        openFridgeProduct(testUser.token, newFridgeBody.products.first().entryNumber, openDate, duration)
        val openProductBody = getBody(
            openFridgeProduct(testUser.token, newFridgeBody.products.first().entryNumber, openDate, duration)
        )

        // then the fridge should contain the updated product
        assertNotNull(openProductBody)
        assertTrue(openProductBody.products.isNotEmpty())
        assertTrue(openProductBody.products.first().name == "orange")
        assertTrue(openProductBody.products.first().quantity == 2)
        assertNotNull(openProductBody.products.first().openDate)
    }

    @Test
    fun `Try to open product but entry number is invalid, fails with 404`() {
        // given a user (testUser)

        // when opening a product with an invalid entry number
        val openDate = LocalDate.now()
        val duration = Period.ofDays(3)
        val error = patch<Problem>(
            client,
            api(PRODUCT.take(16) + 999999),
            body = mapOf("openDate" to openDate, "duration" to duration),
            responseStatus = HttpStatus.NOT_FOUND,
            token = testUser.token
        )
        assertNotNull(error)

        // then the request should fail with code 404
        val errorBody = getBody(error)
        assertEquals(ProductNotFound(999999).message, errorBody.detail)
    }

    @Test
    fun `Try to open product but product is already open, fails with 400`() {
        // given a user (testUser)

        // when adding a product
        val openDate = LocalDate.now()
        val expirationDate = LocalDate.now().plusDays(7)
        val newFridgeBody = getBody(addProducts(testUser.token, "tomato", 1, openDate, expirationDate))

        // and trying to open the product
        val duration = Period.ofDays(3)
        val error = patch<Problem>(
            client,
            api(PRODUCT.take(16) + newFridgeBody.products.first().entryNumber),
            body = mapOf("openDate" to openDate, "duration" to duration),
            responseStatus = HttpStatus.CONFLICT,
            token = testUser.token
        )
        assertNotNull(error)

        // then the request should fail with code 400
        val errorBody = getBody(error)
        assertEquals(ProductIsAlreadyOpen().message, errorBody.detail)
    }
}
