package epicurius.domain.mealPlanner

import epicurius.domain.exceptions.InvalidMealTypeIdx

enum class MealTime {
    BREAKFAST,
    LUNCH,
    DINNER,
    SNACK;

    companion object {
        fun Companion.fromInt(value: Int): MealTime {
            return MealTime.entries.getOrNull(value) ?: throw InvalidMealTypeIdx()
        }
    }
}
