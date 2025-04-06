package epicurius.domain.recipe

import epicurius.domain.recipe.RecipeDomain.Companion.INGREDIENT_NAME_LENGTH_MSG
import epicurius.domain.recipe.RecipeDomain.Companion.INGREDIENT_QUANTITY_MSG
import epicurius.domain.recipe.RecipeDomain.Companion.MAX_INGREDIENT_NAME_LENGTH
import epicurius.domain.recipe.RecipeDomain.Companion.MIN_INGREDIENT_NAME_LENGTH
import epicurius.domain.recipe.RecipeDomain.Companion.MIN_INGREDIENT_QUANTITY

data class Ingredient(
    val name: String,
    val quantity: Int,
    val unit: IngredientUnit
) {

    init {
        if (name.length in MIN_INGREDIENT_NAME_LENGTH..MAX_INGREDIENT_NAME_LENGTH) {
            throw IllegalArgumentException(INGREDIENT_NAME_LENGTH_MSG)
        }

        if (quantity <= MIN_INGREDIENT_QUANTITY) {
            throw IllegalArgumentException(INGREDIENT_QUANTITY_MSG)
        }
    }
}
