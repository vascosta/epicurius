package epicurius.repository.jdbi.mealPlanner.contract

import epicurius.domain.mealPlanner.MealTime
import epicurius.repository.jdbi.mealPlanner.models.JdbiCalories
import epicurius.repository.jdbi.mealPlanner.models.JdbiMealPlanner
import java.time.LocalDate

interface MealPlannerRepository {

    fun createDailyMealPlanner(userId: Int, date: LocalDate)

    fun getWeeklyMealPlanner(userId: Int): JdbiMealPlanner
    fun addDailyMealPlanner(userId: Int, date: LocalDate, recipeId: Int, mealTime: MealTime)
    fun updateDailyMealPlanner(userId: Int, date: LocalDate, recipeId: Int, mealTime: MealTime): JdbiMealPlanner
    fun removeMealTimeDailyMealPlanner(userId: Int, date: LocalDate, mealTime: MealTime): JdbiMealPlanner

    fun getDailyCalories(userId: Int, date: LocalDate): JdbiCalories

    fun checkIfDailyMealPlannerAlreadyExists(userId: Int, date: LocalDate): Boolean
    fun checkIfMealTimeAlreadyExistsInPlanner(userId: Int, date: LocalDate, mealTime: MealTime): Boolean
}
