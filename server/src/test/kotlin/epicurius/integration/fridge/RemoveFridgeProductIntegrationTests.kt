package epicurius.integration.fridge

import epicurius.domain.exceptions.ProductNotFound
import epicurius.http.utils.Problem
import epicurius.http.utils.Uris.Fridge.PRODUCT
import epicurius.integration.utils.delete
import epicurius.integration.utils.getBody
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertNotNull
import org.junit.jupiter.api.Assertions.assertTrue
import org.springframework.http.HttpStatus
import java.time.LocalDate
import kotlin.test.Test

class RemoveFridgeProductIntegrationTests : FridgeIntegrationTest() {

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
            api(PRODUCT.take(16) + 999999),
            HttpStatus.NOT_FOUND,
            token
        )
        assertNotNull(error)

        // then the request should fail with code 404
        val errorBody = getBody(error)
        assertEquals(ProductNotFound(999999).message, errorBody.detail)
    }
}
