package epicurius.domain.recipe

import epicurius.domain.exceptions.InvalidMealTypeIdx

enum class MealType {
    MAIN_COURSE,
    SIDE_DISH,
    DESSERT,
    APPETIZER,
    SALAD,
    BREAD,
    BREAKFAST,
    SOUP,
    BEVERAGE,
    SAUCE,
    MARINADE,
    FINGERFOOD,
    SNACK;

    companion object {
        fun Companion.fromInt(value: Int): MealType {
            return MealType.entries.getOrNull(value) ?: throw InvalidMealTypeIdx()
        }
    }
}
