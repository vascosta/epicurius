package epicurius.services.fridge

import epicurius.domain.exceptions.InvalidProduct
import epicurius.domain.exceptions.ProductIsAlreadyOpen
import epicurius.domain.exceptions.ProductNotFound
import epicurius.domain.fridge.Fridge
import epicurius.domain.fridge.FridgeDomain
import epicurius.domain.fridge.Product
import epicurius.domain.fridge.ProductInfo
import epicurius.domain.fridge.UpdateProductInfo
import epicurius.http.fridge.models.input.ProductInputModel
import epicurius.http.fridge.models.input.UpdateProductInputModel
import epicurius.repository.spoonacular.manager.SpoonacularManager
import epicurius.repository.transaction.TransactionManager
import org.springframework.stereotype.Component
import java.time.LocalDate
import java.time.Period

@Component
class FridgeService(
    private val tm: TransactionManager,
    private val sm: SpoonacularManager,
    private val fridgeDomain: FridgeDomain
) {
    fun getFridge(userId: Int): Fridge = tm.run { it.fridgeRepository.getFridge(userId) }

    suspend fun getProductsList(partial: String): List<String> =
        sm.spoonacularRepository.getProductsList(partial)

    suspend fun addProduct(userId: Int, body: ProductInputModel): Fridge {
        checkIfProductIsValid(body.productName)

        val product = body.toProductInfo()
        checkIfProductExistsInFridge(userId, product = product)?.let { existingProduct ->
            val updatedProduct = UpdateProductInfo(
                entryNumber = existingProduct.entryNumber,
                quantity = existingProduct.quantity + body.quantity
            )
            return tm.run { it.fridgeRepository.updateProduct(userId, updatedProduct) }
        }

        return tm.run { it.fridgeRepository.addProduct(userId, product) }
    }

    fun updateProductInfo(userId: Int, entryNumber: Int, body: UpdateProductInputModel): Fridge {
        val observedProduct = checkIfProductExistsInFridge(userId, entryNumber) ?: throw ProductNotFound(entryNumber)
        if (body.expirationDate != null || body.openDate != null) checkIfProductIsOpen(userId, entryNumber)

        return if (body.openDate != null) {
            openProduct(userId, observedProduct, body.openDate, body.duration)
        } else {
            val updatedProduct = UpdateProductInfo(
                entryNumber = entryNumber,
                quantity = body.quantity ?: observedProduct.quantity,
                expirationDate = body.expirationDate ?: observedProduct.expirationDate
            )

            tm.run { it.fridgeRepository.updateProduct(userId, updatedProduct) }
        }
    }

    private fun openProduct(userId: Int, observedProduct: Product, openDate: LocalDate, duration: Period?): Fridge {
        val decreaseQuantity = UpdateProductInfo(
            entryNumber = observedProduct.entryNumber,
            quantity = observedProduct.quantity - 1
        )
        tm.run { it.fridgeRepository.updateProduct(userId, decreaseQuantity) }

        val newProduct = ProductInfo(
            productName = observedProduct.productName,
            quantity = 1,
            openDate = openDate,
            expirationDate =
            if (duration != null) fridgeDomain.calculateExpirationDate(openDate, duration)
            else observedProduct.expirationDate
        )

        checkIfProductExistsInFridge(userId, product = newProduct)?.let { existingProduct ->
            val updatedProduct = UpdateProductInfo(
                entryNumber = existingProduct.entryNumber,
                quantity = existingProduct.quantity + 1
            )
            return tm.run { it.fridgeRepository.updateProduct(userId, updatedProduct) }
        }

        return tm.run { it.fridgeRepository.addProduct(userId, newProduct) }
    }

    fun removeProduct(userId: Int, entryNumber: Int): Fridge {
        checkIfProductExistsInFridge(userId, entryNumber) ?: throw ProductNotFound(entryNumber)
        return tm.run { it.fridgeRepository.removeProduct(userId, entryNumber) }
    }

    private suspend fun checkIfProductIsValid(productName: String) {
        val productList = sm.spoonacularRepository.getProductsList(productName)
        if (!productList.contains(productName)) throw InvalidProduct()
    }

    private fun checkIfProductExistsInFridge(
        userId: Int,
        entryNumber: Int? = null,
        product: ProductInfo? = null
    ): Product? = tm.run { it.fridgeRepository.checkIfProductExistsInFridge(userId, entryNumber, product) }

    private fun checkIfProductIsOpen(userId: Int, entryNumber: Int) {
        val state = tm.run { it.fridgeRepository.checkIfProductIsOpen(userId, entryNumber) }
        if (state) throw ProductIsAlreadyOpen()
    }
}
