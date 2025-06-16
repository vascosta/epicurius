package epicurius.integration.mealPlanner

import epicurius.domain.exceptions.DailyMealPlannerNotFound
import epicurius.domain.exceptions.MealTimeDoesNotExist
import epicurius.domain.exceptions.RecipeNotAccessible
import epicurius.domain.exceptions.RecipeNotFound
import epicurius.domain.mealPlanner.MealTime
import epicurius.http.media.Problem
import epicurius.http.media.Uris
import epicurius.integration.utils.getBody
import epicurius.integration.utils.patch
import org.springframework.http.HttpStatus
import java.time.LocalDate
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotNull

class UpdateDailyMealPlannerIntegrationTests : MealPlannerIntegrationTest() {

    @Test
    fun `Should update daily meal planner successfully`() {
        // given an authenticated user and a date
        val today = LocalDate.now()

        // when creating a daily meal planner
        createDailyMealPlanner(testUser.token, today, 2000)

        // and adding a recipe to the daily meal planner
        addRecipeToDailyMealPlanner(testUser.token, today, testRecipe.id, MealTime.SNACK)

        // and when updating the daily meal planner
        val updateResponse = updateDailyMealPlanner(testUser.token, today, testRecipe2.id, MealTime.SNACK)

        // then the daily meal planner should be updated successfully
        assertNotNull(updateResponse)
        assertEquals(today, updateResponse.daily.date)
        assertEquals(2000, updateResponse.daily.maxCalories)
        assertEquals(testRecipe2.id, updateResponse.daily.meals[MealTime.SNACK]?.id)
    }

    @Test
    fun `Should fail with code 404 when daily meal planner does not exist`() {
        // given an authenticated user and a date
        val today = LocalDate.now()

        // when trying to update a daily meal planner that does not exist
        val error = patch<Problem>(
            client,
            api(Uris.MealPlanner.MEAL_PLANNER.replace("{date}", today.toString())),
            body = mapOf("recipeId" to testRecipe2.id, "mealTime" to MealTime.SNACK),
            responseStatus = HttpStatus.NOT_FOUND,
            token = testUser.token
        )
        assertNotNull(error)

        // then the error should be returned
        val errorBody = getBody(error)
        assertEquals(DailyMealPlannerNotFound().message, errorBody.detail)
    }

    @Test
    fun `Should fail with code 400 exception when meal time does not exist in planner`() {
        // given an authenticated user and a meal planner
        val today = LocalDate.now()
        createDailyMealPlanner(testUser.token, today, 2000)

        // when trying to update the daily meal planner with an invalid meal time
        val error = patch<Problem>(
            client,
            api(Uris.MealPlanner.MEAL_PLANNER.replace("{date}", today.toString())),
            body = mapOf("recipeId" to testRecipe.id, "mealTime" to MealTime.LUNCH),
            responseStatus = HttpStatus.BAD_REQUEST,
            token = testUser.token
        )
        assertNotNull(error)

        // then the error should indicate that the meal time does not exist
        val errorBody = getBody(error)
        assertEquals(MealTimeDoesNotExist().message, errorBody.detail)
    }

    @Test
    fun `Should fail with code 404 when recipe does not exist`() {
        // given an authenticated user and a meal planner
        val today = LocalDate.now()
        createDailyMealPlanner(testUser.token, today, 2000)
        addRecipeToDailyMealPlanner(testUser.token, today, testRecipe.id, MealTime.SNACK)

        // when trying to update the daily meal planner with a non-existing recipe
        val error = patch<Problem>(
            client,
            api(Uris.MealPlanner.MEAL_PLANNER.replace("{date}", today.toString())),
            body = mapOf("recipeId" to 9999, "mealTime" to MealTime.SNACK),
            responseStatus = HttpStatus.NOT_FOUND,
            token = testUser.token
        )
        assertNotNull(error)

        // then the error should indicate that the recipe was not found
        val errorBody = getBody(error)
        assertEquals(RecipeNotFound().message, errorBody.detail)
    }

    @Test
    fun `Should fail with code 403 exception when recipe is not accessible`() {
        // given an authenticated user, meal planner and a private recipe
        val today = LocalDate.now()
        createDailyMealPlanner(testUser.token, today, 2000)
        addRecipeToDailyMealPlanner(testUser.token, today, testRecipe.id, MealTime.SNACK)

        // when trying to update the daily meal planner with a recipe that is not accessible
        val inaccessibleRecipeId = privateTestRecipe.id
        val error = patch<Problem>(
            client,
            api(Uris.MealPlanner.MEAL_PLANNER.replace("{date}", today.toString())),
            body = mapOf("recipeId" to inaccessibleRecipeId, "mealTime" to MealTime.SNACK),
            responseStatus = HttpStatus.FORBIDDEN,
            token = testUser.token
        )
        assertNotNull(error)

        // then the error should indicate that the recipe is not accessible
        val errorBody = getBody(error)
        assertEquals(RecipeNotAccessible().message, errorBody.detail)
    }

    @Test
    fun `Should fail with code 403 when recipe is invalid for meal time`() {
        // given an authenticated user and a meal planner
        val today = LocalDate.now()
        createDailyMealPlanner(testUser.token, today, 2000)
        addRecipeToDailyMealPlanner(testUser.token, today, testRecipe.id, MealTime.SNACK)

        // when trying to update the daily meal planner with a recipe that is invalid for the specified meal time
        val error = patch<Problem>(
            client,
            api(Uris.MealPlanner.MEAL_PLANNER.replace("{date}", today.toString())),
            body = mapOf("recipeId" to privateTestRecipe.id, "mealTime" to MealTime.SNACK),
            responseStatus = HttpStatus.FORBIDDEN,
            token = testUser.token
        )
        assertNotNull(error)

        // then the error should indicate that the recipe is invalid for the specified meal time
        val errorBody = getBody(error)
        assertEquals(RecipeNotAccessible().message, errorBody.detail)
    }
}
