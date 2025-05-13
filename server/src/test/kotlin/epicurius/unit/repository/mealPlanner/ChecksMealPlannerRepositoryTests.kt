package epicurius.unit.repository.mealPlanner

import epicurius.domain.mealPlanner.MealTime
import epicurius.utils.createTestUser
import org.junit.jupiter.api.Assertions.assertNull
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test
import kotlin.test.assertNotNull

class ChecksMealPlannerRepositoryTests : MealPlannerRepositoryTest() {

    @Test
    fun `Should check if meal time already exists in planner`() {
        // given a test user and a date
        val userId = createTestUser(tm).id
        val date = tomorrow

        // when creating a new daily meal planner
        createDailyMealPlanner(userId, date)

        // and adding a recipe to the daily meal planner
        addRecipeToMealPlanner(userId, date, testRecipe.id, MealTime.BREAKFAST)

        // and checking if the meal time already exists in the planner
        val mealTimeExists = checkIfMealTimeAlreadyExistsInPlanner(userId, date, MealTime.BREAKFAST)

        // then the meal time should exist in the planner
        assertTrue(mealTimeExists)
    }

    @Test
    fun `Should check if meal time does not exist in planner`() {
        // given a test user and a date
        val userId = createTestUser(tm).id
        val date = tomorrow

        // when creating a new daily meal planner
        createDailyMealPlanner(userId, date)

        // and checking if the meal time does not exist in the planner
        val mealTimeExists = checkIfMealTimeAlreadyExistsInPlanner(userId, date, MealTime.LUNCH)

        // then the meal time should not exist in the planner
        assertTrue(!mealTimeExists)
    }

    @Test
    fun `Should check if daily meal planner exists`() {
        // given a test user and a date
        val userId = createTestUser(tm).id
        val date = tomorrow

        // when creating a new daily meal planner
        createDailyMealPlanner(userId, date)

        // and checking if the daily meal planner exists
        val dailyMealPlannerExists = checkIfDailyMealPlannerExists(userId, date)

        // then the daily meal planner should exist
        assertNotNull(dailyMealPlannerExists)
    }

    @Test
    fun `Should check if daily meal planner does not exist`() {
        // given a test user and a date
        val userId = createTestUser(tm).id
        val date = tomorrow

        // when checking if the daily meal planner does not exist
        val dailyMealPlannerExists = checkIfDailyMealPlannerExists(userId, date)

        // then the daily meal planner should not exist
        assertNull(dailyMealPlannerExists)
    }
}
