package epicurius.unit.services.mealPlanner

import epicurius.domain.exceptions.DailyMealPlannerNotFound
import epicurius.domain.exceptions.MealTimeDoesNotExist
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import org.mockito.kotlin.whenever

class RemoveMealTimeDailyMealPlannerServiceTests : MealPlannerServiceTest() {

    @Test
    fun `Should remove a meal time from the daily meal planner successfully`() {
        // given a user (USER_ID) and a date (today)
        // and a recipe (jdbiRecipeInfo)

        // mock
        whenever(
            jdbiMealPlannerRepositoryMock.checkIfDailyMealPlannerExists(USER_ID, today)
        ).thenReturn(jdbiDailyMealPlannerToday)
        whenever(
            jdbiMealPlannerRepositoryMock.checkIfMealTimeAlreadyExistsInPlanner(USER_ID, today, mealTime)
        ).thenReturn(true)
        whenever(
            jdbiMealPlannerRepositoryMock.removeMealTimeDailyMealPlanner(USER_ID, today, mealTime)
        ).thenReturn(jdbiDailyMealPlannerToday.copy(meals = emptyMap()))

        // when the user removes a meal time from the daily meal planner
        val mealPlanner = removeMealTimeDailyMealPlanner(USER_ID, today, mealTime)

        // then the meal time should be removed from the daily meal planner
        assertEquals(today, mealPlanner.date)
        assertEquals(CALORIES, mealPlanner.maxCalories)
        assertEquals(0, mealPlanner.meals.size)
    }

    @Test
    fun `Should throw DailyMealPlannerNotFound exception when daily meal planner does not exist`() {
        // given a user (USER_ID) and a date (today)
        // and a recipe (jdbiRecipeInfo)

        // mock
        whenever(
            jdbiMealPlannerRepositoryMock.checkIfDailyMealPlannerExists(USER_ID, today)
        ).thenReturn(null)

        // when the user tries to remove a meal time from the daily meal planner
        val exception = assertThrows<DailyMealPlannerNotFound> {
            removeMealTimeDailyMealPlanner(USER_ID, today, mealTime)
        }

        // then the exception should be thrown
        assertEquals(DailyMealPlannerNotFound().message, exception.message)
    }

    @Test
    fun `Should throw MealTimeDoesNotExist exception when meal time does not exist in the daily meal planner`() {
        // given a user (USER_ID) and a date (today)
        // and a recipe (jdbiRecipeInfo)

        // mock
        whenever(
            jdbiMealPlannerRepositoryMock.checkIfDailyMealPlannerExists(USER_ID, today)
        ).thenReturn(jdbiDailyMealPlannerToday)
        whenever(
            jdbiMealPlannerRepositoryMock.checkIfMealTimeAlreadyExistsInPlanner(USER_ID, today, mealTime)
        ).thenReturn(false)

        // when the user tries to remove a meal time from the daily meal planner
        val exception = assertThrows<MealTimeDoesNotExist> {
            removeMealTimeDailyMealPlanner(USER_ID, today, mealTime)
        }

        // then the exception should be thrown
        assertEquals(MealTimeDoesNotExist().message, exception.message)
    }
}
