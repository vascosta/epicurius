package epicurius.domain.recipe

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
