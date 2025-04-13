package epicurius.repository.jdbi.mealPlanner

import epicurius.repository.jdbi.mealPlanner.models.JdbiMealPlanner
import java.util.Date

interface MealPlannerRepository {

    fun createMealPlanner(userId: Int, date: Date)

    fun getMealPlanner(userId: Int): JdbiMealPlanner
}
