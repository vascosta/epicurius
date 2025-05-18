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
import kotlin.test.Test

class UpdateFridgeProductIntegrationTests : FridgeIntegrationTest() {

    @Test
    fun `Update product successfully with code 200`() {
        // given a user (testUser)

        // when adding a product
        val expirationDate = LocalDate.now().plusDays(7)
        val newFridgeBody = getBody(addProducts(testUser.token, "milk", 1, null, expirationDate))

        // and updating the product
        val newExpirationDate = LocalDate.now().plusDays(14)
        val updatedFridgeBody = getBody(
            updateFridgeProduct(testUser.token, newFridgeBody.products.first().entryNumber, 2, newExpirationDate)
        )

        // then the fridge should contain the updated product
        assertNotNull(updatedFridgeBody)
        assertTrue(updatedFridgeBody.products.isNotEmpty())
        assertTrue(updatedFridgeBody.products.first().name == "milk")
        assertTrue(updatedFridgeBody.products.first().quantity == 2)
        assertTrue(updatedFridgeBody.products.first().openDate == null)
    }

    @Test
    fun `Try to update product with invalid entry number and fails with code 404`() {
        // given a user (testUser)

        // when updating a product with an invalid entry number
        val newExpirationDate = LocalDate.now().plusDays(14)
        val error = patch<Problem>(
            client,
            api(PRODUCT.take(16) + 999999),
            body = mapOf("quantity" to 2, "expirationDate" to newExpirationDate),
            responseStatus = HttpStatus.NOT_FOUND,
            token = testUser.token
        )
        assertNotNull(error)

        // then the request should fail with code 400
        val errorBody = getBody(error)
        assertEquals(ProductNotFound(999999).message, errorBody.detail)
    }

    @Test
    fun `Try to update a product expiration date but it is already open, fails with 400`() {
        // given a user (testUser)

        // when adding a product
        val openDate = LocalDate.now()
        val expirationDate = LocalDate.now().plusDays(7)
        val newFridgeBody = getBody(addProducts(testUser.token, "cream", 1, openDate, expirationDate))

        // and trying to update the product
        val newExpirationDate = LocalDate.now().plusDays(14)
        val error = patch<Problem>(
            client,
            api(PRODUCT.take(16) + newFridgeBody.products.first().entryNumber),
            body = mapOf("quantity" to 2, "expirationDate" to newExpirationDate),
            responseStatus = HttpStatus.CONFLICT,
            token = testUser.token
        )
        assertNotNull(error)

        // then the request should fail with code 400
        val errorBody = getBody(error)
        assertEquals(ProductIsAlreadyOpen().message, errorBody.detail)
    }
}
