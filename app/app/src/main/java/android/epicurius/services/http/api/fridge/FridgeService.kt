package android.epicurius.services.http.api.fridge

import android.epicurius.services.http.HttpService
import android.epicurius.services.http.api.fridge.input.AddProductInputModel
import android.epicurius.services.http.api.fridge.input.UpdateProductInputModel
import android.epicurius.services.http.api.fridge.output.AddProductOutputModel
import android.epicurius.services.http.api.fridge.output.GetFridgeOutputModel
import android.epicurius.services.http.api.fridge.output.UpdateProductOutputModel
import android.epicurius.services.http.utils.APIResult
import android.epicurius.services.http.utils.Uris

class FridgeService(private val httpService: HttpService) {

    suspend fun getFridge(
        token: String
    ): APIResult<GetFridgeOutputModel> =
        httpService.get<GetFridgeOutputModel>(
            Uris.Fridge.FRIDGE,
            token = token
        )

    suspend fun addProduct(
        token: String,
        addProductInfo: AddProductInputModel
    ): APIResult<AddProductOutputModel> =
        httpService.post<AddProductOutputModel>(
            Uris.Fridge.FRIDGE,
            addProductInfo,
            token = token
        )

    suspend fun updateFridgeProduct(
        token: String,
        entryNumber: Int,
        updateProductInfo: UpdateProductInputModel
    ): APIResult<UpdateProductOutputModel> =
        httpService.patch<UpdateProductOutputModel>(
            Uris.Fridge.PRODUCT,
            updateProductInfo,
            pathParams = mapOf("entryNumber" to entryNumber),
            token = token
        )

    suspend fun removeFridgeProduct(
        token: String,
        entryNumber: Int
    ): APIResult<UpdateProductOutputModel> =
        httpService.patch<UpdateProductOutputModel>(
            Uris.Fridge.PRODUCT,
            pathParams = mapOf("entryNumber" to entryNumber),
            token = token
        )
}