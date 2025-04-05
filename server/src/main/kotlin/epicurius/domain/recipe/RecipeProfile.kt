package epicurius.domain.recipe

data class RecipeProfile (
    val id: Int,
    val name: String,
    val cuisine: Cuisine,
    val mealType: MealType,
    val preparationTime: Int,
    val servings: Int
)