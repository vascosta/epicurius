package epicurius.integration.fridge

import epicurius.integration.EpicuriusIntegrationTest
import epicurius.utils.generateEmail
import epicurius.utils.generateRandomUsername
import epicurius.utils.generateSecurePassword
import org.junit.jupiter.api.BeforeEach

class FridgeIntegrationTest : EpicuriusIntegrationTest() {

    lateinit var testUserToken: String

    @BeforeEach
    fun setup() {
        val username = generateRandomUsername()
        testUserToken = signUp(username, generateEmail(username), "PT", generateSecurePassword())
    }
}
