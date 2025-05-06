package epicurius.unit.repository.fridge

import epicurius.domain.fridge.ProductInfo
import org.junit.jupiter.api.Assertions.assertEquals
import java.time.LocalDate
import kotlin.test.Test

class AddFridgeProductRepositoryTests : FridgeRepositoryTest() {

    @Test
    fun `Add product to user fridge successfully`() {
        // given a user
        val user = testUser2

        // when adding a product to the user's fridge
        val product = ProductInfo(
            productName = "Milk",
            quantity = 1,
            openDate = null,
            expirationDate = LocalDate.now().plusDays(7)
        )
        val fridge = addProduct(user.id, product)

        // then the fridge should contain the added product
        assertEquals(1, fridge.products.size)
        assertEquals(product.productName, fridge.products.first().productName)
        assertEquals(product.quantity, fridge.products.first().quantity)
        assertEquals(product.openDate, fridge.products.first().openDate)
        assertEquals(product.expirationDate, fridge.products.first().expirationDate)
    }
}