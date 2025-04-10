package epicurius.repository.jdbi.recipe.models

import epicurius.domain.Diet
import epicurius.domain.Intolerance
import epicurius.domain.recipe.Cuisine
import epicurius.domain.recipe.Ingredient
import epicurius.domain.recipe.MealType

data class JdbiUpdateRecipeModel(
    val id: Int,
    val name: String?,
    val servings: Int?,
    val preparationTime: Int?,
    val cuisine: Cuisine?,
    val mealType: MealType?,
    val intolerances: List<Intolerance>?,
    val diets: List<Diet>?,
    val ingredients: List<Ingredient>?,
    val calories: Int?,
    val protein: Int?,
    val fat: Int?,
    val carbs: Int?,
    val picturesNames: List<String>?,
)
