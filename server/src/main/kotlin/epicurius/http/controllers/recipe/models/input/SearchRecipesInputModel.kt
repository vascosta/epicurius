package epicurius.http.controllers.recipe.models.input

import epicurius.domain.Diet
import epicurius.domain.Intolerance
import epicurius.domain.recipe.Cuisine
import epicurius.domain.recipe.MealType
import epicurius.domain.recipe.RecipeDomain
import epicurius.domain.recipe.RecipeDomain.Companion.INVALID_CALORIES_VALUES
import epicurius.domain.recipe.RecipeDomain.Companion.INVALID_CARBS_VALUES
import epicurius.domain.recipe.RecipeDomain.Companion.INVALID_FAT_VALUES
import epicurius.domain.recipe.RecipeDomain.Companion.INVALID_PREPARATION_TIME_VALUES
import epicurius.domain.recipe.RecipeDomain.Companion.INVALID_PROTEIN_VALUES
import epicurius.domain.recipe.RecipeDomain.Companion.MAX_NUMBER_OF_INGREDIENTS
import epicurius.domain.recipe.RecipeDomain.Companion.MAX_RECIPE_NAME_LENGTH
import epicurius.domain.recipe.RecipeDomain.Companion.RECIPE_NAME_LENGTH_MSG
import epicurius.domain.recipe.SearchRecipesModel
import epicurius.domain.user.UserDomain.Companion.MAX_DIET_SIZE
import epicurius.domain.user.UserDomain.Companion.MAX_DIET_SIZE_MSG
import epicurius.domain.user.UserDomain.Companion.MAX_INTOLERANCE_SIZE
import epicurius.domain.user.UserDomain.Companion.MAX_INTOLERANCE_SIZE_MSG
import epicurius.domain.user.UserDomain.Companion.POSITIVE_NUMBER_MSG
import jakarta.validation.constraints.Positive

data class SearchRecipesInputModel(
    val name: String? = null,
    val cuisine: List<Cuisine>? = null,
    val mealType: List<MealType>? = null,
    val ingredients: List<String>? = null,
    val intolerances: List<Intolerance>? = null,
    val diets: List<Diet>? = null,

    @field:Positive(message = POSITIVE_NUMBER_MSG)
    val minCalories: Int? = null,

    @field:Positive(message = POSITIVE_NUMBER_MSG)
    val maxCalories: Int? = null,

    @field:Positive(message = POSITIVE_NUMBER_MSG)
    val minCarbs: Int? = null,

    @field:Positive(message = POSITIVE_NUMBER_MSG)
    val maxCarbs: Int? = null,

    @field:Positive(message = POSITIVE_NUMBER_MSG)
    val minFat: Int? = null,

    @field:Positive(message = POSITIVE_NUMBER_MSG)
    val maxFat: Int? = null,

    @field:Positive(message = POSITIVE_NUMBER_MSG)
    val minProtein: Int? = null,

    @field:Positive(message = POSITIVE_NUMBER_MSG)
    val maxProtein: Int? = null,

    @field:Positive(message = POSITIVE_NUMBER_MSG)
    val minTime: Int? = null,

    @field:Positive(message = POSITIVE_NUMBER_MSG)
    val maxTime: Int? = null,
) {
    init {
        if (ingredients != null && ingredients.size > MAX_NUMBER_OF_INGREDIENTS) {
            throw IllegalArgumentException(RecipeDomain.INGREDIENTS_SIZE_MSG)
        }

        if (intolerances != null && intolerances.size > MAX_INTOLERANCE_SIZE) {
            throw IllegalArgumentException(MAX_INTOLERANCE_SIZE_MSG)
        }

        if (diets != null && diets.size > MAX_DIET_SIZE) {
            throw IllegalArgumentException(MAX_DIET_SIZE_MSG)
        }

        if (minCalories != null && maxCalories != null) {
            if (minCalories >= maxCalories) throw IllegalArgumentException(INVALID_CALORIES_VALUES)
        }

        if (minCarbs != null && maxCarbs != null) {
            if (minCarbs >= maxCarbs) throw IllegalArgumentException(INVALID_CARBS_VALUES)
        }

        if (minFat != null && maxFat != null) {
            if (minFat >= maxFat) throw IllegalArgumentException(INVALID_FAT_VALUES)
        }

        if (minProtein != null && maxProtein != null) {
            if (minProtein >= maxProtein) throw IllegalArgumentException(INVALID_PROTEIN_VALUES)
        }

        if (minTime != null && maxTime != null) {
            if (minTime >= maxTime) throw IllegalArgumentException(INVALID_PREPARATION_TIME_VALUES)
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
