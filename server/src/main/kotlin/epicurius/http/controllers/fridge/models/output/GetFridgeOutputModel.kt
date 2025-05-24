package epicurius.http.controllers.fridge.models.output

import epicurius.domain.fridge.Product

data class GetFridgeOutputModel(val products: List<Product> = emptyList())
