package epicurius.unit.http.mealPlanner

import epicurius.domain.exceptions.DailyMealPlannerNotFound
import epicurius.domain.exceptions.MealTimeDoesNotExist
import epicurius.domain.exceptions.RecipeIsInvalidForMealTime
import epicurius.domain.exceptions.RecipeNotAccessible
import epicurius.domain.exceptions.RecipeNotFound
import epicurius.domain.mealPlanner.MealTime
import epicurius.http.controllers.mealPlanner.models.output.DailyMealPlannerOutputModel
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import org.mockito.kotlin.whenever
import org.springframework.http.HttpStatus
import kotlin.test.assertEquals

class UpdateDailyMealPlannerControllerTests : MealPlannerHttpTest() {

    @Test
    fun `Should update daily meal planner successfully`() {
        // given an authenticated user and a meal planner
        val mealTime = MealTime.DINNER
        val expectedMealPlanner = dailyMealPlannerToday.copy(meals = mapOf(mealTime to recipeInfo1))

        // mock
        whenever(
            mealPlannerServiceMock.updateDailyMealPlanner(
                testAuthenticatedUser.user.id,
                today,
                updateDailyMealPlannerInputModel
            )
        ).thenReturn(expectedMealPlanner)

        // when updating the daily meal planner
        val response = updateDailyMealPlanner(
            testAuthenticatedUser,
            today,
            updateDailyMealPlannerInputModel
        )

        // then the daily meal planner should be updated successfully
        assertEquals(HttpStatus.OK, response.statusCode)
        assertEquals(DailyMealPlannerOutputModel(expectedMealPlanner), response.body)
    }

    @Test
    fun `Should throw DailyMealPlannerNotFound exception when daily meal planner does not exist`() {
        // given an authenticated user and a meal planner

        // mock
        whenever(
            mealPlannerServiceMock.updateDailyMealPlanner(
                testAuthenticatedUser.user.id,
                today,
                updateDailyMealPlannerInputModel
            )
        ).thenThrow(DailyMealPlannerNotFound())

        // when updating the daily meal planner
        // then the daily meal planner cannot be updated and throws DailyMealPlannerNotFound exception
        assertThrows<DailyMealPlannerNotFound> {
            updateDailyMealPlanner(
                testAuthenticatedUser,
                today,
                updateDailyMealPlannerInputModel
            )
        }
    }

    @Test
    fun `Should throw MealTimeDoesNotExist exception when meal time does not exist in planner`() {
        // given an authenticated user and a meal planner

        // mock
        whenever(
            mealPlannerServiceMock.updateDailyMealPlanner(
                testAuthenticatedUser.user.id,
                today,
                updateDailyMealPlannerInputModel
            )
        ).thenThrow(MealTimeDoesNotExist())

        // when updating the daily meal planner
        // then the daily meal planner cannot be updated and throws MealTimeDoesNotExist exception
        assertThrows<MealTimeDoesNotExist> {
            updateDailyMealPlanner(
                testAuthenticatedUser,
                today,
                updateDailyMealPlannerInputModel
            )
        }
    }

    @Test
    fun `Should throw RecipeNotFound exception when recipe does not exist`() {
        // given an authenticated user and a meal planner

        // mock
        whenever(
            mealPlannerServiceMock.updateDailyMealPlanner(
                testAuthenticatedUser.user.id,
                today,
                updateDailyMealPlannerInputModel
            )
        ).thenThrow(RecipeNotFound())

        // when updating the daily meal planner
        // then the daily meal planner cannot be updated and throws RecipeNotFound exception
        assertThrows<RecipeNotFound> {
            updateDailyMealPlanner(
                testAuthenticatedUser,
                today,
                updateDailyMealPlannerInputModel
            )
        }
    }

    @Test
    fun `Should throw RecipeNotAccessible exception when recipe is not accessible`() {
        // given an authenticated user and a meal planner

        // mock
        whenever(
            mealPlannerServiceMock.updateDailyMealPlanner(
                testAuthenticatedUser.user.id,
                today,
                updateDailyMealPlannerInputModel
            )
        ).thenThrow(RecipeNotAccessible())

        // when updating the daily meal planner
        // then the daily meal planner cannot be updated and throws RecipeNotAccessible exception
        assertThrows<RecipeNotAccessible> {
            updateDailyMealPlanner(
                testAuthenticatedUser,
                today,
                updateDailyMealPlannerInputModel
            )
        }
    }

    @Test
    fun `Should throw RecipeIsInvalidForMealTime exception when recipe is invalid for meal time`() {
        // given an authenticated user and a meal planner

        // mock
        whenever(
            mealPlannerServiceMock.updateDailyMealPlanner(
                testAuthenticatedUser.user.id,
                today,
                updateDailyMealPlannerInputModel
            )
        ).thenThrow(RecipeIsInvalidForMealTime())

        // when updating the daily meal planner
        // then the daily meal planner cannot be updated and throws RecipeIsInvalidForMealTime exception
        assertThrows<RecipeIsInvalidForMealTime> {
            updateDailyMealPlanner(
                testAuthenticatedUser,
                today,
                updateDailyMealPlannerInputModel
            )
        }
    }
}
