package epicurius.integration.mealPlanner

import epicurius.domain.exceptions.DailyMealPlannerNotFound
import epicurius.domain.exceptions.MealTimeDoesNotExist
import epicurius.domain.mealPlanner.MealTime
import epicurius.http.media.Problem
import epicurius.http.media.Uris
import epicurius.integration.utils.delete
import epicurius.integration.utils.getBody
import org.springframework.http.HttpStatus
import java.time.LocalDate
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotNull

class RemoveMealTimeFromDailyMealPlannerIntegrationTests : MealPlannerIntegrationTest() {

    @Test
    fun `Should remove meal time from daily meal planner with code 200`() {
        // given an authenticated user and a date
        val today = LocalDate.now()

        // when creating a daily meal planner
        createDailyMealPlanner(testUser.token, today, 2000)

        // and adding a recipe to the daily meal planner
        addRecipeToDailyMealPlanner(testUser.token, today, testRecipe.id, MealTime.SNACK)

        // when removing the meal time
        val response = removeMealTimeFromDailyMealPlanner(testUser.token, today, MealTime.SNACK)

        // then the meal time should be removed successfully
        assertNotNull(response)
        assertEquals(today, response.daily.date)
        assertEquals(2000, response.daily.maxCalories)
        assertEquals(0, response.daily.meals.size)
    }

    @Test
    fun `Should fail with code 404 when daily meal planner does not exist`() {
        // given an authenticated user and a date
        val date = LocalDate.now().plusDays(1)

        // when trying to remove a meal time from a daily meal planner that does not exist
        val error = delete<Problem>(
            client,
            api(
                Uris.MealPlanner.CLEAN_MEAL_TIME
                    .replace("{date}", date.toString())
                    .replace("{mealTime}", MealTime.BREAKFAST.toString())
            ),
            token = testUser.token,
            responseStatus = HttpStatus.NOT_FOUND
        )
        assertNotNull(error)

        // then the error should be returned
        val errorBody = getBody(error)
        assertEquals(DailyMealPlannerNotFound().message, errorBody.detail)
    }

    @Test
    fun `Should fail with code 400 when meal time does not exist`() {
        // given an authenticated user and a meal planner
        val today = LocalDate.now()
        createDailyMealPlanner(testUser.token, today, 2000)

        // when trying to remove a meal time that does not exist
        val error = delete<Problem>(
            client,
            api(
                Uris.MealPlanner.CLEAN_MEAL_TIME
                    .replace("{date}", today.toString())
                    .replace("{mealTime}", MealTime.LUNCH.toString())
            ),
            token = testUser.token,
            responseStatus = HttpStatus.BAD_REQUEST
        )
        assertNotNull(error)

        // then the error should be returned
        val errorBody = getBody(error)
        assertEquals(MealTimeDoesNotExist().message, errorBody.detail)
    }
}
