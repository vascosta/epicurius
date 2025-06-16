package epicurius.integration.mealPlanner

import epicurius.domain.exceptions.DailyMealPlannerNotFound
import epicurius.domain.mealPlanner.MealTime
import epicurius.http.media.Problem
import epicurius.http.media.Uris
import epicurius.integration.utils.get
import org.springframework.http.HttpStatus
import java.time.LocalDate
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotNull

class GetDailyMealPlannerIntegrationTests : MealPlannerIntegrationTest() {

    @Test
    fun `Should get user's empty daily meal planner with code 200`() {
        // given an authenticated user and a date
        val today = LocalDate.now()

        // when creating a daily meal planner for today
        createDailyMealPlanner(testUser.token, today, 2000)

        // and getting the daily meal planner
        val response = getDailyMealPlanner(testUser.token, today)

        // then the daily meal planner should be returned successfully
        assertNotNull(response)
        assertNotNull(response.daily)
        assertEquals(today, response.daily.date)
        assertEquals(0, response.daily.meals.size)
        assertEquals(2000, response.daily.maxCalories)
    }

    @Test
    fun `Should get user's daily meal planner with meals with code 200`() {
        // given an authenticated user and a date
        val today = LocalDate.now()

        // when creating a daily meal planner for today with meals
        createDailyMealPlanner(testUser.token, today, 2000)

        // and adding a recipe to the daily meal planner
        addRecipeToDailyMealPlanner(testUser.token, today, testRecipe.id, MealTime.SNACK)

        // and getting the daily meal planner
        val response = getDailyMealPlanner(testUser.token, today)

        // then the daily meal planner should be returned successfully with meals
        assertNotNull(response)
        assertNotNull(response.daily)
        assertEquals(today, response.daily.date)
        assertEquals(1, response.daily.meals.size)
        assertEquals(2000, response.daily.maxCalories)
        val meal = response.daily.meals[MealTime.SNACK]
        assertNotNull(meal)
        assertEquals(testRecipe.id, meal.id)
        assertEquals(testRecipe.name, meal.name)
        assertEquals(testRecipe.authorUsername, meal.authorUsername)
        assertEquals(testRecipe.rating, meal.rating)
        assertEquals(testRecipe.cuisine, meal.cuisine)
        assertEquals(testRecipe.mealType, meal.mealType)
        assertEquals(testRecipe.preparationTime, meal.preparationTime)
        assertEquals(testRecipe.servings, meal.servings)
    }

    @Test
    fun `Should fail with code 400 when user has no meal planner`() {
        // given an authenticated user with no meal planner
        val today = LocalDate.now()

        // when trying to get the daily meal planner
        val error = get<Problem>(
            client,
            api(Uris.MealPlanner.MEAL_PLANNER.replace("{date}", today.toString())),
            token = testUser.token,
            responseStatus = HttpStatus.NOT_FOUND
        )
        assertNotNull(error)

        // then the daily meal planner cannot be retrieved and fails with code 400
        assertEquals(DailyMealPlannerNotFound().message, error.detail)
    }
}
