package epicurius.domain.recipe

import epicurius.domain.exceptions.InvalidIngredientUnitIdx

enum class IngredientUnit {
    G, // gram
    Kg, // kilogram
    L, // liter
    ML, // milliliter
    CUPS,
    TBSP, // tablespoon
    TSP, // teaspoon
    DSP, // dessertspoon
    TEA_CUP,
    COFFEE_CUP,
    X; // no unit, e.g. "1 egg", "1 piece of meat", "1 slice of bread", etc.

    companion object {
        fun Companion.fromInt(value: Int): IngredientUnit {
            return IngredientUnit.entries.getOrNull(value) ?: throw InvalidIngredientUnitIdx()
        }
    }
}
