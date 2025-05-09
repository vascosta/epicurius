package epicurius.http.controllers.recipe.models.input

import epicurius.domain.Diet
import epicurius.domain.Intolerance
import epicurius.domain.recipe.Cuisine
import epicurius.domain.recipe.MealType
import epicurius.domain.recipe.RecipeDomain
import epicurius.domain.recipe.RecipeDomain.Companion.MAX_RECIPE_NAME_LENGTH
import epicurius.domain.recipe.RecipeDomain.Companion.RECIPE_NAME_LENGTH_MSG
import epicurius.domain.recipe.SearchRecipesModel
import epicurius.domain.user.UserDomain
import jakarta.validation.constraints.Positive

data class SearchRecipesInputModel(
    val name: String? = null,
    val cuisine: List<Cuisine>? = null,
    val mealType: List<MealType>? = null,
    val ingredients: List<String>? = null,
    val intolerances: List<Intolerance>? = null,
    val diets: List<Diet>? = null,

    @field:Positive(message = UserDomain.POSITIVE_NUMBER_MSG)
    val minCalories: Int? = null,

    @field:Positive(message = UserDomain.POSITIVE_NUMBER_MSG)
    val maxCalories: Int? = null,

    @field:Positive(message = UserDomain.POSITIVE_NUMBER_MSG)
    val minCarbs: Int? = null,

    @field:Positive(message = UserDomain.POSITIVE_NUMBER_MSG)
    val maxCarbs: Int? = null,

    @field:Positive(message = UserDomain.POSITIVE_NUMBER_MSG)
    val minFat: Int? = null,

    @field:Positive(message = UserDomain.POSITIVE_NUMBER_MSG)
    val maxFat: Int? = null,

    @field:Positive(message = UserDomain.POSITIVE_NUMBER_MSG)
    val minProtein: Int? = null,

    @field:Positive(message = UserDomain.POSITIVE_NUMBER_MSG)
    val maxProtein: Int? = null,

    @field:Positive(message = UserDomain.POSITIVE_NUMBER_MSG)
    val minTime: Int? = null,

    @field:Positive(message = UserDomain.POSITIVE_NUMBER_MSG)
    val maxTime: Int? = null,
) {
    init {
        if (ingredients != null && ingredients.size > RecipeDomain.MAX_NUMBER_OF_INGREDIENTS) {
            throw IllegalArgumentException(RecipeDomain.INGREDIENTS_SIZE_MSG)
        }

        if (intolerances != null && intolerances.size > UserDomain.MAX_INTOLERANCE_SIZE) {
            throw IllegalArgumentException(UserDomain.MAX_INTOLERANCE_SIZE_MSG)
        }

        if (diets != null && diets.size > UserDomain.MAX_DIET_SIZE) {
            throw IllegalArgumentException(UserDomain.MAX_DIET_SIZE_MSG)
        }
    }

    fun toSearchRecipeModel(name: String?): SearchRecipesModel {
        if (name != null && name.length > MAX_RECIPE_NAME_LENGTH) {
            throw IllegalArgumentException(RECIPE_NAME_LENGTH_MSG)
        }
        return SearchRecipesModel(
            name,
            this.cuisine?.map { it.ordinal },
            this.mealType?.map { it.ordinal },
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
            this.maxTime
        )
    }
}
