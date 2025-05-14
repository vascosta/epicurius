package epicurius.integration.fridge

import epicurius.utils.generateEmail
import epicurius.utils.generateRandomUsername
import epicurius.utils.generateSecurePassword
import kotlin.test.Test
import kotlin.test.assertNotNull
import kotlin.test.assertTrue

class GetFridgeIntegrationTests: FridgeIntegrationTest() {

    @Test
    fun `Get fridge successfully with code 200`() {
        // given a user token
        val username = generateRandomUsername()
        val testUserToken = signUp(username, generateEmail(username), "PT", generateSecurePassword())

        // when getting the user's fridge
        val fridgeBody = getFridge(testUserToken)

        // then the fridge should be empty
        assertNotNull(fridgeBody)
        assertTrue(fridgeBody.products.isEmpty())
    }
}
