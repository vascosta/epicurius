package epicurius.integration.fridge

import epicurius.utils.createTestUser
import java.time.LocalDate
import kotlin.test.Test
import kotlin.test.assertNotNull
import kotlin.test.assertTrue

class GetFridgeIntegrationTests : FridgeIntegrationTest() {

    @Test
    fun `Should retrieve user's fridge successfully with code 200`() {
        // given a user
        val testUser = createTestUser(tm)

        // when getting the user's fridge
        val fridgeBody = getFridge(testUser.token)

        // then the fridge should be empty
        assertNotNull(fridgeBody)
        assertTrue(fridgeBody.fridge.products.isEmpty())
    }

    @Test
    fun `Should retrieve user's fridge with products successfully with code 200`() {
        // given a user and a product in the fridge
        val testUser = createTestUser(tm)

        // when adding a product to the user's fridge
        val today = LocalDate.now()
        val tomorrow = today.plusDays(1)
        addProducts(testUser.token, "milk", 1, today, tomorrow)

        // and when getting the user's fridge
        val fridgeBody = getFridge(testUser.token)

        // then the fridge should contain the product
        assertNotNull(fridgeBody)
        assertTrue(fridgeBody.fridge.products.isNotEmpty())
        assertTrue(fridgeBody.fridge.products.any { it.name == "milk" })
    }
}
