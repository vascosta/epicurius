package epicurius.unit.repository.mealPlanner

import org.junit.jupiter.api.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotNull

class CreateDailyMealPlannerRepositoryTests : MealPlannerRepositoryTest() {

    @Test
    fun `Should create a new daily meal planner successfully without calories`() {
        // given a test user and a date

        // when creating a new daily meal planner
        createDailyMealPlanner(testUser4.id, today)

        // then the daily meal planner should be created successfully
        val dailyMealPlanner = getDailyMealPlanner(testUser4.id, today)
        assertNotNull(dailyMealPlanner)
        assertEquals(today, dailyMealPlanner.date)
        assertEquals(0, dailyMealPlanner.maxCalories)
        assertEquals(emptyMap(), dailyMealPlanner.meals)
    }

    @Test
    fun `Should create a new daily meal planner successfully with calories`() {
        // given a test user, a date and a maximum calorie limit
        val maxCalories = 2000

        // when creating a new daily meal planner
        createDailyMealPlanner(testUser4.id, tomorrow, maxCalories)

        // then the daily meal planner should be created successfully
        val dailyMealPlanner = getDailyMealPlanner(testUser4.id, tomorrow)
        assertNotNull(dailyMealPlanner)
        assertEquals(tomorrow, dailyMealPlanner.date)
        assertEquals(maxCalories, dailyMealPlanner.maxCalories)
        assertEquals(emptyMap(), dailyMealPlanner.meals)
    }
}
