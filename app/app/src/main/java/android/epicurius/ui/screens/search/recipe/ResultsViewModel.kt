package android.epicurius.ui.screens.search.recipe

import android.content.Context
import android.epicurius.domain.Diet
import android.epicurius.domain.Intolerance
import android.epicurius.domain.recipe.Cuisine
import android.epicurius.domain.recipe.Ingredient
import android.epicurius.domain.recipe.MealType
import android.epicurius.domain.recipe.validateCalories
import android.epicurius.domain.recipe.validateCarbs
import android.epicurius.domain.recipe.validateFat
import android.epicurius.domain.recipe.validateIngredients
import android.epicurius.domain.recipe.validateName
import android.epicurius.domain.recipe.validatePreparationTime
import android.epicurius.domain.recipe.validateProtein
import android.epicurius.services.EpicuriusService
import android.epicurius.storage.Session
import android.epicurius.ui.screens.collections.CollectionsViewModel

class ResultsViewModel(
    service: EpicuriusService,
    session: Session,
    context: Context
): CollectionsViewModel(service, session, context) {

    fun searchRecipes(
        name: String?,
        cuisine: List<Cuisine>?,
        mealType: List<MealType>?,
        intolerances: List<Intolerance>?,
        diets: List<Diet>?,
        ingredients: List<Ingredient>?,
        minCalories: Int?,
        maxCalories: Int?,
        minProtein: Int?,
        maxProtein: Int?,
        minFat: Int?,
        maxFat: Int?,
        minCarbs: Int?,
        maxCarbs: Int?,
        minTime: Int?,
        maxTime: Int?
    ) {
        if (
            !validateSearchInfo(
                name,
                ingredients,
                minCalories,
                maxCalories,
                minProtein,
                maxProtein,
                minFat,
                maxFat,
                minCarbs,
                maxCarbs,
                minTime,
                maxTime
            )
        ) {

        }
    }

    private fun validateSearchInfo(
        name: String?,
        ingredients: List<Ingredient>?,
        minCalories: Int?,
        maxCalories: Int?,
        minProtein: Int?,
        maxProtein: Int?,
        minFat: Int?,
        maxFat: Int?,
        minCarbs: Int?,
        maxCarbs: Int?,
        minTime: Int?,
        maxTime: Int?
    ): Boolean =
        when {
            name != null && !validateName(name, ::showToast) -> false
            ingredients != null && !validateIngredients(ingredients, ::showToast) -> false
            minCalories != null && !validateCalories(minCalories, ::showToast) -> false
            maxCalories != null && !validateCalories(maxCalories, ::showToast) -> false
            minCalories != null && maxCalories != null && minCalories > maxCalories -> {
                showToast("max calories must be greater then min calories")
                false
            }
            minProtein != null && !validateProtein(minProtein, ::showToast) -> false
            maxProtein != null && !validateProtein(maxProtein, ::showToast) -> false
            minProtein != null && maxProtein != null && minProtein > maxProtein -> {
                showToast("max protein must be greater then min protein")
                false
            }
            minCarbs != null && !validateCarbs(minCarbs, ::showToast) -> false
            maxCarbs != null && !validateCarbs(maxCarbs, ::showToast) -> false
            minCarbs != null && maxCarbs != null && minCarbs > maxCarbs -> {
                showToast("max carbs must be greater then min carbs")
                false
            }
            minFat != null && !validateFat(minFat, ::showToast) -> false
            maxFat != null && !validateFat(maxFat, ::showToast) -> false
            minFat != null && maxFat != null && minFat > maxFat -> {
                showToast("max fat must be greater then min fat")
                false
            }
            minTime != null && !validatePreparationTime(minTime, ::showToast) -> false
            maxTime != null && !validatePreparationTime(maxTime, ::showToast) -> false
            minTime != null && maxTime != null && minTime > maxTime -> {
                showToast("max preparation time must be greater then min preparation time")
                false
            }
            else -> true
        }
}
