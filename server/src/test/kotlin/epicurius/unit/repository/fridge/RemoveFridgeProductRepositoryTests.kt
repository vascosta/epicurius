package epicurius.unit.repository.fridge

import epicurius.domain.fridge.ProductInfo
import epicurius.utils.createTestUser
import org.junit.jupiter.api.Assertions.assertTrue
import java.time.LocalDate
import kotlin.test.Test

class RemoveFridgeProductRepositoryTests : FridgeRepositoryTest() {

    @Test
    fun `Remove product from user fridge successfully`() {
        // given a user
        val user = createTestUser(tm)

        // and a product in the user's fridge
        val product = ProductInfo(
            name = "Peach",
            quantity = 1,
            openDate = null,
            expirationDate = LocalDate.now().plusDays(7)
        )
        val fridge = addProduct(user.user.id, product)

        val entryNumber = fridge.products.first().entryNumber

        // when removing the product from the user's fridge
        val removedFridge = removeProduct(user.user.id, entryNumber)

        // then the fridge should be empty
        assertTrue(removedFridge.products.isEmpty())
    }
}
