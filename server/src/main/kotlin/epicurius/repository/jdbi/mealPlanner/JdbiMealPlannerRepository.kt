package epicurius.repository.jdbi.mealPlanner

import org.jdbi.v3.core.Handle
import java.util.*

class JdbiMealPlannerRepository(private val handle: Handle): MealPlannerRepository {

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
}