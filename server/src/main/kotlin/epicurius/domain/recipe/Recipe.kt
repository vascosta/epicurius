package epicurius.domain.recipe

import epicurius.domain.Diet
import epicurius.domain.Intolerance
import java.time.LocalDate

data class Recipe(
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
    val instructions: Instructions,
    val pictures: List<ByteArray>
)
