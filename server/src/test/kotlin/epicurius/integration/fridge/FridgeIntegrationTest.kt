package epicurius.integration.fridge

import epicurius.domain.user.AuthenticatedUser
import epicurius.integration.EpicuriusIntegrationTest
import epicurius.utils.createTestUser
import org.junit.jupiter.api.BeforeEach

class FridgeIntegrationTest : EpicuriusIntegrationTest() {

    lateinit var testUser: AuthenticatedUser

    @BeforeEach
    fun setup() {
        testUser = createTestUser(tm)
    }
}
