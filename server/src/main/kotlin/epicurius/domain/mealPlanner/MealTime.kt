package epicurius.domain.mealPlanner

import epicurius.domain.exceptions.InvalidMealTimeIdx
import epicurius.domain.recipe.MealType

enum class MealTime {
    BREAKFAST,
    LUNCH,
    DINNER,
    SNACK;

    fun isMealTypeAllowedForMealTime(mealType: MealType): Boolean {
        return when (this) {
            BREAKFAST -> mealType in setOf(
                MealType.BREAKFAST, MealType.BEVERAGE, MealType.BREAD
            )
            LUNCH -> mealType in setOf(
                MealType.MAIN_COURSE, MealType.SIDE_DISH, MealType.SALAD,
                MealType.DESSERT, MealType.BEVERAGE, MealType.BREAD, MealType.SOUP,
                MealType.SAUCE, MealType.MARINADE
            )
            DINNER -> mealType in setOf(
                MealType.MAIN_COURSE, MealType.SIDE_DISH, MealType.SALAD,
                MealType.DESSERT, MealType.BEVERAGE, MealType.BREAD, MealType.SOUP,
                MealType.SAUCE, MealType.MARINADE
            )
            SNACK -> mealType in setOf(
                MealType.SNACK, MealType.FINGERFOOD, MealType.BEVERAGE, MealType.DESSERT
            )
        }
    }

    companion object {
        fun Companion.fromInt(value: Int): MealTime {
            return MealTime.entries.getOrNull(value) ?: throw InvalidMealTimeIdx()
        }
    }
}
