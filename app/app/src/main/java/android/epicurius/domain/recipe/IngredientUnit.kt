package android.epicurius.domain.recipe

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
}
