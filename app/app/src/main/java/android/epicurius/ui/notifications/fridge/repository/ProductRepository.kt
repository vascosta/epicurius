package android.epicurius.ui.notifications.fridge.repository

import android.epicurius.domain.fridge.Product
import android.epicurius.services.api.fridge.FridgeService
import android.epicurius.storage.Session

class ProductRepository(
    private val session: Session,
    private val fridgeService: FridgeService
) {

    suspend fun getUserFridgeProducts(): List<Product> {
        try {
            val token = session.getToken()
            val response = fridgeService.getFridge(token)
            return response.getValueOrThrow().fridge.products
        } catch (E: Exception) {
            return emptyList()
        }
    }
}
