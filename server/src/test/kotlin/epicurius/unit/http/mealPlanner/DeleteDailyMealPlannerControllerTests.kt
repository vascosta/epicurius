package epicurius.unit.http.mealPlanner

import epicurius.domain.exceptions.DailyMealPlannerNotFound
import epicurius.domain.mealPlanner.MealPlanner
import epicurius.http.controllers.mealPlanner.models.output.MealPlannerOutputModel
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import org.mockito.kotlin.whenever
import org.springframework.http.HttpStatus

class DeleteDailyMealPlannerControllerTests : MealPlannerHttpTest() {

    @Test
    fun `Should delete user's daily meal planner successfully`() {
        // given an authenticated user and a date

        // mock
        whenever(
            mealPlannerServiceMock.deleteDailyMealPlanner(testAuthenticatedUser.user.id, today)
        ).thenReturn(MealPlanner(listOf(dailyMealPlannerTomorrow)))

        // when deleting the daily meal planner
        val response = deleteDailyMealPlanner(testAuthenticatedUser, today)

        // then the daily meal planner should be deleted successfully
        assertEquals(HttpStatus.OK, response.statusCode)
        assertEquals(MealPlannerOutputModel(listOf(dailyMealPlannerTomorrow)), response.body)
    }

    @Test
    fun `Should throw DailyMealPlannerNotFound exception when daily meal planner does not exist`() {
        // given an authenticated user and a meal planner

        // mock
        whenever(
            mealPlannerServiceMock.deleteDailyMealPlanner(testAuthenticatedUser.user.id, today)
        ).thenThrow(DailyMealPlannerNotFound())

        // when deleting the daily meal planner
        // then DailyMealPlannerNotFound exception is thrown
        assertThrows<DailyMealPlannerNotFound> {
            deleteDailyMealPlanner(testAuthenticatedUser, today)
        }
    }
}
