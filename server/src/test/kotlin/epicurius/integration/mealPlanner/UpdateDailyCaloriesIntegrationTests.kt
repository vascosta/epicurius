package epicurius.integration.mealPlanner

import epicurius.domain.exceptions.DailyMealPlannerNotFound
import epicurius.http.media.Problem
import epicurius.http.media.Uris
import epicurius.integration.utils.getBody
import epicurius.integration.utils.patch
import org.springframework.http.HttpStatus
import java.time.LocalDate
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotNull

class UpdateDailyCaloriesIntegrationTests : MealPlannerIntegrationTest() {

    @Test
    fun `Should update user's daily meal planner calories with code 200`() {
        // given an authenticated user and a date
        val today = LocalDate.now()

        // when creating a daily meal planner for today
        createDailyMealPlanner(testUser.token, today, 2000)

        // and when updating the daily meal planner calories
        val updatedMealPlanner = updateDailyCalories(testUser.token, today, 2500)

        // then the daily meal planner should be updated successfully
        assertNotNull(updatedMealPlanner)
        assertEquals(today, updatedMealPlanner.daily.date)
        assertEquals(2500, updatedMealPlanner.daily.maxCalories)
    }

    @Test
    fun `Should fail with code 404 when daily meal planner does not exist`() {
        // given an authenticated user and a date
        val date = LocalDate.now().plusDays(1)

        // when trying to update a daily meal planner that does not exist
        val error = patch<Problem>(
            client,
            api(Uris.MealPlanner.CALORIES.replace("{date}", date.toString())),
            body = mapOf("maxCalories" to 2500),
            responseStatus = HttpStatus.NOT_FOUND,
            token = testUser.token
        )
        assertNotNull(error)

        // then the error should be returned
        val errorBody = getBody(error)
        assertEquals(DailyMealPlannerNotFound().message, errorBody.detail)
    }
}
