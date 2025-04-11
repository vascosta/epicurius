package epicurius.domain.recipe

import org.springframework.stereotype.Component

@Component
class RecipeDomain {

    companion object {
        const val MIN_RECIPE_NAME_LENGTH = 3
        const val MAX_RECIPE_NAME_LENGTH = 25
        const val RECIPE_NAME_LENGTH_MSG = "must be between $MIN_RECIPE_NAME_LENGTH and $MAX_RECIPE_NAME_LENGTH characters"

        const val MAX_RECIPE_DESCRIPTION_LENGTH = 200
        const val RECIPE_DESCRIPTION_LENGTH_MSG = "must be less than $MAX_RECIPE_DESCRIPTION_LENGTH characters"

        const val MIN_INSTRUCTIONS_LENGTH = 1
        const val MAX_INSTRUCTIONS_LENGTH = 200
        const val INSTRUCTIONS_LENGTH_MSG = "Instructions length must be between $MIN_INSTRUCTIONS_LENGTH and $MAX_INSTRUCTIONS_LENGTH characters"
        const val INSTRUCTIONS_STEP_NUMBER_MSG = "Instructions step number must be a number"

        const val MIN_INGREDIENT_NAME_LENGTH = 1
        const val MAX_INGREDIENT_NAME_LENGTH = 20
        const val INGREDIENT_NAME_LENGTH_MSG = "Ingredient name must be between $MIN_INGREDIENT_NAME_LENGTH and $MAX_INGREDIENT_NAME_LENGTH characters"

        const val MIN_INGREDIENT_QUANTITY = 1
        const val INGREDIENT_QUANTITY_MSG = "Ingredient quantity must be greater than $MIN_INGREDIENT_QUANTITY"

        const val MIN_PICTURES = 1
        const val MAX_PICTURES = 3
    }
}
