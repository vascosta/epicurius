package epicurius.unit.http.mealPlanner

import epicurius.domain.exceptions.DailyMealPlannerNotFound
import epicurius.http.controllers.mealPlanner.models.output.DailyMealPlannerOutputModel
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import org.mockito.kotlin.whenever
import org.springframework.http.HttpStatus

class GetDailyMealPlannerControllerTests : MealPlannerHttpTest() {

    @Test
    fun `Should get user's daily meal planner successfully`() {
        // given an authenticated user and a dat

        // mock
        whenever(
            mealPlannerServiceMock.getDailyMealPlanner(testAuthenticatedUser.user.id, today)
        ).thenReturn(dailyMealPlannerToday)

        // when getting the daily meal planner
        val response = getDailyMealPlanner(testAuthenticatedUser, today)

        // then the daily meal planner should be returned successfully
        assertEquals(HttpStatus.OK, response.statusCode)
        assertEquals(DailyMealPlannerOutputModel(dailyMealPlannerToday), response.body)
    }

    @Test
    fun `Should throw MealPlannerNotFound exception when user has no meal planner`() {
        // given an authenticated user with no meal planner

        // mock
        whenever(
            mealPlannerServiceMock.getDailyMealPlanner(testAuthenticatedUser.user.id, today)
        ).thenThrow(DailyMealPlannerNotFound())

        // when getting the daily meal planner
        // then MealPlannerNotFound exception is thrown
        assertThrows<DailyMealPlannerNotFound> { getDailyMealPlanner(testAuthenticatedUser, today) }
    }
}
