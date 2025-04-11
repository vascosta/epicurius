package epicurius.services.recipe.models

import epicurius.domain.Diet
import epicurius.domain.Intolerance
import epicurius.domain.recipe.Cuisine
import epicurius.domain.recipe.Ingredient
import epicurius.domain.recipe.Instructions
import epicurius.domain.recipe.MealType
import java.util.Date

data class UpdateRecipeModel(
    val id: Int,
    val name: String,
    val authorUsername: String,
    val date: Date,
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
)
