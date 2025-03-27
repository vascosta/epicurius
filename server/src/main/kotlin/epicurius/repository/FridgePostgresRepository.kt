package epicurius.repository

import epicurius.domain.fridge.Fridge
import epicurius.domain.fridge.Product

interface FridgePostgresRepository {

    fun getFridge(userId: Int): Fridge

    fun addProduct(userId: Int, product: Product): Fridge
}
