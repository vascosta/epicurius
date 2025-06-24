package epicurius.unit.services.mealPlanner

import epicurius.domain.exceptions.DailyMealPlannerNotFound
import epicurius.domain.picture.PictureDomain.Companion.RECIPES_FOLDER
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import org.mockito.kotlin.whenever

class UpdateDailyCaloriesServiceTests : MealPlannerServiceTest() {

    @Test
    fun `Should update daily meal planner calories successfully`() {
        // given a user (USER_ID) and a date (today)
        // and a recipe (jdbiRecipeInfo)

        // mock
        whenever(
            jdbiMealPlannerRepositoryMock.checkIfDailyMealPlannerExists(USER_ID, today)
        ).thenReturn(jdbiDailyMealPlannerToday.copy(maxCalories = 0))
        whenever(
            jdbiMealPlannerRepositoryMock.updateDailyCalories(USER_ID, today, CALORIES)
        ).thenReturn(jdbiDailyMealPlannerToday)
        whenever(pictureRepositoryMock.getPicture(testPicture.name, RECIPES_FOLDER)).thenReturn(testPicture.bytes)

        // when the user updates the daily meal planner calories
        val mealPlanner = updateDailyCalories(USER_ID, today, CALORIES)

        // then the daily meal planner calories should be updated
        assertEquals(today, mealPlanner.date)
        assertEquals(CALORIES, mealPlanner.maxCalories)
    }

    @Test
    fun `Should throw DailyMealPlannerNotFound exception when daily meal planner does not exist`() {
        // given a user (USER_ID) and a date (today)
        // and a recipe (jdbiRecipeInfo)

        // mock
        whenever(
            jdbiMealPlannerRepositoryMock.checkIfDailyMealPlannerExists(USER_ID, today)
        ).thenReturn(null)

        // when the user updates the daily meal planner calories
        // then the calories cannot be updated and throws DailyMealPlannerNotFound exception
        assertThrows<DailyMealPlannerNotFound> {
            updateDailyCalories(USER_ID, today, CALORIES)
        }
    }
}
