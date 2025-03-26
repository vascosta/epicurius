package epicurius.repository

import epicurius.domain.fridge.Fridge
interface FridgePostgresRepository {

    fun getFridge(userId: Int): Fridge?

    //fun addProduct(product: String, quantity: Int, openDate: Date? = null, ): Fridge
}