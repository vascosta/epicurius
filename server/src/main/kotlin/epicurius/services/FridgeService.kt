package epicurius.services

import epicurius.domain.exceptions.InvalidProduct
import epicurius.domain.exceptions.ProductIsAlreadyOpen
import epicurius.domain.exceptions.ProductNotFound
import epicurius.domain.fridge.Fridge
import epicurius.domain.fridge.FridgeDomain
import epicurius.domain.fridge.Product
import epicurius.domain.fridge.ProductInfo
import epicurius.domain.fridge.UpdateProductInfo
import epicurius.http.fridge.models.input.OpenProductInputModel
import epicurius.http.fridge.models.input.ProductInputModel
import epicurius.http.fridge.models.input.UpdateProductInputModel
import epicurius.repository.spoonacular.SpoonacularManager
import epicurius.repository.transaction.TransactionManager
import org.springframework.stereotype.Component

@Component
class FridgeService(
    private val tm: TransactionManager,
    private val sm: SpoonacularManager,
    private val fridgeDomain: FridgeDomain
) {
    fun getFridge(userId: Int): Fridge = tm.run { it.fridgeRepository.getFridge(userId) }

    suspend fun getProductsList(partial: String): List<String> =
        sm.spoonacularRepository.getProductsList(partial)

    suspend fun addProduct(userId: Int, productInput: ProductInputModel): Fridge {
        checkIfProductIsValid(productInput.productName)

        val product = ProductInfo(
            productName = productInput.productName,
            quantity = productInput.quantity,
            openDate = productInput.openDate,
            expirationDate = productInput.expirationDate
        )

        // checkIfProductExistsInFridge(userId, product = product)
        // se existir um produto com o mesmo nome e data de válida, atualiza a entrada

        return tm.run { it.fridgeRepository.addProduct(userId, product) }
    }

    fun updateProductInfo(userId: Int, entryNumber: Int, info: UpdateProductInputModel): Fridge {
        checkIfProductExistsInFridge(userId, entryNumber) ?: throw ProductNotFound()
        if (info.expirationDate != null) checkIfProductIsOpen(userId, entryNumber)

        val updateProduct = UpdateProductInfo(
            entryNumber = entryNumber,
            quantity = info.quantity,
            expirationDate = info.expirationDate
        )
        return tm.run { it.fridgeRepository.updateProduct(userId, updateProduct) }
    }

    fun openProduct(userId: Int, entryNumber: Int, product: OpenProductInputModel): Fridge {
        val observedProduct = checkIfProductExistsInFridge(userId, entryNumber) ?: throw ProductNotFound()
        checkIfProductIsOpen(userId, observedProduct.entryNumber)

        val updateProduct = observedProduct.copy().let {
            UpdateProductInfo(entryNumber = it.entryNumber, quantity = it.quantity - 1)
        }
        tm.run { it.fridgeRepository.updateProduct(userId, updateProduct) }

        val expireDate = fridgeDomain.calculateExpirationDate(product.openDate, product.duration)
        val newProduct = ProductInfo(
            productName = observedProduct.productName,
            quantity = 1,
            openDate = product.openDate,
            expirationDate = expireDate
        )

        return tm.run { it.fridgeRepository.addProduct(userId, newProduct) }
    }

    private suspend fun checkIfProductIsValid(productName: String) {
        val validName = productName.replace(" ", "-").lowercase()
        val productList = sm.spoonacularRepository.getProductsList(validName)
        if (!productList.contains(productName)) throw InvalidProduct()
    }

    private fun checkIfProductExistsInFridge(
        userId: Int,
        entryNumber: Int? = null,
        product: ProductInfo? = null
    ): Product? = tm.run { it.fridgeRepository.checkIfProductExistsInFridge(userId, entryNumber, product) }

    private fun checkIfProductIsOpen(userId: Int, entryNumber: Int) {
        val state = tm.run { it.fridgeRepository.checkIfProductIsOpen(userId, entryNumber) }
        if (!state) throw ProductIsAlreadyOpen()
    }
}
