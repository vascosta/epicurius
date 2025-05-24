package epicurius.http.controllers.fridge.models.output

import epicurius.domain.fridge.Fridge

data class AddProductOutputModel(val fridge: Fridge)

typealias UpdateProductOutputModel = AddProductOutputModel

typealias RemoveProductOutputModel = AddProductOutputModel