package epicurius.repository.jdbi.recipe.models

import epicurius.domain.recipe.Ingredient

data class JdbiUpdateRecipeModel(
    val id: Int,
    val name: String?,
    val servings: Int?,
    val preparationTime: Int?,
    val cuisine: Int?,
    val mealType: Int?,
    val intolerances: List<Int>?,
    val diets: List<Int>?,
    val ingredients: List<Ingredient>?,
    val calories: Int?,
    val protein: Int?,
    val fat: Int?,
    val carbs: Int?,
    val picturesNames: List<String>?,
)
