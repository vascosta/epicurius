package epicurius.repository.jdbi.mealPlanner

import epicurius.domain.mealPlanner.MealTime
import epicurius.repository.jdbi.mealPlanner.contract.MealPlannerRepository
import epicurius.repository.jdbi.mealPlanner.models.JdbiCalories
import epicurius.repository.jdbi.mealPlanner.models.JdbiDailyMealPlanner
import epicurius.repository.jdbi.mealPlanner.models.JdbiDailyMealPlannerRow
import epicurius.repository.jdbi.mealPlanner.models.JdbiMealPlanner
import org.jdbi.v3.core.Handle
import org.jdbi.v3.core.kotlin.mapTo
import java.time.LocalDate

class JdbiMealPlannerRepository(private val handle: Handle) : MealPlannerRepository {

    override fun createDailyMealPlanner(userId: Int, date: LocalDate) {
        handle.createUpdate(
            """
                INSERT INTO dbo.meal_planner (user_id, date)
                VALUES (:id, :date)
            """
        )
            .bind("id", userId)
            .bind("date", date)
            .execute()
    }

    override fun getWeeklyMealPlanner(userId: Int): JdbiMealPlanner {
        val list = handle.createQuery(
            """
                SELECT mp.date, mpr.meal_time, 
                       r.id AS recipe_id, r.name AS recipe_name, 
                       r.cuisine, r.meal_type, r.preparation_time, r.servings, r.pictures_names
                FROM dbo.meal_planner mp 
                JOIN dbo.meal_planner_recipe mpr ON mp.user_id = mpr.user_id AND mp.date = mpr.date
                JOIN dbo.recipe r ON mpr.recipe_id = r.id
                WHERE mp.user_id = :id
            """
        )
            .bind("id", userId)
            .mapTo<JdbiDailyMealPlannerRow>()
            .list()

        val dailyPlanners = list
            .groupBy { it.date }
            .map { (date, dayRows) ->
                val meals = dayRows.associate { it.mealTime to it.jdbiRecipeInfo }
                JdbiDailyMealPlanner(date, meals)
            }

        return JdbiMealPlanner(dailyPlanners)
    }

    override fun addDailyMealPlanner(userId: Int, date: LocalDate, recipeId: Int, mealTime: MealTime) {
        handle.createUpdate(
            """
                INSERT INTO dbo.meal_planner_recipe (user_id, date, recipe_id, meal_time)
                VALUES (:userId, :date, :recipeId, :mealTime)
            """
        )
            .bind("userId", userId)
            .bind("date", date)
            .bind("recipeId", recipeId)
            .bind("mealTime", mealTime.ordinal)
            .execute()
    }

    override fun updateDailyMealPlanner(userId: Int, date: LocalDate, recipeId: Int, mealTime: MealTime): JdbiMealPlanner {
        handle.createUpdate(
            """
                DELETE FROM dbo.meal_planner_recipe 
                WHERE user_id = :userId AND date = :date AND meal_time = :mealTime
            """
        )
            .bind("userId", userId)
            .bind("date", date)
            .bind("mealTime", mealTime.ordinal)
            .execute()

        handle.createUpdate(
            """
                INSERT INTO dbo.meal_planner_recipe(user_id, date, recipe_id, meal_time)
                VALUES (:userId, :date, :recipeId, :mealTime)
            """
        )
            .bind("userId", userId)
            .bind("date", date)
            .bind("recipeId", recipeId)
            .bind("mealTime", mealTime.ordinal)
            .execute()

        return getWeeklyMealPlanner(userId)
    }

    override fun removeMealTimeDailyMealPlanner(userId: Int, date: LocalDate, mealTime: MealTime): JdbiMealPlanner {
        handle.createUpdate(
            """
                DELETE FROM dbo.meal_planner_recipe
                WHERE user_id = :userId AND date = :date AND meal_time = :mealTime
            """
        )
            .bind("userId", userId)
            .bind("date", date)
            .bind("mealTime", mealTime.ordinal)
            .execute()

        return getWeeklyMealPlanner(userId)
    }

    override fun getDailyCalories(userId: Int, date: LocalDate): JdbiCalories =
        handle.createQuery(
            """
                SELECT c.max_calories, COALESCE(SUM(r.calories), 0) as reached_calories
                FROM dbo.calories c 
                LEFT JOIN dbo.meal_planner_recipe mpr ON c.user_id = mpr.user_id AND c.date = mpr.date
                LEFT JOIN dbo.recipe r ON mpr.recipe_id = r.id
                WHERE c.user_id = :id AND c.date = :date
                GROUP BY c.date, c.max_calories
            """
        )
            .bind("id", userId)
            .bind("date", date)
            .mapTo<JdbiCalories>()
            .firstOrNull() ?: JdbiCalories(maxCalories = null, reachedCalories = 0)

    override fun checkIfDailyMealPlannerAlreadyExists(userId: Int, date: LocalDate): Boolean =
        handle.createQuery(
            """
                SELECT COUNT(*)
                FROM dbo.meal_planner
                WHERE user_id = :id AND date = :date
            """
        )
            .bind("id", userId)
            .bind("date", date)
            .mapTo<Int>()
            .first() == 1

    override fun checkIfMealTimeAlreadyExistsInPlanner(userId: Int, date: LocalDate, mealTime: MealTime): Boolean =
        handle.createQuery(
            """
                SELECT COUNT(*)
                FROM dbo.meal_planner_recipe
                WHERE user_id = :id AND date = :date AND meal_time = :time
            """
        )
            .bind("id", userId)
            .bind("date", date)
            .bind("time", mealTime.ordinal)
            .mapTo<Int>()
            .first() == 1
}
