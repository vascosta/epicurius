package android.epicurius.services.http.api.fridge.output

import android.epicurius.domain.fridge.Product

data class GetFridgeOutputModel(val products: List<Product> = emptyList())
