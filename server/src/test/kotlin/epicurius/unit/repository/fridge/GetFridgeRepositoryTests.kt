package epicurius.unit.repository.fridge

import org.junit.jupiter.api.Assertions.assertTrue
import kotlin.test.Test

class GetFridgeRepositoryTests : FridgeRepositoryTest() {

    @Test
    fun `Retrieve user fridge successfully`() {
        // given a user
        val user = testUser1

        // when retrieving the user's fridge
        val fridge = getFridge(user.id)

        // then the fridge should be empty
        assertTrue(fridge.products.isEmpty())
    }
}