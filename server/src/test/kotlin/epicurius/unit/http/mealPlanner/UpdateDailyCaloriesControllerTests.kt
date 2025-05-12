package epicurius.unit.http.mealPlanner

import epicurius.domain.exceptions.DailyMealPlannerNotFound
import epicurius.http.controllers.mealPlanner.models.output.DailyMealPlannerOutputModel
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import org.mockito.kotlin.whenever
import org.springframework.http.HttpStatus
import kotlin.test.assertEquals

class UpdateDailyCaloriesControllerTests : MealPlannerControllerTest() {

    @Test
    fun `Should update user's daily meal planner calories successfully`() {
        // given an authenticated user, a date and new calories

        // mock
        whenever(
            mealPlannerServiceMock.updateDailyCalories(
                testAuthenticatedUser.user.id,
                today,
                updateDailyCaloriesInputModel.maxCalories
            )
        ).thenReturn(dailyMealPlannerToday.copy(maxCalories = updateDailyCaloriesInputModel.maxCalories))
        whenever(authenticationRefreshHandlerMock.refreshToken(testAuthenticatedUser.token)).thenReturn(mockCookie)

        // when updating the daily meal planner calories
        val response = updateDailyCalories(
            testAuthenticatedUser,
            today,
            updateDailyCaloriesInputModel,
            mockResponse
        )

        // then the daily meal planner should be updated successfully
        assertEquals(HttpStatus.OK, response.statusCode)
        assertEquals(
            DailyMealPlannerOutputModel(
                dailyMealPlannerToday.copy(maxCalories = updateDailyCaloriesInputModel.maxCalories)
            ),
            response.body
        )
    }

    @Test
    fun `Should throw DailyMealPlannerNotFound exception when daily meal planner does not exist`() {
        // given an authenticated user and a meal planner

        // mock
        whenever(
            mealPlannerServiceMock.updateDailyCalories(
                testAuthenticatedUser.user.id,
                today,
                updateDailyCaloriesInputModel.maxCalories
            )
        ).thenThrow(DailyMealPlannerNotFound())

        // when updating the daily meal planner calories
        // then DailyMealPlannerNotFound exception is thrown
        assertThrows<DailyMealPlannerNotFound> {
            updateDailyCalories(
                testAuthenticatedUser,
                today,
                updateDailyCaloriesInputModel,
                mockResponse
            )
        }
    }
}