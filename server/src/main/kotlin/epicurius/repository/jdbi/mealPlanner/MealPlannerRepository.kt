package epicurius.repository.jdbi.mealPlanner

import java.util.Date

interface MealPlannerRepository {

    fun createMealPlanner(userId: Int, date: Date)
}