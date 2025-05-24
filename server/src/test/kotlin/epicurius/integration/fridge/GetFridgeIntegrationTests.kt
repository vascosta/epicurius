package epicurius.integration.fridge

import epicurius.utils.createTestUser
import kotlin.test.Test
import kotlin.test.assertNotNull
import kotlin.test.assertTrue

class GetFridgeIntegrationTests : FridgeIntegrationTest() {

    @Test
    fun `Get fridge successfully with code 200`() {
        // given a user
        val testUser = createTestUser(tm)

        // when getting the user's fridge
        val fridgeBody = getFridge(testUser.token)

        // then the fridge should be empty
        assertNotNull(fridgeBody)
        assertTrue(fridgeBody.products.isEmpty())
    }
}
