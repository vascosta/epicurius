package epicurius.unit.repository.mealPlanner

import epicurius.domain.mealPlanner.MealTime
import epicurius.utils.createTestUser
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test

class UpdateDailyMealPlannerControllerTestsRepositoryTests : MealPlannerRepositoryTest() {

    @Test
    fun `Should update user's daily meal planner recipe successfully`() {
        // given a test user and a date
        val userId = createTestUser(tm).id
        val date = today

        // when creating a new daily meal planner
        createDailyMealPlanner(userId, date)

        // and adding a recipe to the daily meal planner
        addRecipeToMealPlanner(userId, date, testRecipe.id, MealTime.BREAKFAST)

        // and updating the daily meal planner
        val updatedDailyMealPlanner = updateDailyMealPlanner(userId, date, testRecipe2.id, MealTime.BREAKFAST)

        // then the daily meal planner should be updated successfully
        assertEquals(date, updatedDailyMealPlanner.date)
        assertEquals(0, updatedDailyMealPlanner.maxCalories)
        assertEquals(testRecipe2.id, updatedDailyMealPlanner.meals[MealTime.BREAKFAST]?.id)
        assertEquals(testRecipe2.name, updatedDailyMealPlanner.meals[MealTime.BREAKFAST]?.name)
        assertEquals(testRecipe2.cuisine, updatedDailyMealPlanner.meals[MealTime.BREAKFAST]?.cuisine)
        assertEquals(testRecipe2.mealType, updatedDailyMealPlanner.meals[MealTime.BREAKFAST]?.mealType)
        assertEquals(testRecipe2.preparationTime, updatedDailyMealPlanner.meals[MealTime.BREAKFAST]?.preparationTime)
        assertEquals(testRecipe2.servings, updatedDailyMealPlanner.meals[MealTime.BREAKFAST]?.servings)
    }

    @Test
    fun `Should update user's daily meal planner meal time successfully`() {
        // given a test user and a date
        val userId = createTestUser(tm).id
        val date = tomorrow

        // when creating a new daily meal planner
        createDailyMealPlanner(userId, date)

        // and adding a recipe to the daily meal planner
        addRecipeToMealPlanner(userId, date, testRecipe.id, MealTime.LUNCH)

        // and updating the daily meal planner
        val updatedDailyMealPlanner = updateDailyMealPlanner(userId, date, testRecipe.id, MealTime.DINNER)

        // then the daily meal planner should be updated successfully
        assertEquals(date, updatedDailyMealPlanner.date)
        assertEquals(0, updatedDailyMealPlanner.maxCalories)
        assertEquals(testRecipe.id, updatedDailyMealPlanner.meals[MealTime.DINNER]?.id)
        assertEquals(testRecipe.name, updatedDailyMealPlanner.meals[MealTime.DINNER]?.name)
        assertEquals(testRecipe.cuisine, updatedDailyMealPlanner.meals[MealTime.DINNER]?.cuisine)
        assertEquals(testRecipe.mealType, updatedDailyMealPlanner.meals[MealTime.DINNER]?.mealType)
        assertEquals(testRecipe.preparationTime, updatedDailyMealPlanner.meals[MealTime.DINNER]?.preparationTime)
        assertEquals(testRecipe.servings, updatedDailyMealPlanner.meals[MealTime.DINNER]?.servings)
    }
}
