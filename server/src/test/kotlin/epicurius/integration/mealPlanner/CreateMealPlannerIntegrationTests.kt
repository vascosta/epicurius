package epicurius.integration.mealPlanner

import epicurius.domain.exceptions.MealPlannerAlreadyExists
import epicurius.http.media.Problem
import epicurius.http.media.Uris
import epicurius.integration.utils.getBody
import epicurius.integration.utils.post
import org.springframework.http.HttpStatus
import java.time.LocalDate
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotNull

class CreateMealPlannerIntegrationTests : MealPlannerIntegrationTest() {

    @Test
    fun `Should create meal planner with code 201`() {
        // given an authenticated user and a date
        val today = LocalDate.now()

        // when creating a daily meal planner for today
        val response = createDailyMealPlanner(testUser.token, today, 2000)

        // then the daily meal planner should be created successfully
        assertNotNull(response)
        assertNotNull(response.daily)
        assertEquals(today, response.daily.date)
        assertEquals(0, response.daily.meals.size)
        assertEquals(2000, response.daily.maxCalories)
    }

    @Test
    fun `Should fail with code 400 when user already has a meal planner`() {
        // given an authenticated user and a date
        val today = LocalDate.now()

        // when creating a daily meal planner for today
        createDailyMealPlanner(testUser.token, today, 2000)

        // and trying to create the same daily meal planner again
        val error = post<Problem>(
            client,
            api(Uris.MealPlanner.PLANNER),
            body = mapOf("date" to today.toString(), "maxCalories" to 2000),
            responseStatus = HttpStatus.CONFLICT,
            token = testUser.token
        )
        assertNotNull(error)

        // then the request should fail with a MealPlannerAlreadyExists error
        val errorBody = getBody(error)
        assertEquals(MealPlannerAlreadyExists(today).message, errorBody.detail)
    }
}
