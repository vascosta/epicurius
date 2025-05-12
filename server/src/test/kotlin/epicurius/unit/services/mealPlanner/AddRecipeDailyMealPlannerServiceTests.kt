package epicurius.unit.services.mealPlanner

import epicurius.domain.exceptions.DailyMealPlannerNotFound
import epicurius.domain.exceptions.MealTimeAlreadyExistsInPlanner
import epicurius.domain.exceptions.RecipeIsInvalidForMealTime
import epicurius.domain.exceptions.RecipeNotAccessible
import epicurius.domain.exceptions.RecipeNotFound
import epicurius.domain.picture.PictureDomain.Companion.RECIPES_FOLDER
import epicurius.domain.recipe.MealType
import epicurius.http.controllers.mealPlanner.models.input.AddMealPlannerInputModel
import org.junit.jupiter.api.assertThrows
import org.mockito.kotlin.whenever
import kotlin.test.Test
import kotlin.test.assertEquals

class AddRecipeDailyMealPlannerServiceTests : MealPlannerServiceTest() {

    @Test
    fun `Should add a recipe to the daily meal planner successfully`() {
        // given a user (USER_ID) and a date (today)
        // and a recipe (jdbiRecipeInfo)

        // mock
        whenever(
            jdbiMealPlannerRepositoryMock.checkIfDailyMealPlannerExists(USER_ID, today)
        ).thenReturn(jdbiDailyMealPlannerToday)
        whenever(
            jdbiMealPlannerRepositoryMock.checkIfMealTimeAlreadyExistsInPlanner(USER_ID, today, mealTime)
        ).thenReturn(false)
        whenever(jdbiRecipeRepositoryMock.getRecipeById(RECIPE_ID)).thenReturn(jdbiRecipeModel)
        whenever(jdbiUserRepositoryMock.checkUserVisibility(AUTHOR_USERNAME, USERNAME)).thenReturn(true)
        whenever(mealTimeMock.isMealTypeAllowedForMealTime(jdbiRecipeModel.mealType)).thenReturn(true)
        whenever(
            jdbiMealPlannerRepositoryMock.addRecipeToDailyMealPlanner(USER_ID, today, RECIPE_ID, mealTime)
        ).thenReturn(jdbiDailyMealPlannerToday)
        whenever(pictureRepositoryMock.getPicture(testPicture.name, RECIPES_FOLDER)).thenReturn(testPicture.bytes)

        // when the user adds a recipe to the daily meal planner
        val mealPlanner = addRecipeDailyMealPlanner(USER_ID, USERNAME, today, AddMealPlannerInputModel(RECIPE_ID, mealTime))

        // then the recipe should be added to the daily meal planner
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

        // when the user tries to add a recipe to the daily meal planner
        val exception = assertThrows<DailyMealPlannerNotFound> {
            addRecipeDailyMealPlanner(USER_ID, USERNAME, today, AddMealPlannerInputModel(RECIPE_ID, mealTime))
        }

        // then the exception should be thrown
        assertEquals(DailyMealPlannerNotFound().message, exception.message)
    }

    @Test
    fun `Should throw MealTimeAlreadyExistsInPlanner exception when meal time already exists in the daily meal planner`() {
        // given a user (USER_ID) and a date (today)
        // and a recipe (jdbiRecipeInfo)

        // mock
        whenever(
            jdbiMealPlannerRepositoryMock.checkIfDailyMealPlannerExists(USER_ID, today)
        ).thenReturn(jdbiDailyMealPlannerToday)
        whenever(
            jdbiMealPlannerRepositoryMock.checkIfMealTimeAlreadyExistsInPlanner(USER_ID, today, mealTime)
        ).thenReturn(true)

        // when the user tries to add a recipe to the daily meal planner
        val exception = assertThrows<MealTimeAlreadyExistsInPlanner> {
            addRecipeDailyMealPlanner(USER_ID, USERNAME, today, AddMealPlannerInputModel(RECIPE_ID, mealTime))
        }

        // then the exception should be thrown
        assertEquals(MealTimeAlreadyExistsInPlanner(mealTime).message, exception.message)
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
        ).thenReturn(false)
        whenever(jdbiRecipeRepositoryMock.getRecipeById(RECIPE_ID)).thenReturn(null)

        // when the user tries to add a recipe to the daily meal planner
        val exception = assertThrows<RecipeNotFound> {
            addRecipeDailyMealPlanner(USER_ID, USERNAME, today, AddMealPlannerInputModel(RECIPE_ID, mealTime))
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
        ).thenReturn(false)
        whenever(jdbiRecipeRepositoryMock.getRecipeById(RECIPE_ID)).thenReturn(jdbiRecipeModel)
        whenever(jdbiUserRepositoryMock.checkUserVisibility(AUTHOR_USERNAME, USERNAME)).thenReturn(false)

        // when the user tries to add a recipe to the daily meal planner
        val exception = assertThrows<RecipeNotAccessible> {
            addRecipeDailyMealPlanner(USER_ID, USERNAME, today, AddMealPlannerInputModel(RECIPE_ID, mealTime))
        }

        // then the exception should be thrown
        assertEquals(RecipeNotAccessible().message, exception.message)
    }

    @Test
    fun `Should throw RecipeIsInvalidForMealTime exception when recipe is invalid for meal time`() {
        // given a user (USER_ID) and a date (today)
        // and a recipe (jdbiRecipeInfo)
        val mealType = MealType.SNACK

        // mock
        whenever(
            jdbiMealPlannerRepositoryMock.checkIfDailyMealPlannerExists(USER_ID, today)
        ).thenReturn(jdbiDailyMealPlannerToday)
        whenever(
            jdbiMealPlannerRepositoryMock.checkIfMealTimeAlreadyExistsInPlanner(USER_ID, today, mealTime)
        ).thenReturn(false)
        whenever(jdbiRecipeRepositoryMock.getRecipeById(RECIPE_ID)).thenReturn(jdbiRecipeModel.copy(mealType = mealType))
        whenever(jdbiUserRepositoryMock.checkUserVisibility(AUTHOR_USERNAME, USERNAME)).thenReturn(true)
        whenever(mealTimeMock.isMealTypeAllowedForMealTime(mealType)).thenReturn(false)

        // when the user tries to add a recipe to the daily meal planner
        val exception = assertThrows<RecipeIsInvalidForMealTime> {
            addRecipeDailyMealPlanner(USER_ID, USERNAME, today, AddMealPlannerInputModel(RECIPE_ID, mealTime))
        }

        // then the exception should be thrown
        assertEquals(RecipeIsInvalidForMealTime().message, exception.message)
    }
}
