package epicurius.integration.mealPlanner

import epicurius.domain.exceptions.DailyMealPlannerNotFound
import epicurius.http.media.Problem
import epicurius.http.media.Uris
import epicurius.integration.utils.delete
import epicurius.integration.utils.getBody
import org.springframework.http.HttpStatus
import java.time.LocalDate
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotNull

class DeleteDailyMealPlannerIntegrationTests : MealPlannerIntegrationTest() {

    @Test
    fun `Should delete user's daily meal planner with code 200`() {
        // given an authenticated user
        val today = LocalDate.now()

        // when creating a daily meal planner for today
        createDailyMealPlanner(testUser.token, today, 2000)

        // and when deleting the daily meal planner
        val response = deleteDailyMealPlanner(testUser.token, today)

        // then the daily meal planner should be deleted successfully
        assertNotNull(response)
        assertEquals(0, response.planner.size)
    }

    @Test
    fun `Should fail with code 404 when daily meal planner does not exist`() {
        // given an authenticated user and a date
        val date = LocalDate.now()

        // when trying to delete a daily meal planner that does not exist
        val error = delete<Problem>(
            client,
            api(Uris.MealPlanner.MEAL_PLANNER.replace("{date}", date.toString())),
            responseStatus = HttpStatus.NOT_FOUND,
            token = testUser.token
        )
        assertNotNull(error)

        // then the error should be returned
        val errorBody = getBody(error)
        assertEquals(DailyMealPlannerNotFound().message, errorBody.detail)
    }
}
