package epicurius.repository.jdbi.fridge

import epicurius.domain.fridge.Fridge
import epicurius.domain.fridge.Product
import epicurius.domain.fridge.ProductInfo
import epicurius.domain.fridge.UpdateProductInfo

interface FridgeRepository {

    fun getFridge(userId: Int): Fridge

    fun addProduct(userId: Int, product: ProductInfo): Fridge

    fun updateProduct(userId: Int, product: UpdateProductInfo): Fridge

    fun removeProduct(userId: Int, entryNumber: Int): Fridge

    fun checkIfProductExistsInFridge(userId: Int, entryNumber: Int?, product: ProductInfo?): Product?
    fun checkIfProductIsOpen(userId: Int, entryNumber: Int): Boolean
}
