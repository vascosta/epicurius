package epicurius.unit.http.mealPlanner

import epicurius.domain.exceptions.DailyMealPlannerNotFound
import epicurius.domain.exceptions.MealTimeDoesNotExist
import epicurius.domain.mealPlanner.DailyMealPlanner
import epicurius.http.controllers.mealPlanner.models.output.DailyMealPlannerOutputModel
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import org.mockito.kotlin.whenever
import org.springframework.http.HttpStatus

class RemoveMealTimeFromDailyMealPlannerControllerTests : MealPlannerControllerTest() {

    @Test
    fun `Should remove meal time from daily meal planner successfully`() {
        // given an authenticated user and a meal planner

        // mock
        whenever(
            mealPlannerServiceMock.removeMealTimeFromDailyMealPlanner(testAuthenticatedUser.user.id, today, mealTime)
        ).thenReturn(DailyMealPlanner(today, CALORIES, emptyMap()))
        whenever(authenticationRefreshHandlerMock.refreshToken(testAuthenticatedUser.token)).thenReturn(mockCookie)

        // when removing the meal time
        val response = removeMealTimeFromDailyMealPlanner(
            testAuthenticatedUser,
            today,
            mealTime,
            mockResponse
        )

        // then the meal time should be removed successfully
        assertEquals(HttpStatus.OK, response.statusCode)
        assertEquals(DailyMealPlannerOutputModel(DailyMealPlanner(today, CALORIES, emptyMap())), response.body)
    }

    @Test
    fun `Should throw DailyMealPlannerNotFound exception when daily meal planner does not exist`() {
        // given an authenticated user and a meal planner

        // mock
        whenever(
            mealPlannerServiceMock.removeMealTimeFromDailyMealPlanner(testAuthenticatedUser.user.id, today, mealTime)
        ).thenThrow(DailyMealPlannerNotFound())

        // when removing the meal time
        // then DailyMealPlannerNotFound exception is thrown
        assertThrows<DailyMealPlannerNotFound> {
            removeMealTimeFromDailyMealPlanner(
                testAuthenticatedUser,
                today,
                mealTime,
                mockResponse
            )
        }
    }

    @Test
    fun `Should throw MealTimeDoesNotExist exception when meal time does not exist`() {
        // given an authenticated user and a meal planner

        // mock
        whenever(
            mealPlannerServiceMock.removeMealTimeFromDailyMealPlanner(testAuthenticatedUser.user.id, today, mealTime)
        ).thenThrow(MealTimeDoesNotExist())

        // when removing the meal time
        // then MealTimeDoesNotExist exception is thrown
        assertThrows<MealTimeDoesNotExist> {
            removeMealTimeFromDailyMealPlanner(
                testAuthenticatedUser,
                today,
                mealTime,
                mockResponse
            )
        }
    }
}