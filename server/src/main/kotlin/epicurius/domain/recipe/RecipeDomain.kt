package epicurius.domain.recipe

import org.springframework.stereotype.Component

@Component
class RecipeDomain {

    companion object {
        const val MIN_RECIPE_NAME_LENGTH = 3
        const val MAX_RECIPE_NAME_LENGTH = 25
        const val RECIPE_NAME_LENGTH_MSG = "must be between $MIN_RECIPE_NAME_LENGTH and $MAX_RECIPE_NAME_LENGTH characters"

        const val MIN_RECIPE_DESCRIPTION_LENGTH = 1
        const val MAX_RECIPE_DESCRIPTION_LENGTH = 200
        const val RECIPE_DESCRIPTION_LENGTH_MSG = "must be less than $MAX_RECIPE_DESCRIPTION_LENGTH characters"

        const val MIN_INGREDIENT_NAME_LENGTH = 1
        const val MAX_INGREDIENT_NAME_LENGTH = 20
        const val MAX_NUMBER_OF_INGREDIENTS = 30
        const val INGREDIENTS_SIZE_MSG = "The number of ingredients must be at most $MAX_NUMBER_OF_INGREDIENTS"
        const val INGREDIENT_NAME_LENGTH_MSG = "Ingredient name must be between $MIN_INGREDIENT_NAME_LENGTH and $MAX_INGREDIENT_NAME_LENGTH characters"

        const val MIN_INGREDIENT_QUANTITY = 1
        const val INGREDIENT_QUANTITY_MSG = "Ingredient quantity must be greater than $MIN_INGREDIENT_QUANTITY"

        const val MIN_INSTRUCTIONS_STEP_LENGTH = 1
        const val MAX_INSTRUCTIONS_STEP_LENGTH = 200
        const val MAX_NUMBER_OF_INSTRUCTIONS_STEPS = 20
        const val INSTRUCTIONS_STEPS_SIZE_MSG = "The number of instructions steps must be at most $MAX_NUMBER_OF_INSTRUCTIONS_STEPS steps"
        const val INSTRUCTIONS_STEP_LENGTH_MSG =
            "The length of the instructions` step must be between $MIN_INSTRUCTIONS_STEP_LENGTH and $MAX_INSTRUCTIONS_STEP_LENGTH characters"
        const val INSTRUCTIONS_STEP_NUMBER_MSG = "Instructions step number must be a number"

        const val MIN_PICTURES = 1
        const val MAX_PICTURES = 3
    }
}
