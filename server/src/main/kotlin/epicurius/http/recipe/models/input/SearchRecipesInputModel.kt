package epicurius.http.recipe.models.input

import epicurius.domain.Diet
import epicurius.domain.Intolerance
import epicurius.domain.recipe.Cuisine
import epicurius.domain.recipe.Ingredient
import epicurius.domain.recipe.MealType
import epicurius.domain.recipe.RecipeDomain.Companion.MAX_RECIPE_NAME_LENGTH
import epicurius.domain.recipe.RecipeDomain.Companion.RECIPE_NAME_LENGTH_MSG
import epicurius.domain.recipe.SearchRecipesModel

data class SearchRecipesInputModel(
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
) {
    fun toSearchRecipe(name: String?): SearchRecipesModel {
        if (name != null && name.length > MAX_RECIPE_NAME_LENGTH) {
            throw IllegalArgumentException(RECIPE_NAME_LENGTH_MSG)
        }
        return SearchRecipesModel(
            name,
            this.cuisine,
            this.mealType,
            this.ingredients,
            this.intolerances,
            this.diets,
            this.minCalories,
            this.maxCalories,
            this.minCarbs,
            this.maxCarbs,
            this.minFat,
            this.maxFat,
            this.minProtein,
            this.maxProtein,
            this.minTime,
            this.maxTime,
            this.maxResults
        )
    }
}
