package epicurius.repository.jdbi.mealPlanner

import epicurius.repository.jdbi.mealPlanner.models.JdbiDailyMealPlanner
import epicurius.repository.jdbi.mealPlanner.models.JdbiMealPlanner
import org.jdbi.v3.core.Handle
import org.jdbi.v3.core.kotlin.mapTo
import java.util.Date

class JdbiMealPlannerRepository(private val handle: Handle) : MealPlannerRepository {

    override fun createMealPlanner(userId: Int, date: Date) {
        handle.createUpdate(
            """
                INSERT INTO dbo.meal_planner (user_id, date)
                VALUES (:userId, :date)
            """
        )
            .bind("userId", userId)
            .bind("date", date)
            .execute()
    }

    override fun getMealPlanner(userId: Int): JdbiMealPlanner {
        val list = handle.createQuery(
            """
                SELECT mp.user_id, mp.date, mpr.meal_time, mpr.id, 
                       r.name, r.cuisine, r.meal_type, r.preparation_time, r.servings
                FROM dbo.meal_planner mp 
                JOIN dbo.meal_planner_recipe mpr ON mp.user_id = mpr.user_id and AND mp.date = mpr.date
                JOIN dbo.recipe r ON mpr.recipe_id = r.recipe
                WHERE mp.user_id = :id
            """
        )
            .bind("id", userId)
            .mapTo<JdbiDailyMealPlanner>()
            .list()

        return JdbiMealPlanner(list)
    }
}
