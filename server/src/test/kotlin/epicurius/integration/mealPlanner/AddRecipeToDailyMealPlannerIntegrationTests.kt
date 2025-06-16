package epicurius.integration.mealPlanner

import epicurius.domain.exceptions.DailyMealPlannerNotFound
import epicurius.domain.exceptions.MealTimeAlreadyExistsInPlanner
import epicurius.domain.exceptions.RecipeIsInvalidForMealTime
import epicurius.domain.exceptions.RecipeNotAccessible
import epicurius.domain.exceptions.RecipeNotFound
import epicurius.domain.mealPlanner.MealTime
import epicurius.http.media.Problem
import epicurius.http.media.Uris
import epicurius.integration.utils.getBody
import epicurius.integration.utils.post
import org.springframework.http.HttpStatus
import java.time.LocalDate
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotNull

class AddRecipeToDailyMealPlannerIntegrationTests : MealPlannerIntegrationTest() {

    @Test
    fun `Should add recipe to daily meal planner with code 201`() {
        // given an authenticated user
        val today = LocalDate.now()

        // when creating a daily meal planner for today
        createDailyMealPlanner(testUser.token, today, 2000)

        // and adding a recipe to the daily meal planner
        val response =
            addRecipeToDailyMealPlanner(
                testUser.token,
                today,
                testRecipe.id,
                MealTime.SNACK
            )

        // then the recipe should be added successfully to the daily meal planner
        assertNotNull(response)
        assertEquals(today, response.daily.date)
        assertEquals(2000, response.daily.maxCalories)
        assertEquals(1, response.daily.meals.size)
        assertEquals(testRecipe.id, response.daily.meals[MealTime.SNACK]?.id)
    }

    @Test
    fun `Should fail with code 404 when daily meal planner does not exist`() {
        // given an authenticated user
        val today = LocalDate.now()

        // when trying to add a recipe to a daily meal planner that does not exist
        val error = post<Problem>(
            client,
            api(Uris.MealPlanner.MEAL_PLANNER.replace("{date}", today.toString())),
            body = mapOf("date" to today.toString(), "mealTime" to MealTime.SNACK),
            responseStatus = HttpStatus.NOT_FOUND,
            token = testUser.token
        )
        assertNotNull(error)

        // then the response should indicate that the daily meal planner does not exist
        val errorBody = getBody(error)
        assertEquals(DailyMealPlannerNotFound().message, errorBody.detail)
    }

    @Test
    fun `Should fail with code 409 when meal time already exists in planner`() {
        // given an authenticated user
        val today = LocalDate.now()
        val mealTime = MealTime.SNACK

        // when creating a daily meal planner for today
        createDailyMealPlanner(testUser.token, today, 2000)

        // and adding a recipe to the daily meal planner
        addRecipeToDailyMealPlanner(testUser.token, today, testRecipe.id, mealTime)

        // and trying to add another recipe to the same meal time
        val error = post<Problem>(
            client,
            api(Uris.MealPlanner.MEAL_PLANNER.replace("{date}", today.toString())),
            body = mapOf("recipeId" to testRecipe.id, "mealTime" to mealTime),
            responseStatus = HttpStatus.CONFLICT,
            token = testUser.token
        )
        assertNotNull(error)

        // then the response should indicate that the meal time already exists in the planner
        val errorBody = getBody(error)
        assertEquals(MealTimeAlreadyExistsInPlanner(mealTime).message, errorBody.detail)
    }

    @Test
    fun `Should fail with code 404 when recipe does not exist`() {
        // given an authenticated user
        val today = LocalDate.now()

        // when creating a daily meal planner for today
        createDailyMealPlanner(testUser.token, today, 2000)

        // and when trying to add a recipe that does not exist
        val error = post<Problem>(
            client,
            api(Uris.MealPlanner.MEAL_PLANNER.replace("{date}", today.toString())),
            body = mapOf("recipeId" to 9999, "mealTime" to MealTime.SNACK),
            responseStatus = HttpStatus.NOT_FOUND,
            token = testUser.token
        )
        assertNotNull(error)

        // then the response should indicate that the recipe does not exist
        val errorBody = getBody(error)
        assertEquals(RecipeNotFound().message, errorBody.detail)
    }

    @Test
    fun `Should fail with code 403 when recipe is not accessible`() {
        // given an authenticated user
        val today = LocalDate.now()

        // when creating a daily meal planner for today
        createDailyMealPlanner(testUser.token, today, 2000)

        // and when trying to add a recipe that is not accessible
        val inaccessibleRecipeId = privateTestRecipe.id
        val error = post<Problem>(
            client,
            api(Uris.MealPlanner.MEAL_PLANNER.replace("{date}", today.toString())),
            body = mapOf("recipeId" to inaccessibleRecipeId, "mealTime" to MealTime.SNACK),
            responseStatus = HttpStatus.FORBIDDEN,
            token = testUser.token
        )
        assertNotNull(error)

        // then the response should indicate that the recipe is not accessible
        val errorBody = getBody(error)
        assertEquals(RecipeNotAccessible().message, errorBody.detail)
    }

    @Test
    fun `Should fail with code 400 when recipe is invalid for meal time`() {
        // given an authenticated user
        val today = LocalDate.now()

        // when creating a daily meal planner for today
        createDailyMealPlanner(testUser.token, today, 2000)

        // and when trying to add a recipe that is invalid for the specified meal time
        val error = post<Problem>(
            client,
            api(Uris.MealPlanner.MEAL_PLANNER.replace("{date}", today.toString())),
            body = mapOf("recipeId" to testRecipe.id, "mealTime" to MealTime.BREAKFAST),
            responseStatus = HttpStatus.BAD_REQUEST,
            token = testUser.token
        )
        assertNotNull(error)

        // then the response should indicate that the recipe is invalid for the meal time
        val errorBody = getBody(error)
        assertEquals(RecipeIsInvalidForMealTime().message, errorBody.detail)
    }
}
