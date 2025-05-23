package epicurius.unit.http.mealPlanner

import epicurius.domain.exceptions.MealPlannerAlreadyExists
import epicurius.http.controllers.mealPlanner.models.input.CreateMealPlannerInputModel
import epicurius.http.controllers.mealPlanner.models.output.DailyMealPlannerOutputModel
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import org.mockito.kotlin.whenever
import org.springframework.http.HttpStatus
import java.time.LocalDate

class CreateMealPlannerControllerTests : MealPlannerControllerTest() {

    @Test
    fun `Should create meal planner successfully`() {
        // given an authenticated user and a meal planner
        val today = LocalDate.now()

        // mock
        whenever(
            mealPlannerServiceMock.createDailyMealPlanner(testAuthenticatedUser.user.id, today, CALORIES)
        ).thenReturn(dailyMealPlannerToday.copy(meals = emptyMap()))

        // when creating the meal planner
        val response = createDailyMealPlanner(
            testAuthenticatedUser,
            CreateMealPlannerInputModel(today, CALORIES)
        )

        // then the meal planner should be created successfully
        assertEquals(HttpStatus.CREATED, response.statusCode)
        assertEquals(DailyMealPlannerOutputModel(dailyMealPlannerToday.copy(meals = emptyMap())), response.body)
    }

    @Test
    fun `Should throw MealPlannerAlreadyExists exception when user already has a meal planner`() {
        // given an authenticated user with a meal planner
        val today = LocalDate.now()

        // mock
        whenever(
            mealPlannerServiceMock.createDailyMealPlanner(testAuthenticatedUser.user.id, today, CALORIES)
        ).thenThrow(MealPlannerAlreadyExists(today))

        // when creating the meal planner
        // then the daily meal planner cannot be created and throws MealPlannerAlreadyExists exception
        assertThrows<MealPlannerAlreadyExists> {
            createDailyMealPlanner(
                testAuthenticatedUser,
                CreateMealPlannerInputModel(today, CALORIES)
            )
        }
    }
}
