package epicurius.domain.recipe

data class RecipeInfo(
    val id: Int,
    val name: String,
    val authorUsername: String,
    val rating: Double,
    val cuisine: Cuisine,
    val mealType: MealType,
    val preparationTime: Int,
    val servings: Int,
    val picture: ByteArray
)
