package epicurius.unit.repository.mealPlanner

import epicurius.domain.mealPlanner.MealTime
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import kotlin.test.assertNotNull

class DeleteDailyMealPlannerRepositoryTests : MealPlannerRepositoryTest() {

    @Test
    fun `Should delete user's daily meal planner successfully`() {
        // given a test user and a date
        val userId = testUser7.id
        val date = today

        // when creating a new daily meal planner
        createDailyMealPlanner(userId, date)

        // and deleting the daily meal planner
        deleteDailyMealPlanner(userId, date)

        // then the daily meal planner should be deleted successfully
        val dailyMealPlanner = checkIfDailyMealPlannerExists(userId, date)
        assertEquals(null, dailyMealPlanner)
    }

    @Test
    fun `Should delete meal time from user's daily meal planner successfully`() {
        // given a test user and a date
        val userId = testUser8.id
        val date = tomorrow

        // when creating a new daily meal planner
        createDailyMealPlanner(userId, date)

        // and adding a recipe to the daily meal planner
        addRecipeToMealPlanner(userId, date, testRecipe.id, MealTime.BREAKFAST)

        // and deleting the meal time from the daily meal planner
        removeMealTimeFromDailyMealPlanner(userId, date, MealTime.BREAKFAST)

        // then the meal time should be deleted successfully
        val updatedDailyMealPlanner = getDailyMealPlanner(userId, date)
        assertNotNull(updatedDailyMealPlanner)
        assertEquals(0, updatedDailyMealPlanner.meals.size)
    }
}