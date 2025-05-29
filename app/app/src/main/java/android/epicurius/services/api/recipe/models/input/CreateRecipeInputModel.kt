package android.epicurius.services.api.recipe.models.input

import android.epicurius.domain.Diet
import android.epicurius.domain.Intolerance
import android.epicurius.domain.recipe.Cuisine
import android.epicurius.domain.recipe.Ingredient
import android.epicurius.domain.recipe.Instructions
import android.epicurius.domain.recipe.MealType

data class CreateRecipeInputModel(
    val name: String,
    val description: String,
    val servings: Int,
    val preparationTime: Int,
    val cuisine: Cuisine,
    val mealType: MealType,
    val intolerances: Set<Intolerance>,
    val diets: Set<Diet>,
    val ingredients: List<Ingredient>,
    val calories: Int? = null,
    val protein: Int? = null,
    val fat: Int? = null,
    val carbs: Int? = null,
    val instructions: Instructions
)
