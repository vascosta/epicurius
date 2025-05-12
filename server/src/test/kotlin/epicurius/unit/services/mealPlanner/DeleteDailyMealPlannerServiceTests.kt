package epicurius.unit.services.mealPlanner

import epicurius.domain.exceptions.DailyMealPlannerNotFound
import epicurius.domain.picture.PictureDomain.Companion.RECIPES_FOLDER
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import org.mockito.kotlin.whenever

class DeleteDailyMealPlannerServiceTests : MealPlannerServiceTest() {

    @Test
    fun `Should delete the daily meal planner successfully`() {
        // given a user (USER_ID) and a date (today)
        // and a recipe (jdbiRecipeInfo)

        // mock
        whenever(
            jdbiMealPlannerRepositoryMock.checkIfDailyMealPlannerExists(USER_ID, today)
        ).thenReturn(jdbiDailyMealPlannerToday)
        whenever(
            jdbiMealPlannerRepositoryMock.deleteDailyMealPlanner(USER_ID, today)
        ).thenReturn(jdbiMealPlanner)
        whenever(pictureRepositoryMock.getPicture(testPicture.name, RECIPES_FOLDER)).thenReturn(testPicture.bytes)

        // when the user deletes the daily meal planner
        val result = deleteDailyMealPlanner(USER_ID, today)

        // then the daily meal planner should be deleted
        assertEquals(1, result.planner.size)
        assertEquals(tomorrow, result.planner[0].date)
    }

    @Test
    fun `Should throw DailyMealPlannerNotFound exception when daily meal planner does not exist`() {
        // given a user (USER_ID) and a date (today)
        // and a recipe (jdbiRecipeInfo)

        // mock
        whenever(
            jdbiMealPlannerRepositoryMock.checkIfDailyMealPlannerExists(USER_ID, today)
        ).thenReturn(null)

        // when the user tries to delete the daily meal planner
        val exception = assertThrows<DailyMealPlannerNotFound> {
            deleteDailyMealPlanner(USER_ID, today)
        }

        // then the exception should be thrown
        assertEquals(DailyMealPlannerNotFound().message, exception.message)
    }
}
