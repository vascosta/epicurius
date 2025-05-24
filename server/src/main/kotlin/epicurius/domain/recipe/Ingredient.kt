package epicurius.domain.recipe

data class Ingredient(
    val name: String,
    val quantity: Double,
    val unit: IngredientUnit
) {

    init {
        if (name.length !in MIN_INGREDIENT_NAME_LENGTH..MAX_INGREDIENT_NAME_LENGTH) {
            throw IllegalArgumentException(INGREDIENT_NAME_LENGTH_MSG)
        }

        if (quantity < MIN_INGREDIENT_QUANTITY) {
            throw IllegalArgumentException(INGREDIENT_QUANTITY_MSG)
        }
    }
}
