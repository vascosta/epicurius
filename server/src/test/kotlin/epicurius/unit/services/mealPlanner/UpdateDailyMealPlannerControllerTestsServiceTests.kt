package epicurius.unit.services.mealPlanner

import epicurius.domain.exceptions.DailyMealPlannerNotFound
import epicurius.domain.exceptions.MealTimeDoesNotExist
import epicurius.domain.exceptions.RecipeIsInvalidForMealTime
import epicurius.domain.exceptions.RecipeNotAccessible
import epicurius.domain.exceptions.RecipeNotFound
import epicurius.domain.picture.PictureDomain.Companion.RECIPES_FOLDER
import epicurius.domain.recipe.MealType
import epicurius.http.controllers.mealPlanner.models.input.UpdateMealPlannerInputModel
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import org.mockito.kotlin.whenever
import kotlin.test.assertEquals

class UpdateDailyMealPlannerControllerTestsServiceTests : MealPlannerServiceTest() {

    @Test
    fun `Should update the daily meal planner successfully`() {
        // given a user (USER_ID) and a date (today)
        // and a recipe (jdbiRecipeInfo)

        // mock
        whenever(
            jdbiMealPlannerRepositoryMock.checkIfDailyMealPlannerExists(USER_ID, today)
        ).thenReturn(jdbiDailyMealPlannerToday)
        whenever(
            jdbiMealPlannerRepositoryMock.checkIfMealTimeAlreadyExistsInPlanner(USER_ID, today, mealTime)
        ).thenReturn(true)
        whenever(jdbiRecipeRepositoryMock.getRecipeById(RECIPE_ID)).thenReturn(jdbiRecipeModel)
        whenever(jdbiUserRepositoryMock.checkUserVisibility(AUTHOR_USERNAME, USERNAME)).thenReturn(true)
        whenever(mealTimeMock.isMealTypeAllowedForMealTime(jdbiRecipeModel.mealType)).thenReturn(true)
        whenever(
            jdbiMealPlannerRepositoryMock.updateDailyMealPlanner(USER_ID, today, RECIPE_ID, mealTime)
        ).thenReturn(jdbiDailyMealPlannerToday)
        whenever(pictureRepositoryMock.getPicture(testPicture.name, RECIPES_FOLDER)).thenReturn(testPicture.bytes)

        // when the user updates the daily meal planner
        val mealPlanner = updateDailyMealPlanner(USER_ID, USERNAME, today, UpdateMealPlannerInputModel(RECIPE_ID, mealTime))

        // then the daily meal planner should be updated
        assertEquals(today, mealPlanner.date)
        assertEquals(CALORIES, mealPlanner.maxCalories)
        assertEquals(1, mealPlanner.meals.size)
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
        // and a recipe (jdbiRecipeInfo)

        // mock
        whenever(
            jdbiMealPlannerRepositoryMock.checkIfDailyMealPlannerExists(USER_ID, today)
        ).thenReturn(null)

        // when the user tries to update the daily meal planner
        val exception = assertThrows<DailyMealPlannerNotFound> {
            updateDailyMealPlanner(USER_ID, USERNAME, today, UpdateMealPlannerInputModel(RECIPE_ID, mealTime))
        }

        // then the exception should be thrown
        assertEquals(DailyMealPlannerNotFound().message, exception.message)
    }

    @Test
    fun `Should throw MealTimeDoesNotExist exception when meal time does not exist in the daily meal planner`() {
        // given a user (USER_ID) and a date (today)
        // and a recipe (jdbiRecipeInfo)

        // mock
        whenever(
            jdbiMealPlannerRepositoryMock.checkIfDailyMealPlannerExists(USER_ID, today)
        ).thenReturn(jdbiDailyMealPlannerToday)
        whenever(
            jdbiMealPlannerRepositoryMock.checkIfMealTimeAlreadyExistsInPlanner(USER_ID, today, mealTime)
        ).thenReturn(false)

        // when the user tries to update the daily meal planner
        val exception = assertThrows<MealTimeDoesNotExist> {
            updateDailyMealPlanner(USER_ID, USERNAME, today, UpdateMealPlannerInputModel(RECIPE_ID, mealTime))
        }

        // then the exception should be thrown
        assertEquals(MealTimeDoesNotExist().message, exception.message)
    }

