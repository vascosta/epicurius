package epicurius.unit.services.mealPlanner

import epicurius.domain.exceptions.MealPlannerAlreadyExists
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import org.mockito.kotlin.verify
import org.mockito.kotlin.whenever

class CreateDailyMealPlannerServiceTests : MealPlannerServiceTest() {

    @Test
    fun `Should create a daily meal planner successfully`() {
        // given a user (USER_ID) and a date (today)

        // mock
        whenever(jdbiMealPlannerRepositoryMock.checkIfDailyMealPlannerExists(USER_ID, today)).thenReturn(null)

        // when the user creates a daily meal planner
        createDailyMealPlanner(USER_ID, today, CALORIES)

        // then the daily meal planner should be created
        verify(jdbiMealPlannerRepositoryMock).createDailyMealPlanner(USER_ID, today, CALORIES)
    }

    @Test
    fun `Should throw MealPlannerAlreadyExists exception when daily meal planner already exists`() {
        // given a user (USER_ID) and a date (today)

        // mock
        whenever(
            jdbiMealPlannerRepositoryMock.checkIfDailyMealPlannerExists(USER_ID, today)
        ).thenReturn(jdbiDailyMealPlannerToday)

        // when the user tries to create a daily meal planner
        // then the daily meal planner cannot be created and throws MealPlannerAlreadyExists exception
        assertThrows<MealPlannerAlreadyExists> {
            createDailyMealPlanner(USER_ID, today, CALORIES)
        }
    }
}
