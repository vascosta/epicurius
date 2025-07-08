package android.epicurius.ui.screens.mealPlanner.daily

import android.content.Context
import android.epicurius.domain.exceptions.DailyMealPlannerNotFound
import android.epicurius.domain.mealPlanner.DailyMealPlanner
import android.epicurius.domain.mealPlanner.MealTime
import android.epicurius.services.EpicuriusService
import android.epicurius.services.api.mealPlanner.models.input.CreateMealPlannerInputModel
import android.epicurius.services.api.mealPlanner.models.input.UpdateDailyCaloriesInputModel
import android.epicurius.storage.Session
import android.epicurius.ui.EpicuriusViewModel
import android.epicurius.ui.screens.utils.LoadState
import android.epicurius.ui.screens.utils.apiSuccess
import android.epicurius.ui.screens.utils.idle
import android.epicurius.ui.screens.utils.loading
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.time.LocalDate

class DailyMealPlannerViewModel(
    service: EpicuriusService,
    session: Session,
    context: Context
): EpicuriusViewModel(service, session, context) {

    private val dailyMealPlannerFlow = MutableStateFlow<LoadState<DailyMealPlanner>>(idle())

    val dailyMealPlanner = dailyMealPlannerFlow.asStateFlow()

    fun getDailyMealPlanner(date: LocalDate) {
        disableButtons()
        dailyMealPlannerFlow.value = loading()
        viewModelScope.launch { fetchDailyMealPlanner(date) }
    }

    fun updateDailyMealPlannerCalories(calories: Int) {
        disableButtons()
        val updateDailyMealPlannerCaloriesInfo = UpdateDailyCaloriesInputModel(calories)
        viewModelScope.launch { handleUpdateDailyMealPlannerCalories(LocalDate.now(), updateDailyMealPlannerCaloriesInfo) }
    }

    fun deleteRecipeFromDailyMealPlanner(date: LocalDate, mealTime: MealTime) {
        disableButtons()
        viewModelScope.launch { handleDeleteRecipeFromDailyMealPlanner(date, mealTime) }
    }

    private suspend fun fetchDailyMealPlanner(date: LocalDate) {
        val result = request(showError = false) {
            val token = session.getToken()
            service.mealPlannerService.getDailyMealPlanner(token, date)
        }
        when {
            result.isFailure -> {
                val problem = result.getProblemOrThrow()
                if (problem.detail == DailyMealPlannerNotFound().message) {
                    val createDailyMealPlannerInfo = CreateMealPlannerInputModel(date)
                    handleCreateDailyMealPlanner(createDailyMealPlannerInfo)
                }
            }
            result.isSuccess -> {
                val fetchedDailyMenu = result.getValueOrThrow().daily
                dailyMealPlannerFlow.value = apiSuccess(fetchedDailyMenu)
            }
        }
        enableButtons()
    }

    private suspend fun handleCreateDailyMealPlanner(createDailyMealPlannerInfo: CreateMealPlannerInputModel) {
        val result = request {
            val token = session.getToken()
            service.mealPlannerService.createDailyMealPlanner(token, createDailyMealPlannerInfo)
        }
        when {
            result.isSuccess -> {
                val fetchedDailyMenu = result.getValueOrThrow().daily
                dailyMealPlannerFlow.value = apiSuccess(fetchedDailyMenu)
            }
        }
        enableButtons()
    }

    private suspend fun handleUpdateDailyMealPlannerCalories(
        date: LocalDate,
        updateDailyMealPlannerCalories: UpdateDailyCaloriesInputModel
    ) {
        val result = request {
            val token = session.getToken()
            service.mealPlannerService.updateDailyCalories(token, date, updateDailyMealPlannerCalories)
        }
        when {
            result.isSuccess -> {
                val fetchedDailyMenu = result.getValueOrThrow().daily
                dailyMealPlannerFlow.value = apiSuccess(fetchedDailyMenu)
            }
        }
        enableButtons()
    }

    private suspend fun handleDeleteRecipeFromDailyMealPlanner(date: LocalDate, mealTime: MealTime) {
        val result = request {
            val token = session.getToken()
            service.mealPlannerService.removeMealTimeFromDailyMealPlanner(token, date, mealTime)
        }
        when {
            result.isSuccess -> {
                val fetchedDailyMenu = result.getValueOrThrow().daily
                dailyMealPlannerFlow.value = apiSuccess(fetchedDailyMenu)
            }
        }
        enableButtons()
    }
}