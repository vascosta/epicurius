package epicurius.repository.jdbi.mealPlanner.contract

import epicurius.domain.mealPlanner.MealTime
import epicurius.repository.jdbi.mealPlanner.models.JdbiCalories
import epicurius.repository.jdbi.mealPlanner.models.JdbiDailyMealPlanner
import epicurius.repository.jdbi.mealPlanner.models.JdbiMealPlanner
import java.time.LocalDate

interface MealPlannerRepository {

    fun getWeeklyMealPlanner(userId: Int): JdbiMealPlanner

    fun getDailyMealPlanner(userId: Int, date: LocalDate): JdbiDailyMealPlanner
    fun createDailyMealPlanner(userId: Int, date: LocalDate, maxCalories: Int?)
    fun addDailyMealPlanner(userId: Int, date: LocalDate, recipeId: Int, mealTime: MealTime): JdbiMealPlanner
    fun updateDailyMealPlanner(userId: Int, date: LocalDate, recipeId: Int, mealTime: MealTime): JdbiMealPlanner
    fun removeMealTimeDailyMealPlanner(userId: Int, date: LocalDate, mealTime: MealTime): JdbiMealPlanner
    fun deleteDailyMealPlanner(userId: Int, date: LocalDate): JdbiMealPlanner

    fun updateDailyCalories(userId: Int, date: LocalDate, calories: Int?): JdbiDailyMealPlanner

    fun checkIfDailyMealPlannerAlreadyExists(userId: Int, date: LocalDate): Boolean
    fun checkIfMealTimeAlreadyExistsInPlanner(userId: Int, date: LocalDate, mealTime: MealTime): Boolean
}
