package epicurius.domain.recipe

import epicurius.domain.recipe.RecipeDomain.Companion.INSTRUCTIONS_LENGTH_MSG
import epicurius.domain.recipe.RecipeDomain.Companion.INSTRUCTIONS_STEP_NUMBER_MSG
import epicurius.domain.recipe.RecipeDomain.Companion.MAX_INSTRUCTIONS_LENGTH
import epicurius.domain.recipe.RecipeDomain.Companion.MIN_INSTRUCTIONS_LENGTH

data class Instructions(val steps: Map<String, String>) {

    init {
        steps.values.forEach { value ->
            if (value.length !in MIN_INSTRUCTIONS_LENGTH..MAX_INSTRUCTIONS_LENGTH) {
                throw IllegalArgumentException(INSTRUCTIONS_LENGTH_MSG)
            }
        }

        steps.keys.forEach { key ->
            if (key.toIntOrNull() == null) {
                throw IllegalArgumentException(INSTRUCTIONS_STEP_NUMBER_MSG)
            }
        }
    }
}
