package epicurius.unit.repository.fridge

import epicurius.domain.fridge.ProductInfo
import epicurius.utils.createTestUser
import org.junit.jupiter.api.Assertions.assertEquals
import java.time.LocalDate
import kotlin.test.Test

class AddFridgeProductRepositoryTests : FridgeRepositoryTest() {

    @Test
    fun `Add product to user fridge successfully`() {
        // given a user
        val user = createTestUser(tm)

        // when adding a product to the user's fridge
        val product = ProductInfo(
            name = "Milk",
            quantity = 1,
            openDate = null,
            expirationDate = LocalDate.now().plusDays(7)
        )
        val fridge = addProduct(user.user.id, product)

        // then the fridge should contain the added product
        assertEquals(1, fridge.products.size)
        assertEquals(product.name, fridge.products.first().name)
        assertEquals(product.quantity, fridge.products.first().quantity)
        assertEquals(product.openDate, fridge.products.first().openDate)
        assertEquals(product.expirationDate, fridge.products.first().expirationDate)
    }
}
