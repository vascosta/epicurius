package android.epicurius.services.api.recipe

import android.epicurius.domain.Diet
import android.epicurius.domain.Intolerance
import android.epicurius.domain.recipe.Cuisine
import android.epicurius.domain.recipe.MealType
import android.epicurius.domain.recipe.UpdateRecipePicturesModel
import android.epicurius.services.api.recipe.models.input.CreateRecipeInputModel
import android.epicurius.services.api.recipe.models.input.UpdateRecipeInputModel
import android.epicurius.services.api.recipe.models.output.CreateRecipeOutputModel
import android.epicurius.services.api.recipe.models.output.GetRecipeOutputModel
import android.epicurius.services.api.recipe.models.output.SearchRecipesOutputModel
import android.epicurius.services.api.recipe.models.output.UpdateRecipeOutputModel
import android.epicurius.services.api.recipe.models.output.UpdateRecipePicturesOutputModel
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

    suspend fun searchRecipes(
        token: String,
        name: String?,
        cuisine: List<Cuisine>?,
        mealType: List<MealType>?,
        ingredients: List<String>?,
        intolerances: List<Intolerance>?,
        diets: List<Diet>?,
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
        skip: Int,
        limit: Int,
    ): APIResult<SearchRecipesOutputModel> =
        httpService.get<SearchRecipesOutputModel>(
            Uris.Recipe.RECIPES,
            queryParams = mapOf(
                "name" to name,
                "cuisine" to cuisine,
                "mealType" to mealType,
                "ingredients" to ingredients,
                "intolerances" to intolerances,
                "diets" to diets,
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
                "skip" to skip,
                "limit" to limit,
            ),
            token = token
        )

    suspend fun createRecipe(
        token: String,
        createRecipeInfo: CreateRecipeInputModel,
        pictures: List<UpdateRecipePicturesModel>
    ): APIResult<CreateRecipeOutputModel> =
        httpService.postMultipartWithJsonAndFiles<CreateRecipeOutputModel>(
            Uris.Recipe.RECIPES,
            "body",
            createRecipeInfo,
            "pictures",
            pictures,
            token
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
        pictures: List<UpdateRecipePicturesModel>
    ): APIResult<UpdateRecipePicturesOutputModel> =
        httpService.patchMultipart<UpdateRecipePicturesOutputModel>(
            Uris.Recipe.RECIPE_PICTURES,
            "pictures",
            pictures,
            mapOf("id" to id),
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
}