    @Test
    fun `Should throw RecipeNotFound exception when recipe does not exist`() {
        // given a user (USER_ID) and a date (today)
        // and a recipe (jdbiRecipeInfo)

        // mock
        whenever(
            jdbiMealPlannerRepositoryMock.checkIfDailyMealPlannerExists(USER_ID, today)
        ).thenReturn(jdbiDailyMealPlannerToday)
        whenever(
            jdbiMealPlannerRepositoryMock.checkIfMealTimeAlreadyExistsInPlanner(USER_ID, today, mealTime)
        ).thenReturn(true)
        whenever(jdbiRecipeRepositoryMock.getRecipeById(RECIPE_ID)).thenReturn(null)

        // when the user tries to update the daily meal planner
        val exception = assertThrows<RecipeNotFound> {
            updateDailyMealPlanner(USER_ID, USERNAME, today, UpdateMealPlannerInputModel(RECIPE_ID, mealTime))
        }

        // then the exception should be thrown
        assertEquals(RecipeNotFound().message, exception.message)
    }

    @Test
    fun `Should throw RecipeNotAccessible exception when recipe is not accessible`() {
        // given a user (USER_ID) and a date (today)
        // and a recipe (jdbiRecipeInfo)

        // mock
        whenever(
            jdbiMealPlannerRepositoryMock.checkIfDailyMealPlannerExists(USER_ID, today)
        ).thenReturn(jdbiDailyMealPlannerToday)
        whenever(
            jdbiMealPlannerRepositoryMock.checkIfMealTimeAlreadyExistsInPlanner(USER_ID, today, mealTime)
        ).thenReturn(true)
        whenever(jdbiRecipeRepositoryMock.getRecipeById(RECIPE_ID)).thenReturn(jdbiRecipeModel)
        whenever(jdbiUserRepositoryMock.checkUserVisibility(AUTHOR_USERNAME, USERNAME)).thenReturn(false)

        // when the user tries to update the daily meal planner
        val exception = assertThrows<RecipeNotAccessible> {
            updateDailyMealPlanner(USER_ID, USERNAME, today, UpdateMealPlannerInputModel(RECIPE_ID, mealTime))
        }

        // then the exception should be thrown
        assertEquals(RecipeNotAccessible().message, exception.message)
    }

    @Test
    fun `Should throw RecipeIsInvalidForMealTime exception when recipe is not valid for meal time`() {
        // given a user (USER_ID) and a date (today)
        // and a recipe (jdbiRecipeInfo)
        val mealType = MealType.SNACK

        // mock
        whenever(
            jdbiMealPlannerRepositoryMock.checkIfDailyMealPlannerExists(USER_ID, today)
        ).thenReturn(jdbiDailyMealPlannerToday)
        whenever(
            jdbiMealPlannerRepositoryMock.checkIfMealTimeAlreadyExistsInPlanner(USER_ID, today, mealTime)
        ).thenReturn(true)
        whenever(jdbiRecipeRepositoryMock.getRecipeById(RECIPE_ID)).thenReturn(jdbiRecipeModel.copy(mealType = mealType))
        whenever(jdbiUserRepositoryMock.checkUserVisibility(AUTHOR_USERNAME, USERNAME)).thenReturn(true)
        whenever(mealTimeMock.isMealTypeAllowedForMealTime(mealType)).thenReturn(false)

        // when the user tries to update the daily meal planner
        val exception = assertThrows<RecipeIsInvalidForMealTime> {
            updateDailyMealPlanner(USER_ID, USERNAME, today, UpdateMealPlannerInputModel(RECIPE_ID, mealTime))
        }

        // then the exception should be thrown
        assertEquals(RecipeIsInvalidForMealTime().message, exception.message)
    }
}
