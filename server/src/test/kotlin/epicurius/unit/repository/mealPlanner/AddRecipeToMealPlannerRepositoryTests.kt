package epicurius.unit.repository.mealPlanner

import epicurius.domain.mealPlanner.MealTime
import epicurius.utils.createTestUser
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test

class AddRecipeToMealPlannerRepositoryTests : MealPlannerRepositoryTest() {

    @Test
    fun `Should add a recipe to user's daily meal planner successfully`() {
        // given a test user, a date, and a recipe
        val userId = createTestUser(tm).user.id
        val date = today

        // when creating a new daily meal planner
        createDailyMealPlanner(userId, date)

        // when adding the recipe to the daily meal planner
        val mealPlanner = addRecipeToMealPlanner(userId, date, testRecipe.id, MealTime.DINNER)

        // then the recipe should be added successfully
        assertEquals(date, mealPlanner.date)
        assertEquals(0, mealPlanner.maxCalories)
        assertEquals(testRecipe.id, mealPlanner.meals[MealTime.DINNER]?.id)
        assertEquals(testRecipe.name, mealPlanner.meals[MealTime.DINNER]?.name)
        assertEquals(testRecipe.cuisine, mealPlanner.meals[MealTime.DINNER]?.cuisine)
        assertEquals(testRecipe.mealType, mealPlanner.meals[MealTime.DINNER]?.mealType)
        assertEquals(testRecipe.preparationTime, mealPlanner.meals[MealTime.DINNER]?.preparationTime)
        assertEquals(testRecipe.servings, mealPlanner.meals[MealTime.DINNER]?.servings)
    }
}
