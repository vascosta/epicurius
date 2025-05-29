package android.epicurius.domain.recipe

import android.epicurius.domain.Diet
import android.epicurius.domain.Intolerance
import java.time.LocalDate

data class UpdateRecipeModel(
    val id: Int,
    val name: String,
    val authorUsername: String,
    val date: LocalDate,
    val description: String,
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
    fun toRecipe(pictures: List<ByteArray>) =
        Recipe(
            id,
            name,
            authorUsername,
            date,
            description,
            servings,
            preparationTime,
            cuisine,
            mealType,
            intolerances,
            diets,
            ingredients,
            calories,
            protein,
            fat,
            carbs,
            instructions,
            pictures
        )
}
