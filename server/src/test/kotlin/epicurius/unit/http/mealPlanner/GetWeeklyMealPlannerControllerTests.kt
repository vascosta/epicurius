package epicurius.unit.http.mealPlanner

import epicurius.domain.mealPlanner.MealPlanner
import epicurius.http.controllers.mealPlanner.models.output.MealPlannerOutputModel
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import org.mockito.kotlin.whenever
import org.springframework.http.HttpStatus

class GetWeeklyMealPlannerControllerTests : MealPlannerHttpTest() {

    @Test
    fun `Should get user's weekly meal planner successfully`() {
        // given an authenticated user

        // mock
        whenever(mealPlannerServiceMock.getWeeklyMealPlanner(testAuthenticatedUser.user.id)).thenReturn(mealPlanner)
        whenever(authenticationRefreshHandlerMock.refreshToken(testAuthenticatedUser.token)).thenReturn(mockCookie)

        // when getting the weekly meal planner
        val response = getWeeklyMealPlanner(testAuthenticatedUser, mockResponse)

        // then the weekly meal planner should be returned successfully
        assertEquals(HttpStatus.OK, response.statusCode)
        assertEquals(mealPlannerOutputModel, response.body)
    }

    @Test
    fun `Should return empty meal planner when user has no meal planner`() {
        // given an authenticated user with no meal planner

        // mock
        whenever(mealPlannerServiceMock.getWeeklyMealPlanner(testAuthenticatedUser.user.id)).thenReturn(MealPlanner(emptyList()))
        whenever(authenticationRefreshHandlerMock.refreshToken(testAuthenticatedUser.token)).thenReturn(mockCookie)

        // when getting the weekly meal planner
        val response = getWeeklyMealPlanner(testAuthenticatedUser, mockResponse)

        // then an empty meal planner should be returned successfully
        assertEquals(HttpStatus.OK, response.statusCode)
        assertEquals(MealPlannerOutputModel(emptyList()), response.body)
    }
}
