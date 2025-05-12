package epicurius.unit.services.mealPlanner

import epicurius.domain.exceptions.DailyMealPlannerNotFound
import epicurius.domain.picture.PictureDomain.Companion.RECIPES_FOLDER
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import org.mockito.kotlin.whenever

class GetDailyMealPlannerServiceTests : MealPlannerServiceTest() {

    @Test
    fun `Should get the daily meal planner successfully`() {
        // given a user (USER_ID) and a date (today)

        // mock
        whenever(jdbiMealPlannerRepositoryMock.checkIfDailyMealPlannerExists(USER_ID, today)).thenReturn(jdbiDailyMealPlannerToday)
        whenever(pictureRepositoryMock.getPicture(testPicture.name, RECIPES_FOLDER)).thenReturn(testPicture.bytes)

        // when the user gets the daily meal planner
        val mealPlanner = getDailyMealPlanner(USER_ID, today)

        // then the daily meal planner should be returned
        assertEquals(today, mealPlanner.date)
        assertEquals(CALORIES, mealPlanner.maxCalories)
        assertEquals(mealTime, mealPlanner.meals.keys.first())
        assertEquals(jdbiRecipeInfo.name, mealPlanner.meals[mealTime]?.name)
        assertEquals(jdbiRecipeInfo.servings, mealPlanner.meals[mealTime]?.servings)
        assertEquals(jdbiRecipeInfo.preparationTime, mealPlanner.meals[mealTime]?.preparationTime)
        assertEquals(jdbiRecipeInfo.cuisine, mealPlanner.meals[mealTime]?.cuisine)
        assertEquals(jdbiRecipeInfo.mealType, mealPlanner.meals[mealTime]?.mealType)
    }

    @Test
    fun `Should throw DailyMealPlannerNotFound exception when daily meal planner does not exist`() {
        // given a user (USER_ID) and a date (today)

        // mock
        whenever(jdbiMealPlannerRepositoryMock.checkIfDailyMealPlannerExists(USER_ID, today)).thenReturn(null)

        // when the user tries to get the daily meal planner
        val exception = assertThrows<DailyMealPlannerNotFound> {
            getDailyMealPlanner(USER_ID, today)
        }

        // then the exception should be thrown
        assertEquals(DailyMealPlannerNotFound().message, exception.message)
    }
}
