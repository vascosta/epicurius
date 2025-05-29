package android.epicurius.services.api.recipe.models.input

import android.epicurius.domain.Diet
import android.epicurius.domain.Intolerance
import android.epicurius.domain.recipe.Cuisine
import android.epicurius.domain.recipe.Ingredient
import android.epicurius.domain.recipe.Instructions
import android.epicurius.domain.recipe.MealType

data class UpdateRecipeInputModel(
    val name: String? = null,
    val description: String? = null,
    val servings: Int? = null,
    val preparationTime: Int? = null,
    val cuisine: Cuisine? = null,
    val mealType: MealType? = null,
    val intolerances: Set<Intolerance>? = null,
    val diets: Set<Diet>? = null,
    val ingredients: List<Ingredient>? = null,
    val calories: Int? = null,
    val protein: Int? = null,
    val fat: Int? = null,
    val carbs: Int? = null,
    val instructions: Instructions? = null,
)
