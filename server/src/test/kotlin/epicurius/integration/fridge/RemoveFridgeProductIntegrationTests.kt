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
        // given a user (testUser)

        // when adding a product
        val expirationDate = LocalDate.now().plusDays(7)
        val newFridgeBody = getBody(addProducts(testUser.token, "banana", 1, null, expirationDate))

        // and removing the product
        val removedFridgeBody = getBody(
            removeProduct(testUser.token, newFridgeBody.fridge.products.first().entryNumber)
        )

        // then the fridge should be empty
        assertNotNull(removedFridgeBody)
        assertTrue(removedFridgeBody.fridge.products.isEmpty())
    }

    @Test
    fun `Should fail with code 404 when removing product with invalid entry number`() {
        // given a user (testUser)

        // when removing a product with an invalid entry number
        val error = delete<Problem>(
            client,
            api(PRODUCT.take(16) + 999999),
            HttpStatus.NOT_FOUND,
            testUser.token
        )
        assertNotNull(error)

        // then the product cannot be removed and fails with code 404
        val errorBody = getBody(error)
        assertEquals(ProductNotFound(999999).message, errorBody.detail)
    }
}
