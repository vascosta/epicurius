package epicurius.unit.repository.fridge

import epicurius.domain.fridge.ProductInfo
import epicurius.domain.fridge.UpdateProductInfo
import epicurius.utils.createTestUser
import org.junit.jupiter.api.Assertions.assertEquals
import java.time.LocalDate
import kotlin.test.Test

class UpdateFridgeProductRepositoryTests : FridgeRepositoryTest() {

    @Test
    fun `Update product in user fridge successfully`() {
        // given a user
        val user = createTestUser(tm)

        // and a product in the user's fridge
        val product = ProductInfo(
            name = "Apple",
            quantity = 1,
            openDate = null,
            expirationDate = LocalDate.now().plusDays(7)
        )
        val fridge = addProduct(user.user.id, product)

        val entryNumber = fridge.products.first().entryNumber

        // when updating the product in the user's fridge
        val newQuantity = 2
        val newExpirationDate = LocalDate.now().plusDays(6)
        val updatedProduct = UpdateProductInfo(
            entryNumber = entryNumber,
            quantity = newQuantity,
            expirationDate = newExpirationDate
        )
        val updatedFridge = updateProduct(user.user.id, updatedProduct)

        // then the fridge should contain the updated product
        assertEquals(1, updatedFridge.products.size)
        assertEquals(product.name, updatedFridge.products.first().name)
        assertEquals(newQuantity, updatedFridge.products.first().quantity)
        assertEquals(product.openDate, updatedFridge.products.first().openDate)
        assertEquals(newExpirationDate, updatedFridge.products.first().expirationDate)
    }
}
