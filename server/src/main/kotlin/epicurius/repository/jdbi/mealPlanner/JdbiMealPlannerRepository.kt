package epicurius.repository.jdbi.mealPlanner

import epicurius.domain.exceptions.DailyMealPlannerNotFound
import epicurius.domain.mealPlanner.MealTime
import epicurius.repository.jdbi.mealPlanner.contract.MealPlannerRepository
import epicurius.repository.jdbi.mealPlanner.models.JdbiDailyMealPlanner
import epicurius.repository.jdbi.mealPlanner.models.JdbiDailyMealPlannerRow
import epicurius.repository.jdbi.mealPlanner.models.JdbiMealPlanner
import org.jdbi.v3.core.Handle
import org.jdbi.v3.core.kotlin.mapTo
import java.time.LocalDate

class JdbiMealPlannerRepository(private val handle: Handle) : MealPlannerRepository {

    override fun createDailyMealPlanner(userId: Int, date: LocalDate, maxCalories: Int?) {
        handle.createUpdate(
            """
                INSERT INTO dbo.meal_planner (user_id, date, max_calories)
                VALUES (:id, :date, :calories)
            """
        )
            .bind("id", userId)
            .bind("date", date)
            .bind("calories", maxCalories)
            .execute()
    }

    override fun getWeeklyMealPlanner(userId: Int): JdbiMealPlanner {
        val list = handle.createQuery(
            """
                SELECT mp.date, mp.max_calories, mpr.meal_time, 
                       r.id AS recipe_id, r.name AS recipe_name, 
                       r.cuisine, r.meal_type, r.preparation_time, r.servings, r.pictures_names
                FROM dbo.meal_planner mp 
                LEFT JOIN dbo.meal_planner_recipe mpr ON mp.user_id = mpr.user_id AND mp.date = mpr.date
                LEFT JOIN dbo.recipe r ON mpr.recipe_id = r.id
                WHERE mp.user_id = :id 
                AND mp.date >= DATE_TRUNC('week', CURRENT_DATE)
                AND mp.date < DATE_TRUNC('week', CURRENT_DATE) + INTERVAL '1 week'
                ORDER BY mp.date ASC, mpr.meal_time ASC
            """
        )
            .bind("id", userId)
            .mapTo<JdbiDailyMealPlannerRow>()
            .list()

        if (list.isEmpty()) return JdbiMealPlanner(emptyList())

        return JdbiMealPlanner(list.toJdbiDailyMealPlanner())
    }

    override fun getDailyMealPlanner(userId: Int, date: LocalDate): JdbiDailyMealPlanner {
        val dailyRow = dailyMealPlannerResults(userId, date)
        if (dailyRow.isEmpty()) throw DailyMealPlannerNotFound()

        return dailyRow.toJdbiDailyMealPlanner().first()
    }

    override fun addDailyMealPlanner(userId: Int, date: LocalDate, recipeId: Int, mealTime: MealTime): JdbiDailyMealPlanner {
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

        return getDailyMealPlanner(userId, date)
    }

    override fun updateDailyMealPlanner(userId: Int, date: LocalDate, recipeId: Int, mealTime: MealTime): JdbiDailyMealPlanner{
        handle.createUpdate(
            """
                DELETE FROM dbo.meal_planner_recipe 
                WHERE user_id = :userId AND date = :date AND meal_time = :mealTime;
        
                INSERT INTO dbo.meal_planner_recipe(user_id, date, recipe_id, meal_time)
                VALUES (:userId, :date, :recipeId, :mealTime);
            """
        )
            .bind("userId", userId)
            .bind("date", date)
            .bind("recipeId", recipeId)
            .bind("mealTime", mealTime.ordinal)
            .execute()

        return getDailyMealPlanner(userId, date)
    }

    override fun removeMealTimeDailyMealPlanner(userId: Int, date: LocalDate, mealTime: MealTime): JdbiDailyMealPlanner {
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

        return getDailyMealPlanner(userId, date)
    }

    override fun deleteDailyMealPlanner(userId: Int, date: LocalDate): JdbiMealPlanner {
        handle.createUpdate(
            """
                DELETE FROM dbo.meal_planner_recipe 
                WHERE user_id = :userId AND date = :date;
                
                DELETE FROM dbo.meal_planner
                WHERE user_id = :userId AND date = :date;
            """
        )
            .bind("userId", userId)
            .bind("date", date)
            .execute()

        return getWeeklyMealPlanner(userId)
    }

    override fun updateDailyCalories(userId: Int, date: LocalDate, calories: Int?): JdbiDailyMealPlanner {
        handle.createUpdate(
            """
                UPDATE dbo.meal_planner
                SET max_calories = :calories
                WHERE user_id = :userId AND date = :date
            """
        )
            .bind("calories", calories)
            .bind("userId", userId)
            .bind("date", date)
            .execute()

        return getDailyMealPlanner(userId, date)
    }

    override fun checkIfDailyMealPlannerExists(userId: Int, date: LocalDate): JdbiDailyMealPlanner? {
        val dailyRow = dailyMealPlannerResults(userId, date)
        if (dailyRow.isEmpty()) return null

        return dailyRow.toJdbiDailyMealPlanner().first()
    }

    override fun checkIfMealTimeAlreadyExistsInPlanner(userId: Int, date: LocalDate, mealTime: MealTime): Boolean =
        handle.createQuery(
            """
                SELECT COUNT(user_id)
                FROM dbo.meal_planner_recipe
                WHERE user_id = :id AND date = :date AND meal_time = :time
            """
        )
            .bind("id", userId)
            .bind("date", date)
            .bind("time", mealTime.ordinal)
            .mapTo<Int>()
            .first() == 1

    private fun dailyMealPlannerResults(userId: Int, date: LocalDate): List<JdbiDailyMealPlannerRow> =
        handle.createQuery(
            """
                SELECT mp.date, mp.max_calories, mpr.meal_time, 
                       r.id AS recipe_id, r.name AS recipe_name, 
                       r.cuisine, r.meal_type, r.preparation_time, r.servings, r.pictures_names
                FROM dbo.meal_planner mp 
                LEFT JOIN dbo.meal_planner_recipe mpr ON mp.user_id = mpr.user_id AND mp.date = mpr.date
                LEFT JOIN dbo.recipe r ON mpr.recipe_id = r.id
                WHERE mp.user_id = :userId AND mp.date = :date
                ORDER BY mpr.meal_time ASC
            """
        )
            .bind("userId", userId)
            .bind("date", date)
            .mapTo<JdbiDailyMealPlannerRow>()
            .list()

    private fun List<JdbiDailyMealPlannerRow>.toJdbiDailyMealPlanner(): List<JdbiDailyMealPlanner> =
        this.groupBy { it.date }
            .map { (date, dailyMeals) ->
                val calories = dailyMeals.first { it.date == date }.maxCalories

                if (dailyMeals.first().jdbiRecipeInfo.id == 0) {
                    JdbiDailyMealPlanner(date, calories, emptyMap())
                } else {
                    val meals = dailyMeals.associate { it.mealTime to it.jdbiRecipeInfo }
                    JdbiDailyMealPlanner(date, calories, meals)
                }
            }
}
