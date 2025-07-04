package android.epicurius.services.api.mealPlanner

import android.epicurius.domain.mealPlanner.MealTime
import android.epicurius.services.api.mealPlanner.models.input.AddMealPlannerInputModel
import android.epicurius.services.api.mealPlanner.models.input.CreateMealPlannerInputModel
import android.epicurius.services.api.mealPlanner.models.input.UpdateDailyCaloriesInputModel
import android.epicurius.services.api.mealPlanner.models.input.UpdateMealPlannerInputModel
import android.epicurius.services.api.mealPlanner.models.output.DailyMealPlannerOutputModel
import android.epicurius.services.http.HttpService
import android.epicurius.services.api.mealPlanner.models.output.GetWeeklyMealPlannerOutputModel
import android.epicurius.services.http.utils.APIResult
import android.epicurius.services.http.utils.Uris
import java.time.LocalDate

class MealPlannerService(private val httpService: HttpService) {

    suspend fun getWeeklyMealPlanner(
        token: String
    ): APIResult<GetWeeklyMealPlannerOutputModel> =
        httpService.get<GetWeeklyMealPlannerOutputModel>(
            Uris.MealPlanner.PLANNER,
            token = token
        )

    suspend fun getDailyMealPlanner(
        token: String,
        date: LocalDate
    ): APIResult<DailyMealPlannerOutputModel> =
        httpService.get<DailyMealPlannerOutputModel>(
            Uris.MealPlanner.MEAL_PLANNER,
            pathParams = mapOf("date" to date),
            token = token
        )

    suspend fun createDailyMealPlanner(
        token: String,
        dailyMealPlannerInfo: CreateMealPlannerInputModel
    ): APIResult<DailyMealPlannerOutputModel> =
        httpService.post<DailyMealPlannerOutputModel>(
            Uris.MealPlanner.PLANNER,
            dailyMealPlannerInfo,
            token = token
        )

    suspend fun addRecipeToDailyMealPlanner(
        token: String,
        date: LocalDate,
        dailyMealPlannerInfo: AddMealPlannerInputModel
    ): APIResult<DailyMealPlannerOutputModel> =
        httpService.post<DailyMealPlannerOutputModel>(
            Uris.MealPlanner.MEAL_PLANNER,
            dailyMealPlannerInfo,
            pathParams = mapOf("date" to date),
            token = token
        )

    suspend fun updateDailyMealPlanner(
        token: String,
        date: LocalDate,
        dailyMealPlannerInfo: UpdateMealPlannerInputModel
    ): APIResult<DailyMealPlannerOutputModel> =
        httpService.patch<DailyMealPlannerOutputModel>(
            Uris.MealPlanner.MEAL_PLANNER,
            dailyMealPlannerInfo,
            pathParams = mapOf("date" to date),
            token = token
        )

    suspend fun updateDailyCalories(
        token: String,
        date: LocalDate,
        dailyMealPlannerInfo: UpdateDailyCaloriesInputModel
    ): APIResult<DailyMealPlannerOutputModel> =
        httpService.patch<DailyMealPlannerOutputModel>(
            Uris.MealPlanner.CALORIES,
            dailyMealPlannerInfo,
            pathParams = mapOf("date" to date),
            token = token
        )

    suspend fun removeMealTimeFromDailyMealPlanner(
        token: String,
        date: LocalDate,
        mealTime: MealTime
    ): APIResult<DailyMealPlannerOutputModel> =
        httpService.delete<DailyMealPlannerOutputModel>(
            Uris.MealPlanner.CLEAN_MEAL_TIME,
            pathParams = mapOf("date" to date, "mealTime" to mealTime),
            token = token
        )

    suspend fun deleteDailyMealPlanner(
        token: String,
        date: LocalDate
    ): APIResult<GetWeeklyMealPlannerOutputModel> =
        httpService.delete<GetWeeklyMealPlannerOutputModel>(
            Uris.MealPlanner.MEAL_PLANNER,
            pathParams = mapOf("date" to date),
            token = token
        )
}