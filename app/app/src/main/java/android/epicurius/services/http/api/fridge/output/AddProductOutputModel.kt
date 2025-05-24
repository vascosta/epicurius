package android.epicurius.services.http.api.fridge.output

import android.epicurius.domain.fridge.Fridge

data class AddProductOutputModel(val fridge: Fridge)

typealias UpdateProductOutputModel = AddProductOutputModel

typealias RemoveProductOutputModel = AddProductOutputModel