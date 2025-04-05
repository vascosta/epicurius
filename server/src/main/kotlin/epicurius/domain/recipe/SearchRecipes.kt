package epicurius.domain.recipe

import epicurius.domain.Cuisine
import epicurius.domain.Diet
import epicurius.domain.Ingredient
import epicurius.domain.Intolerance
import epicurius.domain.MealType

data class SearchRecipes(
    val name: String?,
    val cuisine: Cuisine?,
    val mealType: MealType?,
    val ingredients: List<Ingredient>?,
    val intolerances: List<Intolerance>?,
    val diets: List<Diet>?,
    val minCalories: Int?,
    val maxCalories: Int?,
    val minCarbs: Int?,
    val maxCarbs: Int?,
    val minFat: Int?,
    val maxFat: Int?,
    val minProtein: Int?,
    val maxProtein: Int?,
    val minTime: Int?,
    val maxTime: Int?,
    val maxResults: Int = 10,
)
