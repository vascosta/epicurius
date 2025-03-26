package epicurius.http.fridge.models.output

import epicurius.domain.fridge.Product

data class FridgeOutputModel(val products: List<Product> = emptyList())
