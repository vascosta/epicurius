package epicurius.integration.fridge

import epicurius.domain.exceptions.InvalidProduct
import epicurius.http.utils.Problem
import epicurius.http.utils.Uris.Fridge.FRIDGE
import epicurius.integration.utils.getBody
import epicurius.integration.utils.post
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertNotNull
import org.junit.jupiter.api.Assertions.assertTrue
import org.springframework.http.HttpStatus
import java.time.LocalDate
import kotlin.test.Test

class AddProductToFridgeIntegrationTests : FridgeIntegrationTest() {

    @Test
    fun `Add products successfully with code 200`() {
        // given a user (testUser)

        // when adding a product
        val expirationDate = LocalDate.now().plusDays(7)
        val newFridgeBody = getBody(
            addProducts(testUser.token, "apple", 1, null, expirationDate)
        )

        // then the fridge should contain the product
        assertNotNull(newFridgeBody)
        assertTrue(newFridgeBody.products.isNotEmpty())
        assertTrue(newFridgeBody.products.first().name == "apple")
        assertTrue(newFridgeBody.products.first().quantity == 1)
        assertTrue(newFridgeBody.products.first().openDate == null)
    }

    @Test
    fun `Add product that already exists successfully with code 200`() {
        // given a user (testUser)

        // when adding a product
        val expirationDate = LocalDate.now().plusDays(7)
        addProducts(testUser.token, "peach", 1, null, expirationDate)

        // and adding the same product again
        val newFridgeBody = getBody(addProducts(testUser.token, "peach", 1, null, expirationDate))

        // then the fridge should contain the product with the updated quantity
        assertNotNull(newFridgeBody)
        assertTrue(newFridgeBody.products.isNotEmpty())
        assertTrue(newFridgeBody.products.first().name == "peach")
        assertTrue(newFridgeBody.products.first().quantity == 2)
        assertTrue(newFridgeBody.products.first().openDate == null)
    }

    @Test
    fun `Should fail with code 400 when adding a product with invalid name`() {
        // given a user (testUser)

        // when trying to add a product with an invalid name
        val expirationDate = LocalDate.now().plusDays(7)
        val error = post<Problem>(
            client,
            api(FRIDGE),
            mapOf("productName" to "invalid", "quantity" to 1, "expirationDate" to expirationDate),
            HttpStatus.BAD_REQUEST,
            testUser.token
        )
        assertNotNull(error)

        // then the product cannot be added and fails with code 400
        val errorBody = getBody(error)
        assertEquals(InvalidProduct().message, errorBody.detail)
    }
}
