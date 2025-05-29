package android.epicurius.services.api.recipe.models.input

import android.epicurius.domain.Diet
import android.epicurius.domain.Intolerance
import android.epicurius.domain.recipe.Cuisine
import android.epicurius.domain.recipe.MealType

data class SearchRecipesInputModel(
    val name: String? = null,
    val cuisine: List<Cuisine>? = null,
    val mealType: List<MealType>? = null,
    val ingredients: List<String>? = null,
    val intolerances: List<Intolerance>? = null,
    val diets: List<Diet>? = null,
    val minCalories: Int? = null,
    val maxCalories: Int? = null,
    val minCarbs: Int? = null,
    val maxCarbs: Int? = null,
    val minFat: Int? = null,
    val maxFat: Int? = null,
    val minProtein: Int? = null,
    val maxProtein: Int? = null,
    val minTime: Int? = null,
    val maxTime: Int? = null,
)
