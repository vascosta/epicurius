package android.epicurius.services.http.api.fridge.output

import android.epicurius.domain.fridge.Fridge

data class GetFridgeOutputModel(val fridge: Fridge)

typealias AddProductOutputModel = GetFridgeOutputModel

typealias UpdateProductOutputModel = GetFridgeOutputModel

typealias RemoveProductOutputModel = GetFridgeOutputModel
