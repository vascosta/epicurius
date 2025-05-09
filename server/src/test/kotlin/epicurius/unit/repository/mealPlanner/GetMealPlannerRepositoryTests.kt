package epicurius.unit.repository.mealPlanner

import epicurius.domain.mealPlanner.MealTime
import epicurius.repository.jdbi.recipe.models.JdbiRecipeInfo
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import kotlin.test.assertNotNull

class GetMealPlannerRepositoryTests : MealPlannerRepositoryTest() {

    @Test
    fun `Should get user's daily meal planner successfully`() {
        // given a test user and a date
        val userId = testUser1.id

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

    @Test
    fun `Should get user's weekly meal planner successfully`() {
        // given a test user
        val userId = testUser2.id

        // when creating a new weekly meal planner
        createDailyMealPlanner(userId, today)
        createDailyMealPlanner(userId, tomorrow)

        // and getting the weekly meal planner
        val weeklyMealPlanner = getWeeklyMealPlanner(userId)

        // then the weekly meal planner should be retrieved successfully
        assertEquals(2, weeklyMealPlanner.planner.size)
        assertEquals(today, weeklyMealPlanner.planner[0].date)
        assertEquals(0, weeklyMealPlanner.planner[0].maxCalories)
        assertEquals(emptyMap<MealTime, JdbiRecipeInfo>(), weeklyMealPlanner.planner[0].meals)
        assertEquals(tomorrow, weeklyMealPlanner.planner[1].date)
        assertEquals(0, weeklyMealPlanner.planner[1].maxCalories)
        assertEquals(emptyMap<MealTime, JdbiRecipeInfo>(), weeklyMealPlanner.planner[1].meals)

        // when adding recipes to the daily meal planners
        addRecipeToMealPlanner(userId, today, testRecipe.id, MealTime.LUNCH)
        addRecipeToMealPlanner(userId, tomorrow, testRecipe.id, MealTime.DINNER)

        // and getting the weekly meal planner
        val updatedWeeklyMealPlanner = getWeeklyMealPlanner(userId)

        // then the weekly meal planner should be retrieved successfully
        assertEquals(2, updatedWeeklyMealPlanner.planner.size)
        assertEquals(today, updatedWeeklyMealPlanner.planner[0].date)
        assertEquals(0, updatedWeeklyMealPlanner.planner[0].maxCalories)
        assertEquals(1, updatedWeeklyMealPlanner.planner[0].meals.size)
        assertEquals(testRecipe.id, updatedWeeklyMealPlanner.planner[0].meals[MealTime.LUNCH]?.id)
        assertEquals(testRecipe.name, updatedWeeklyMealPlanner.planner[0].meals[MealTime.LUNCH]?.name)
        assertEquals(testRecipe.cuisine, updatedWeeklyMealPlanner.planner[0].meals[MealTime.LUNCH]?.cuisine)
        assertEquals(testRecipe.mealType, updatedWeeklyMealPlanner.planner[0].meals[MealTime.LUNCH]?.mealType)
        assertEquals(testRecipe.preparationTime, updatedWeeklyMealPlanner.planner[0].meals[MealTime.LUNCH]?.preparationTime)
        assertEquals(testRecipe.servings, updatedWeeklyMealPlanner.planner[0].meals[MealTime.LUNCH]?.servings)
        assertEquals(tomorrow, updatedWeeklyMealPlanner.planner[1].date)
        assertEquals(0, updatedWeeklyMealPlanner.planner[1].maxCalories)
        assertEquals(1, updatedWeeklyMealPlanner.planner[1].meals.size)
        assertEquals(testRecipe.id, updatedWeeklyMealPlanner.planner[1].meals[MealTime.DINNER]?.id)
        assertEquals(testRecipe.name, updatedWeeklyMealPlanner.planner[1].meals[MealTime.DINNER]?.name)
        assertEquals(testRecipe.cuisine, updatedWeeklyMealPlanner.planner[1].meals[MealTime.DINNER]?.cuisine)
        assertEquals(testRecipe.mealType, updatedWeeklyMealPlanner.planner[1].meals[MealTime.DINNER]?.mealType)
        assertEquals(testRecipe.preparationTime, updatedWeeklyMealPlanner.planner[1].meals[MealTime.DINNER]?.preparationTime)
        assertEquals(testRecipe.servings, updatedWeeklyMealPlanner.planner[1].meals[MealTime.DINNER]?.servings)

        // when creating a weekly meal planner for next week
        val nextWeekDate = today.plusWeeks(1)
        createDailyMealPlanner(userId, nextWeekDate)

        // and getting the weekly meal planner again
        val sameWeeklyPlanner = tm.run { it.mealPlannerRepository.getWeeklyMealPlanner(userId) }

        // then the updated weekly meal planner should be retrieved successfully
        assertEquals(2, sameWeeklyPlanner.planner.size)
        assertEquals(today, updatedWeeklyMealPlanner.planner[0].date)
        assertEquals(tomorrow, updatedWeeklyMealPlanner.planner[1].date)
    }
}