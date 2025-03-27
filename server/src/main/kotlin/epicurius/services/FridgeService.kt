package epicurius.services

import epicurius.domain.exceptions.InvalidProduct
import epicurius.domain.fridge.Fridge
import epicurius.domain.fridge.Product
import epicurius.http.fridge.models.input.ProductInputModel
import epicurius.repository.spoonacular.SpoonacularManager
import epicurius.repository.transaction.TransactionManager
import org.springframework.stereotype.Component

@Component
class FridgeService(private val tm: TransactionManager, private val sm: SpoonacularManager) {
    fun getFridge(userId: Int): Fridge {
        val fridge = tm.run { it.fridgeRepository.getFridge(userId) }

        return fridge
    }

    suspend fun getProductsList(partial: String): List<String> =
        sm.spoonacularRepository.getProductsList(partial)

    suspend fun addProduct(userId: Int, product: ProductInputModel): Fridge {
        checkIfProductIsValid(product.productName)
        return tm.run {
            it.fridgeRepository.addProduct(
                userId,
                Product(
                    product.productName,
                    product.quantity,
                    product.openDate,
                    product.expirationDate
                )
            )
        }
    }

    private suspend fun checkIfProductIsValid(productName: String) {
        val productList = sm.spoonacularRepository.getProductsList(partial = productName)
        if (!productList.contains(productName)) throw InvalidProduct()
    }
}
