package epicurius.domain.exceptions

import epicurius.domain.mealPlanner.MealTime
import java.time.LocalDate

class MealPlannerAlreadyExists(date: LocalDate) : RuntimeException("Meal planner already exists for date $date")
class MealTimeAlreadyExistsInPlanner(mealTime: MealTime) : RuntimeException("Meal time $mealTime already exists in daily planner")
class DailyMealPlannerNotFound : RuntimeException("Meal planner not found")
class MealTimeDoesNotExist : RuntimeException("Meal time does not exist in daily planner")

class RecipeDoesNotContainCaloriesInfo : RuntimeException("Recipe doesn't contain calories info")
class RecipeExceedsMaximumCalories : RuntimeException("Recipe exceeds maximum calories")
class RecipeIsInvalidForMealTime : RuntimeException("Recipe is invalid for meal time")

class InvalidMealPlannerDate : RuntimeException("Date must be present or future")

class InvalidMealTimeIdx : RuntimeException("Invalid meal time index")
