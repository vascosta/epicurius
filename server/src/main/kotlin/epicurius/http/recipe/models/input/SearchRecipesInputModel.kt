package epicurius.http.recipe.models.input

import epicurius.domain.Diet
import epicurius.domain.Intolerance
import epicurius.domain.recipe.Cuisine
import epicurius.domain.recipe.MealType
import epicurius.domain.recipe.RecipeDomain.Companion.MAX_RECIPE_NAME_LENGTH
import epicurius.domain.recipe.RecipeDomain.Companion.RECIPE_NAME_LENGTH_MSG
import epicurius.domain.recipe.SearchRecipesModel
import epicurius.domain.user.UserDomain

data class SearchRecipesInputModel(
    val name: String? = null,
    val cuisine: Cuisine? = null,
    val mealType: MealType? = null,
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
    val maxResults: Int = 10,
) {
    init {
        if (intolerances != null && intolerances.size > UserDomain.MAX_INTOLERANCE_SIZE) {
            throw IllegalArgumentException(UserDomain.MAX_INTOLERANCE_SIZE_MSG)
        }

        if (diets != null && diets.size > UserDomain.MAX_DIET_SIZE) {
            throw IllegalArgumentException(UserDomain.MAX_DIET_SIZE_MSG)
        }
    }

    fun toSearchRecipe(name: String?): SearchRecipesModel {
        if (name != null && name.length > MAX_RECIPE_NAME_LENGTH) {
            throw IllegalArgumentException(RECIPE_NAME_LENGTH_MSG)
        }
        return SearchRecipesModel(
            name,
            this.cuisine?.ordinal,
            this.mealType?.ordinal,
            this.ingredients,
            this.intolerances?.map { it.ordinal },
            this.diets?.map { it.ordinal },
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
