package epicurius.unit.repository.mealPlanner

import epicurius.domain.mealPlanner.MealTime
import epicurius.repository.jdbi.recipe.models.JdbiRecipeInfo
import epicurius.utils.createTestUser
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertNotNull
import kotlin.test.Test

class GetDailyMealPlannerRepositoryTests : MealPlannerRepositoryTest() {

    @Test
    fun `Should get user's daily meal planner successfully`() {
        // given a test user and a date
        val userId = createTestUser(tm).user.id

        // when creating a new daily meal planner
        createDailyMealPlanner(userId, today)

        // and getting the daily meal planner
        val dailyMealPlanner = getDailyMealPlanner(userId, today)

        // then the daily meal planner should be retrieved successfully
        assertNotNull(dailyMealPlanner)
        assertEquals(today, dailyMealPlanner.date)
        assertEquals(0, dailyMealPlanner.maxCalories)
        assertEquals(emptyMap<MealTime, JdbiRecipeInfo>(), dailyMealPlanner.meals)

        // when adding a recipe to the daily meal planner
        addRecipeToMealPlanner(userId, today, testRecipe.id, MealTime.SNACK)

        // and getting the daily meal planner
        val updatedDailyPlanner = getDailyMealPlanner(userId, today)

        // then the daily meal planner should be retrieved successfully
        assertNotNull(updatedDailyPlanner)
        assertEquals(today, updatedDailyPlanner.date)
        assertEquals(0, updatedDailyPlanner.maxCalories)
        assertEquals(jdbiRecipeInfo.id, updatedDailyPlanner.meals[MealTime.SNACK]?.id)
        assertEquals(jdbiRecipeInfo.name, updatedDailyPlanner.meals[MealTime.SNACK]?.name)
        assertEquals(jdbiRecipeInfo.cuisine, updatedDailyPlanner.meals[MealTime.SNACK]?.cuisine)
        assertEquals(jdbiRecipeInfo.mealType, updatedDailyPlanner.meals[MealTime.SNACK]?.mealType)
        assertEquals(jdbiRecipeInfo.preparationTime, updatedDailyPlanner.meals[MealTime.SNACK]?.preparationTime)
        assertEquals(jdbiRecipeInfo.servings, updatedDailyPlanner.meals[MealTime.SNACK]?.servings)
    }
}
