package epicurius.http.recipe.models.input

import epicurius.domain.Diet
import epicurius.domain.Intolerance
import epicurius.domain.recipe.CreateRecipeModel
import epicurius.domain.recipe.Cuisine
import epicurius.domain.recipe.Ingredient
import epicurius.domain.recipe.Instructions
import epicurius.domain.recipe.MealType
import epicurius.domain.recipe.RecipeDomain
import jakarta.validation.constraints.Size

data class CreateRecipeInputModel(
    @field:Size(min = RecipeDomain.MIN_RECIPE_NAME_LENGTH, max = RecipeDomain.MAX_RECIPE_NAME_LENGTH, message = RecipeDomain.RECIPE_NAME_LENGTH_MSG)
    val name: String,

    @field:Size(max = RecipeDomain.MAX_RECIPE_DESCRIPTION_LENGTH, message = RecipeDomain.RECIPE_DESCRIPTION_LENGTH_MSG)
    val description: String?,

    val servings: Int,
    val preparationTime: Int,
    val cuisine: Cuisine,
    val mealType: MealType,
    val intolerances: List<Intolerance>,
    val diets: List<Diet>,
    val ingredients: List<Ingredient>,
    val calories: Int?,
    val protein: Int?,
    val fat: Int?,
    val carbs: Int?,
    val instructions: Instructions
) {
    fun toCreateRecipeInputModel(authorId: Int, picturesNames: List<String>): CreateRecipeModel {
        return CreateRecipeModel(
            name = name,
            authorId = authorId,
            servings = servings,
            preparationTime = preparationTime,
            cuisine = cuisine,
            mealType = mealType,
            intolerances = intolerances,
            diets = diets,
            ingredients = ingredients,
            calories = calories,
            protein = protein,
            fat = fat,
            carbs = carbs,
            picturesNames = picturesNames
        )
    }
}

