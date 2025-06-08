package android.epicurius.domain.recipe

enum class IngredientUnit(val displayName: String) {
    G("g"), // gram
    Kg("Kg"), // kilogram
    L("L"), // liter
    ML("ml"), // milliliter
    CUPS("Cups"),
    TBSP("Table spoon"), // tablespoon
    TSP("Tea spoon"), // teaspoon
    DSP("Dessert spoon"), // dessertspoon
    TEA_CUP("Tea cup"), // tea cup
    COFFEE_CUP("Coffee cup"), // coffee cup
    X(""); // no unit, e.g. "1 egg", "1 piece of meat", "1 slice of bread", etc.

    companion object {
        fun fromString(unit: String): IngredientUnit {
            return entries.find { it.displayName.equals(unit, ignoreCase = true) } ?: X
        }
    }
}
