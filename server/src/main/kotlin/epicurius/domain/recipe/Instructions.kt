package epicurius.domain.recipe

data class Instructions(val instr: Map<Int, String>) {

    fun getInstruction(instructionNumber: Int): String? {
        return instr[instructionNumber - 1]
    }
}