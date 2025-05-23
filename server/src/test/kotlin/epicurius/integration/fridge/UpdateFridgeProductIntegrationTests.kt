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
        val newFridgeBody = getBody(
            addProducts(testUser.token, "milk", 1, null, expirationDate)
        )

        // and updating the product
        val newExpirationDate = LocalDate.now().plusDays(14)
        val updatedFridgeBody = getBody(
            updateFridgeProduct(
                token = testUser.token,
                entryNumber = newFridgeBody.fridge.products.first().entryNumber,
                quantity = 2,
                expirationDate = newExpirationDate
            )
        )

        // then the fridge should contain the updated product
        assertNotNull(updatedFridgeBody)
        assertTrue(updatedFridgeBody.fridge.products.isNotEmpty())
        assertTrue(updatedFridgeBody.fridge.products.first().name == "milk")
        assertTrue(updatedFridgeBody.fridge.products.first().quantity == 2)
        assertTrue(updatedFridgeBody.fridge.products.first().openDate == null)
    }

    @Test
    fun `Should fail with code 404 when updating a product with invalid entry number`() {
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

        // then the fridge cannot be updated and fails with code 400
        val errorBody = getBody(error)
        assertEquals(ProductNotFound(999999).message, errorBody.detail)
    }

    @Test
    fun `Should fail with code 400 when updating a product expiration date but it is already open`() {
        // given a user (testUser)

        // when adding a product
        val openDate = LocalDate.now()
        val expirationDate = LocalDate.now().plusDays(7)
        val newFridgeBody = getBody(
            addProducts(testUser.token, "cream", 1, openDate, expirationDate)
        )

        // and trying to update the product
        val newExpirationDate = LocalDate.now().plusDays(14)
        val error = patch<Problem>(
            client,
            api(PRODUCT.take(16) + newFridgeBody.fridge.products.first().entryNumber),
            body = mapOf("quantity" to 2, "expirationDate" to newExpirationDate),
            responseStatus = HttpStatus.CONFLICT,
            token = testUser.token
        )
        assertNotNull(error)

        // then the fridge cannot be updated and fails with code 400
        val errorBody = getBody(error)
        assertEquals(ProductIsAlreadyOpen().message, errorBody.detail)
    }
}
