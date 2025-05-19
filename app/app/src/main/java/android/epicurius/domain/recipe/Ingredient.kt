package android.epicurius.domain.recipe

data class Ingredient(
    val name: String,
    val quantity: Double,
    val unit: IngredientUnit
)
