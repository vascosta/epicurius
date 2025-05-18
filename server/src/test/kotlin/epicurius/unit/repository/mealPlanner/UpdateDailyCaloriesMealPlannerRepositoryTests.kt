package epicurius.unit.repository.mealPlanner

import epicurius.utils.createTestUser
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import kotlin.test.assertNotNull

class UpdateDailyCaloriesMealPlannerRepositoryTests : MealPlannerRepositoryTest() {

    @Test
    fun `Should update user's daily meal planner max calories successfully`() {
        // given a test user and a date
        val userId = createTestUser(tm).user.id
        val date = today

        // when creating a new daily meal planner
        createDailyMealPlanner(userId, date)

        // and getting the daily meal planner
        val dailyMealPlanner = getDailyMealPlanner(userId, date)

        // then the daily meal planner should be created successfully with 0 max calories
        assertNotNull(dailyMealPlanner)
        assertEquals(0, dailyMealPlanner.maxCalories)

        // and updating the daily meal planner max calories
        val updatedDailyMealPlanner = updateDailyCalories(userId, date, 2000)

        // then the daily meal planner should be updated successfully
        assertEquals(2000, updatedDailyMealPlanner.maxCalories)
    }
}
