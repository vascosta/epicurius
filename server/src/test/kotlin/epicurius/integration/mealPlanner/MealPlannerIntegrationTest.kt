package epicurius.integration.mealPlanner

import epicurius.domain.mealPlanner.MealTime
import epicurius.domain.recipe.Recipe
import epicurius.domain.user.AuthenticatedUser
import epicurius.http.controllers.mealPlanner.models.output.DailyMealPlannerOutputModel
import epicurius.http.controllers.mealPlanner.models.output.GetWeeklyMealPlannerOutputModel
import epicurius.http.media.Uris
import epicurius.integration.EpicuriusIntegrationTest
import epicurius.integration.utils.delete
import epicurius.integration.utils.get
import epicurius.integration.utils.getBody
import epicurius.integration.utils.patch
import epicurius.integration.utils.post
import epicurius.utils.createTestRecipe
import epicurius.utils.createTestUser
import org.junit.jupiter.api.BeforeEach
import org.springframework.http.HttpStatus
import java.time.LocalDate

class MealPlannerIntegrationTest : EpicuriusIntegrationTest() {

    lateinit var testUser: AuthenticatedUser
    lateinit var privateTestUser: AuthenticatedUser
    lateinit var authorUser: AuthenticatedUser
    lateinit var testRecipe: Recipe
    lateinit var testRecipe2: Recipe
    lateinit var privateTestRecipe: Recipe

    @BeforeEach
    fun setup() {
        testUser = createTestUser(tm)
        privateTestUser = createTestUser(tm, true)
        authorUser = createTestUser(tm)
        testRecipe = createTestRecipe(tm, authorUser.user)
        testRecipe2 = createTestRecipe(tm, authorUser.user)
        privateTestRecipe = createTestRecipe(tm, privateTestUser.user)
    }

    fun getWeeklyMealPlanner(token: String) =
        get<GetWeeklyMealPlannerOutputModel>(
            client,
            api(Uris.MealPlanner.PLANNER),
            token = token
        )

    fun getDailyMealPlanner(token: String, date: LocalDate) =
        get<DailyMealPlannerOutputModel>(
            client,
            api(Uris.MealPlanner.MEAL_PLANNER.replace("{date}", date.toString())),
            token = token
        )

    fun createDailyMealPlanner(
        token: String,
        date: LocalDate,
        maxCalories: Int
    ): DailyMealPlannerOutputModel? {
        val result = post<DailyMealPlannerOutputModel>(
            client,
            api(Uris.MealPlanner.PLANNER),
            body = mapOf("date" to date.toString(), "maxCalories" to maxCalories),
            responseStatus = HttpStatus.CREATED,
            token = token
        )
        return getBody(result)
    }

    fun addRecipeToDailyMealPlanner(
        token: String,
        date: LocalDate,
        recipeId: Int,
        mealTime: MealTime
    ): DailyMealPlannerOutputModel? {
        val result = post<DailyMealPlannerOutputModel>(
            client,
            api(Uris.MealPlanner.MEAL_PLANNER.replace("{date}", date.toString())),
            body = mapOf("recipeId" to recipeId, "mealTime" to mealTime),
            responseStatus = HttpStatus.CREATED,
            token = token
        )
        return getBody(result)
    }

    fun updateDailyMealPlanner(
        token: String,
        date: LocalDate,
        recipeId: Int,
        mealTime: MealTime
    ): DailyMealPlannerOutputModel? {
        val result = patch<DailyMealPlannerOutputModel>(
            client,
            api(Uris.MealPlanner.MEAL_PLANNER.replace("{date}", date.toString())),
            body = mapOf("recipeId" to recipeId, "mealTime" to mealTime),
            responseStatus = HttpStatus.OK,
            token = token
        )
        return getBody(result)
    }

    fun updateDailyCalories(
        token: String,
        date: LocalDate,
        calories: Int
    ): DailyMealPlannerOutputModel? {
        val result = patch<DailyMealPlannerOutputModel>(
            client,
            api(Uris.MealPlanner.CALORIES.replace("{date}", date.toString())),
            body = mapOf("maxCalories" to calories),
            responseStatus = HttpStatus.OK,
            token = token
        )
        return getBody(result)
    }

    fun removeMealTimeFromDailyMealPlanner(
        token: String,
        date: LocalDate,
        mealTime: MealTime
    ): DailyMealPlannerOutputModel? {
        val result = delete<DailyMealPlannerOutputModel>(
            client,
            api(
                Uris.MealPlanner.CLEAN_MEAL_TIME
                    .replace("{date}", date.toString())
                    .replace("{mealTime}", mealTime.toString())
            ),
            responseStatus = HttpStatus.OK,
            token = token
        )
        return getBody(result)
    }

    fun deleteDailyMealPlanner(
        token: String,
        date: LocalDate
    ): GetWeeklyMealPlannerOutputModel? {
        val result = delete<GetWeeklyMealPlannerOutputModel>(
            client,
            api(Uris.MealPlanner.MEAL_PLANNER.replace("{date}", date.toString())),
            responseStatus = HttpStatus.OK,
            token = token
        )
        return getBody(result)
    }
}
