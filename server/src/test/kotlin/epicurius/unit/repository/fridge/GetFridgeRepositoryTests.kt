package epicurius.unit.repository.fridge

import epicurius.utils.createTestUser
import org.junit.jupiter.api.Assertions.assertTrue
import kotlin.test.Test

class GetFridgeRepositoryTests : FridgeRepositoryTest() {

    @Test
    fun `Retrieve user fridge successfully`() {
        // given a user
        val user = createTestUser(tm)

        // when retrieving the user's fridge
        val fridge = getFridge(user.user.id)

        // then the fridge should be empty
        assertTrue(fridge.products.isEmpty())
    }
}
