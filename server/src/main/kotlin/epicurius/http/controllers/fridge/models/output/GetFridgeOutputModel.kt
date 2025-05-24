package epicurius.http.controllers.fridge.models.output

import epicurius.domain.fridge.Fridge

data class GetFridgeOutputModel(val fridge: Fridge)

typealias AddProductOutputModel = GetFridgeOutputModel

typealias UpdateProductOutputModel = GetFridgeOutputModel

typealias RemoveProductOutputModel = GetFridgeOutputModel
