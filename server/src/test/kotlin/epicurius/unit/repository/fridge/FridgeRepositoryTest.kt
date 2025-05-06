package epicurius.unit.repository.fridge

import epicurius.domain.fridge.ProductInfo
import epicurius.domain.fridge.UpdateProductInfo
import epicurius.unit.repository.RepositoryTest
import epicurius.utils.createTestUser

open class FridgeRepositoryTest : RepositoryTest() {

    companion object {

        val testUser1 = createTestUser(tm)
        val testUser2 = createTestUser(tm, true)
        val testUser3 = createTestUser(tm)
        val testUser4 = createTestUser(tm, true)
        val testUser5 = createTestUser(tm)
        val testUser6 = createTestUser(tm, true)
        val testUser7 = createTestUser(tm)

        fun getFridge(userId: Int) = tm.run { it.fridgeRepository.getFridge(userId) }

        fun addProduct(userId: Int, product: ProductInfo) = tm.run { it.fridgeRepository.addProduct(userId, product) }

        fun updateProduct(userId: Int, product: UpdateProductInfo) =
            tm.run { it.fridgeRepository.updateProduct(userId, product) }

        fun removeProduct(userId: Int, entryNumber: Int) =
            tm.run { it.fridgeRepository.removeProduct(userId, entryNumber) }

        fun checkIfProductExistsInFridge(userId: Int, entryNumber: Int?, product: ProductInfo?) =
            tm.run { it.fridgeRepository.checkIfProductExistsInFridge(userId, entryNumber, product) }

        fun checkIfProductIsOpen(userId: Int, entryNumber: Int) =
            tm.run { it.fridgeRepository.checkIfProductIsOpen(userId, entryNumber) }
    }
}
