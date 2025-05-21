package android.epicurius.services.http.api.ingredients

import android.epicurius.services.http.HttpService
import android.epicurius.services.http.api.ingredients.models.output.GetIngredientsOutputModel
import android.epicurius.services.http.api.ingredients.models.output.GetSubstituteIngredientsOutputModel
import android.epicurius.services.http.api.ingredients.models.output.IdentifyIngredientsInPictureOutputModel
import android.epicurius.services.http.api.user.models.output.UpdateUserProfilePictureOutputModel
import android.epicurius.services.http.utils.APIResult
import android.epicurius.services.http.utils.Uris

class IngredientsService(private val httpService: HttpService) {

    suspend fun getIngredients(
        token: String,
        partialIngredientName: String
    ): APIResult<GetIngredientsOutputModel> =
        httpService.get<GetIngredientsOutputModel>(
            Uris.Ingredients.INGREDIENTS,
            queryParams = mapOf("partial" to partialIngredientName),
            token = token
        )

    suspend fun getSubstituteIngredients(
        token: String,
        ingredientName: String
    ): APIResult<GetSubstituteIngredientsOutputModel> =
        httpService.get<GetSubstituteIngredientsOutputModel>(
            Uris.Ingredients.INGREDIENTS_SUBSTITUTES,
            queryParams = mapOf("name" to ingredientName),
            token = token
        )

    suspend fun identifyIngredientsInPicture(
        token: String,
        pictureName : String,
        picture: ByteArray
    ): APIResult<IdentifyIngredientsInPictureOutputModel> =
        httpService.postMultipart<IdentifyIngredientsInPictureOutputModel>(
            Uris.Ingredients.INGREDIENTS,
            "picture",
            pictureName,
            picture,
            token = token
        )
}