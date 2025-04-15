package epicurius.domain.exceptions

import epicurius.domain.mealPlanner.MealTime
import java.time.LocalDate

class MealPlannerAlreadyExists(date: LocalDate) : Exception("Meal planner already exists for date $date")
class MealTimeAlreadyExistsInPlanner(mealTime: MealTime) : Exception("Meal time $mealTime already exists in daily planner")
class MealPlannerNotFound : Exception("Meal planner not found")

class RecipeDoesNotContainCaloriesInfo : Exception("Recipe doesn't contain calories info")
class RecipeExceedsMaximumCalories : Exception("Recipe exceeds maximum calories")
class RecipeIsInvalidForMealTime : Exception("Recipe is invalid for meal time")

class InvalidMealPlannerDate : Exception("Date must be present or future")

class InvalidMealTimeIdx : Exception("Invalid meal time index")
