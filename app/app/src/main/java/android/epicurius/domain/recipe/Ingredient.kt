package android.epicurius.domain.recipe

import android.epicurius.ui.screens.recipe.createRecipe.IngredientComponent

data class Ingredient(
    val name: String,
    val quantity: Double,
    val unit: IngredientUnit
) {
    fun toIngredientComponent(): IngredientComponent {
        return IngredientComponent(
            name = name,
            quantity = quantity.toString(),
            unit = unit.displayName
        )
    }
}
