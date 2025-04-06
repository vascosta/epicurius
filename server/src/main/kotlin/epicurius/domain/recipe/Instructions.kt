package epicurius.domain.recipe

import epicurius.domain.recipe.RecipeDomain.Companion.INSTRUCTIONS_LENGTH_MSG
import epicurius.domain.recipe.RecipeDomain.Companion.MAX_INSTRUCTIONS_LENGTH
import epicurius.domain.recipe.RecipeDomain.Companion.MIN_INSTRUCTIONS_LENGTH

data class Instructions(val steps: Map<Int, String>) {

    init {
        steps.values.forEach { step ->
            if (step.length !in MIN_INSTRUCTIONS_LENGTH..MAX_INSTRUCTIONS_LENGTH) {
                throw IllegalArgumentException(INSTRUCTIONS_LENGTH_MSG)
            }
        }
    }

    fun getInstruction(instructionNumber: Int): String? {
        return steps[instructionNumber - 1]
    }
}
