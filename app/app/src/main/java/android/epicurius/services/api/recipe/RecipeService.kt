package android.epicurius.services.api.recipe

import android.epicurius.domain.Diet
import android.epicurius.domain.Intolerance
import android.epicurius.domain.Picture
import android.epicurius.domain.recipe.Cuisine
import android.epicurius.domain.recipe.MealType
import android.epicurius.services.api.recipe.models.input.CreateRecipeInputModel
import android.epicurius.services.api.recipe.models.input.RateRecipeInputModel
import android.epicurius.services.api.recipe.models.input.UpdateRecipeInputModel
import android.epicurius.services.api.recipe.models.output.CreateRecipeOutputModel
import android.epicurius.services.api.recipe.models.output.GetRecipeOutputModel
import android.epicurius.services.api.recipe.models.output.GetRecipeRateOutputModel
import android.epicurius.services.api.recipe.models.output.GetUserRecipeRateOutputModel
import android.epicurius.services.api.recipe.models.output.GetUserRecipesOutputModel
import android.epicurius.services.api.recipe.models.output.RateRecipeOutputModel
import android.epicurius.services.api.recipe.models.output.SearchRecipesOutputModel
import android.epicurius.services.api.recipe.models.output.UpdateRecipeOutputModel
import android.epicurius.services.api.recipe.models.output.UpdateRecipePicturesOutputModel
import android.epicurius.services.api.recipe.models.output.UpdateRecipeRateOutputModel
import android.epicurius.services.http.HttpService
import android.epicurius.services.http.utils.APIResult
import android.epicurius.services.http.utils.Uris

class RecipeService(private val httpService: HttpService) {

    suspend fun getRecipe(
        token: String,
        id: Int
    ): APIResult<GetRecipeOutputModel> =
        httpService.get<GetRecipeOutputModel>(
            Uris.Recipe.RECIPE,
            pathParams = mapOf("id" to id),
            token = token
        )

    suspend fun getUserRecipes(
        token: String,
        lastRecipeId: Int?,
        limit: Int
    ): APIResult<GetUserRecipesOutputModel> =
        httpService.get<GetUserRecipesOutputModel>(
            Uris.User.USER_RECIPES,
            queryParams = mapOf("lastRecipeId" to lastRecipeId, "limit" to limit),
            token = token
        )

    suspend fun getRecipeRate(
        token: String,
        id: Int
    ): APIResult<GetRecipeRateOutputModel> =
        httpService.get<GetRecipeRateOutputModel>(
            Uris.Recipe.RATE_RECIPE,
            pathParams = mapOf("id" to id),
            token = token
        )

    suspend fun getUserRecipeRate(
        token: String,
        id: Int
    ): APIResult<GetUserRecipeRateOutputModel> =
        httpService.get<GetUserRecipeRateOutputModel>(
            Uris.Recipe.USER_RECIPE_RATE,
            pathParams = mapOf("id" to id),
            token = token
        )

    suspend fun searchRecipes(
        token: String,
        name: String?,
        cuisine: List<Cuisine>?,
        mealType: List<MealType>?,
        ingredients: List<String>?,
        intolerances: List<Intolerance>?,
        diets: List<Diet>?,
        servings: Int?,
        minCalories: Int?,
        maxCalories: Int?,
        minCarbs: Int?,
        maxCarbs: Int?,
        minFat: Int?,
        maxFat: Int?,
        minProtein: Int?,
        maxProtein: Int?,
        minTime: Int?,
        maxTime: Int?,
        lastRecipeId: Int?,
        limit: Int,
    ): APIResult<SearchRecipesOutputModel> =
        httpService.get<SearchRecipesOutputModel>(
            Uris.Recipe.RECIPES,
            queryParams = mapOf(
                "name" to name,
                "cuisine" to cuisine?.joinToString(","),
                "mealType" to mealType?.joinToString(","),
                "ingredients" to ingredients?.joinToString(","),
                "intolerances" to intolerances?.joinToString(","),
                "diets" to diets,
                "servings" to servings,
                "minCalories" to minCalories,
                "maxCalories" to maxCalories,
                "minCarbs" to minCarbs,
                "maxCarbs" to maxCarbs,
                "minFat" to minFat,
                "maxFat" to maxFat,
                "minProtein" to minProtein,
                "maxProtein" to maxProtein,
                "minTime" to minTime,
                "maxTime" to maxTime,
                "lastRecipeId" to lastRecipeId,
                "limit" to limit,
            ),
            token = token
        )

    suspend fun createRecipe(
        token: String,
        createRecipeInfo: CreateRecipeInputModel,
        picturesBytes: List<ByteArray>
    ): APIResult<CreateRecipeOutputModel> =
        httpService.postMultipartWithJsonAndFiles<CreateRecipeOutputModel>(
            Uris.Recipe.RECIPES,
            "body",
            createRecipeInfo,
            "pictures",
            picturesBytes.map { Picture("picture", it) },
            token
        )

    suspend fun rateRecipe(
        token: String,
        id: Int,
        rateRecipeInfo: RateRecipeInputModel
    ): APIResult<RateRecipeOutputModel> =
        httpService.post<RateRecipeOutputModel>(
            Uris.Recipe.RATE_RECIPE,
            rateRecipeInfo,
            pathParams = mapOf("id" to id),
            token = token
        )

    suspend fun updateRecipe(
        token: String,
        id: Int,
        updateRecipeInfo: UpdateRecipeInputModel
    ): APIResult<UpdateRecipeOutputModel> =
        httpService.patch<UpdateRecipeOutputModel>(
            Uris.Recipe.RECIPE,
            updateRecipeInfo,
            pathParams = mapOf("id" to id),
            token = token
        )

    suspend fun updateRecipePictures(
        token: String,
        id: Int,
        pictures: List<Picture>
    ): APIResult<UpdateRecipePicturesOutputModel> =
        httpService.patchMultipart<UpdateRecipePicturesOutputModel>(
            Uris.Recipe.RECIPE_PICTURES,
            "pictures",
            pictures,
            mapOf("id" to id),
            token = token
        )

    suspend fun updateRecipeRate(
        token: String,
        id: Int,
        rateRecipeInfo: RateRecipeInputModel
    ): APIResult<UpdateRecipeRateOutputModel> =
        httpService.patch<UpdateRecipeRateOutputModel>(
            Uris.Recipe.RATE_RECIPE,
            rateRecipeInfo,
            pathParams = mapOf("id" to id),
            token = token
        )

    suspend fun deleteRecipe(
        token: String,
        id: Int,
    ): APIResult<Unit> =
        httpService.delete<Unit>(
            Uris.Recipe.RECIPE,
            pathParams = mapOf("id" to id),
            token = token
        )

    suspend fun deleteRecipeRate(
        token: String,
        id: Int,
    ): APIResult<Unit> =
        httpService.delete<Unit>(
            Uris.Recipe.RATE_RECIPE,
            pathParams = mapOf("id" to id),
            token = token
        )
}