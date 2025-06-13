package epicurius.repository.jdbi.recipe.models

import epicurius.domain.recipe.Ingredient
import epicurius.domain.recipe.Instructions

data class JdbiUpdateRecipeModel(
    val id: Int,
    val name: String? = null,
    val description: String? = null,
    val servings: Int? = null,
    val preparationTime: Int? = null,
    val cuisine: Int? = null,
    val mealType: Int? = null,
    val intolerances: Set<Int>? = null,
    val diets: Set<Int>? = null,
    val ingredients: List<Ingredient>? = null,
    val calories: Int? = null,
    val protein: Int? = null,
    val fat: Int? = null,
    val carbs: Int? = null,
    val instructions: Instructions? = null,
    val picturesNames: List<String>? = null,
)
