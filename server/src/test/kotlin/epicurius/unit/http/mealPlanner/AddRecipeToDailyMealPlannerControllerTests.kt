package epicurius.unit.http.mealPlanner

import epicurius.domain.exceptions.DailyMealPlannerNotFound
import epicurius.domain.exceptions.MealTimeAlreadyExistsInPlanner
import epicurius.domain.exceptions.RecipeIsInvalidForMealTime
import epicurius.domain.exceptions.RecipeNotAccessible
import epicurius.domain.exceptions.RecipeNotFound
import epicurius.http.controllers.mealPlanner.models.output.DailyMealPlannerOutputModel
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import org.mockito.kotlin.whenever
import org.springframework.http.HttpStatus

class AddRecipeToDailyMealPlannerControllerTests : MealPlannerHttpTest() {

    @Test
    fun `Should add recipe to daily meal planner successfully`() {
        // given an authenticated user and a meal planner

        // mock
        whenever(
            mealPlannerServiceMock.addRecipeToDailyMealPlanner(
                testAuthenticatedUser.user.id,
                testAuthenticatedUser.user.name,
                today,
                addRecipeToDailyMealPlannerInputModel
            )
        ).thenReturn(dailyMealPlannerToday)

        // when adding the recipe to the daily meal planner
        val response = addRecipeToDailyMealPlanner(
            testAuthenticatedUser,
            today,
            addRecipeToDailyMealPlannerInputModel
        )

        // then the recipe should be added successfully
        assertEquals(HttpStatus.CREATED, response.statusCode)
        assertEquals(DailyMealPlannerOutputModel(dailyMealPlannerToday), response.body)
    }

    @Test
    fun `Should throw DailyMealPlannerNotFound exception when daily meal planner does not exist`() {
        // given an authenticated user and a meal planner

        // mock
        whenever(
            mealPlannerServiceMock.addRecipeToDailyMealPlanner(
                testAuthenticatedUser.user.id,
                testAuthenticatedUser.user.name,
                today,
                addRecipeToDailyMealPlannerInputModel
            )
        ).thenThrow(DailyMealPlannerNotFound())

        // when adding the recipe to the daily meal planner
        // then DailyMealPlannerNotFound exception is thrown
        assertThrows<DailyMealPlannerNotFound> {
            addRecipeToDailyMealPlanner(
                testAuthenticatedUser,
                today,
                addRecipeToDailyMealPlannerInputModel
            )
        }
    }

    @Test
    fun `Should throw MealTimeAlreadyExistsInPlanner exception when meal time already exists in planner`() {
        // given an authenticated user and a meal planner

        // mock
        whenever(
            mealPlannerServiceMock.addRecipeToDailyMealPlanner(
                testAuthenticatedUser.user.id,
                testAuthenticatedUser.user.name,
                today,
                addRecipeToDailyMealPlannerInputModel
            )
        ).thenThrow(MealTimeAlreadyExistsInPlanner(addRecipeToDailyMealPlannerInputModel.mealTime))

        // when adding the recipe to the daily meal planner
        // then MealTimeAlreadyExistsInPlanner exception is thrown
        assertThrows<MealTimeAlreadyExistsInPlanner> {
            addRecipeToDailyMealPlanner(
                testAuthenticatedUser,
                today,
                addRecipeToDailyMealPlannerInputModel
            )
        }
    }

    @Test
    fun `Should throw RecipeNotFound exception when recipe does not exist`() {
        // given an authenticated user and a meal planner

        // mock
        whenever(
            mealPlannerServiceMock.addRecipeToDailyMealPlanner(
                testAuthenticatedUser.user.id,
                testAuthenticatedUser.user.name,
                today,
                addRecipeToDailyMealPlannerInputModel
            )
        ).thenThrow(RecipeNotFound())

        // when adding the recipe to the daily meal planner
        // then DailyMealPlannerNotFound exception is thrown
        assertThrows<RecipeNotFound> {
            addRecipeToDailyMealPlanner(
                testAuthenticatedUser,
                today,
                addRecipeToDailyMealPlannerInputModel
            )
        }
    }

    @Test
    fun `Should throw RecipeNotAccessible exception when recipe is not accessible`() {
        // given an authenticated user and a meal planner

        // mock
        whenever(
            mealPlannerServiceMock.addRecipeToDailyMealPlanner(
                testAuthenticatedUser.user.id,
                testAuthenticatedUser.user.name,
                today,
                addRecipeToDailyMealPlannerInputModel
            )
        ).thenThrow(RecipeNotAccessible())

        // when adding the recipe to the daily meal planner
        // then DailyMealPlannerNotFound exception is thrown
        assertThrows<RecipeNotAccessible> {
            addRecipeToDailyMealPlanner(
                testAuthenticatedUser,
                today,
                addRecipeToDailyMealPlannerInputModel
            )
        }
    }

    @Test
    fun `Should throw RecipeIsInvalidForMealTime exception when recipe is invalid for meal time`() {
        // given an authenticated user and a meal planner

        // mock
        whenever(
            mealPlannerServiceMock.addRecipeToDailyMealPlanner(
                testAuthenticatedUser.user.id,
                testAuthenticatedUser.user.name,
                today,
                addRecipeToDailyMealPlannerInputModel
            )
        ).thenThrow(RecipeIsInvalidForMealTime())

        // when adding the recipe to the daily meal planner
        // then DailyMealPlannerNotFound exception is thrown
        assertThrows<RecipeIsInvalidForMealTime> {
            addRecipeToDailyMealPlanner(
                testAuthenticatedUser,
                today,
                addRecipeToDailyMealPlannerInputModel
            )
        }
    }
}
