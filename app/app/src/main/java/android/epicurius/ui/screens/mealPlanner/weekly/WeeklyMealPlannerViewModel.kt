package android.epicurius.ui.screens.mealPlanner.weekly

import android.content.Context
import android.epicurius.domain.mealPlanner.DailyMealPlanner
import android.epicurius.domain.mealPlanner.utils.getWeek
import android.epicurius.services.EpicuriusService
import android.epicurius.services.api.mealPlanner.models.input.CreateMealPlannerInputModel
import android.epicurius.storage.Session
import android.epicurius.ui.EpicuriusViewModel
import android.epicurius.ui.screens.utils.LoadState
import android.epicurius.ui.screens.utils.apiSuccess
import android.epicurius.ui.screens.utils.idle
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.time.LocalDate

class WeeklyMealPlannerViewModel(
    service: EpicuriusService,
    session: Session,
    context: Context
): EpicuriusViewModel(service, session, context) {

    private val weeklyMealPlannerFlow = MutableStateFlow<LoadState<List<DailyMealPlanner>>>(idle())

    val weeklyMealPlanner = weeklyMealPlannerFlow.asStateFlow()

    fun getWeeklyMealPlanner(onErrorNavigateTo: () -> Unit) {
        disableButtons()
        viewModelScope.launch { fetchWeeklyMealPlanner(onErrorNavigateTo) }
    }

    private suspend fun fetchWeeklyMealPlanner(onErrorNavigateTo: () -> Unit) {
        val result = request {
            val token = session.getToken()
            service.mealPlannerService.getWeeklyMealPlanner(token)
        }
        when {
            result.isFailure -> onErrorNavigateTo()
            result.isSuccess -> {
                val fetchedWeeklyMealPlanner = result.getValueOrThrow().planner
                handleNotCreatedDailyMealPlanners(fetchedWeeklyMealPlanner)
            }
        }
        enableButtons()
    }

    private suspend fun handleNotCreatedDailyMealPlanners(fetchedWeeklyMealPlanner: List<DailyMealPlanner>) {
        val week = getWeek(LocalDate.now())
        val newDailyMealPlanners = mutableListOf<DailyMealPlanner>()
        week.forEach { date ->
            if (
                fetchedWeeklyMealPlanner.firstOrNull { it.date == date } == null ||
                date.isEqual(LocalDate.now()) || date.isAfter(LocalDate.now())
            ) {
                val newDailyMealPlanner = handleCreateDailyMealPlanner(CreateMealPlannerInputModel(date, null))
                if (newDailyMealPlanner != null) newDailyMealPlanners.add(newDailyMealPlanner)
            }
        }
        weeklyMealPlannerFlow.value = apiSuccess(fetchedWeeklyMealPlanner + newDailyMealPlanners)
    }

    private suspend fun handleCreateDailyMealPlanner(
        createDailyMealPlannerInfo: CreateMealPlannerInputModel
    ): DailyMealPlanner? {
        val result = request {
            val token = session.getToken()
            service.mealPlannerService.createDailyMealPlanner(token, createDailyMealPlannerInfo)
        }
        when {
            result.isSuccess -> return result.getValueOrThrow().daily
        }
        return null
    }
}