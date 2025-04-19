package epicurius.repository.jdbi.mealPlanner.contract

import epicurius.domain.mealPlanner.MealTime
import epicurius.repository.jdbi.mealPlanner.models.JdbiCalories
import epicurius.repository.jdbi.mealPlanner.models.JdbiMealPlanner
import java.time.LocalDate

interface MealPlannerRepository {

    fun createMealPlanner(userId: Int, date: LocalDate)

    fun getMealPlanner(userId: Int): JdbiMealPlanner
    fun addMealPlanner(userId: Int, date: LocalDate, recipeId: Int, mealTime: MealTime): JdbiMealPlanner

    fun getDailyCalories(userId: Int, date: LocalDate): JdbiCalories

    fun checkIfMealPlannerAlreadyExists(userId: Int, date: LocalDate): Boolean
    fun checkIfMealTimeAlreadyExistsInPlanner(userId: Int, date: LocalDate, mealTime: MealTime): Boolean
}
