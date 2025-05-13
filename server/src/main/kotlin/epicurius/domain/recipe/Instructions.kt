package epicurius.domain.recipe

import epicurius.domain.recipe.RecipeDomain.Companion.INSTRUCTIONS_STEP_LENGTH_MSG
import epicurius.domain.recipe.RecipeDomain.Companion.INSTRUCTIONS_STEP_NUMBER_MSG
import epicurius.domain.recipe.RecipeDomain.Companion.MAX_INSTRUCTIONS_STEP_LENGTH
import epicurius.domain.recipe.RecipeDomain.Companion.MIN_INSTRUCTIONS_STEP_LENGTH

data class Instructions(val steps: Map<String, String>) {

    init {
        steps.forEach { (key, value) ->
            if (value.length !in MIN_INSTRUCTIONS_STEP_LENGTH..MAX_INSTRUCTIONS_STEP_LENGTH) {
                throw IllegalArgumentException(INSTRUCTIONS_STEP_LENGTH_MSG)
            }
            if (key.toIntOrNull() == null) {
                throw IllegalArgumentException(INSTRUCTIONS_STEP_NUMBER_MSG)
            }
        }
    }
}
