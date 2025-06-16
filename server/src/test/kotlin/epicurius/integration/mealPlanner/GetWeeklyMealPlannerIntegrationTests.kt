package epicurius.integration.mealPlanner

import epicurius.domain.mealPlanner.MealTime
import org.junit.jupiter.api.Assertions.assertEquals
import java.time.LocalDate
import kotlin.test.Test
import kotlin.test.assertNotNull
import kotlin.test.assertTrue

class GetWeeklyMealPlannerIntegrationTests : MealPlannerIntegrationTest() {

    @Test
    fun `Should retrieve empty user's weekly meal planner with code 200`() {
        // given an authenticated user

        // when getting the user's weekly meal planner
        val mealPlannerBody = getWeeklyMealPlanner(testUser.token)

        // then the weekly meal planner should be empty
        assertNotNull(mealPlannerBody)
        assertTrue(mealPlannerBody.planner.isEmpty())
    }

    @Test
    fun `Should retrieve user's weekly meal planner with code 200`() {
        // given an authenticated user and a date
        val today = LocalDate.now()

        // when creating a daily meal planner
        createDailyMealPlanner(testUser.token, today, 2000)

        // and adding a recipe to the daily meal planner
        addRecipeToDailyMealPlanner(testUser.token, today, testRecipe.id, MealTime.SNACK)

        // when getting the user's weekly meal planner
        val mealPlannerBody = getWeeklyMealPlanner(testUser.token)

        // then the weekly meal planner should have two days with one meal each
        assertNotNull(mealPlannerBody)
        assertTrue(mealPlannerBody.planner.isNotEmpty())
        assertEquals(1, mealPlannerBody.planner.size)
        assertEquals(today, mealPlannerBody.planner[0].date)
        assertEquals(1, mealPlannerBody.planner[0].meals.size)
        assertEquals(testRecipe.id, mealPlannerBody.planner[0].meals[MealTime.SNACK]?.id)
    }
}
