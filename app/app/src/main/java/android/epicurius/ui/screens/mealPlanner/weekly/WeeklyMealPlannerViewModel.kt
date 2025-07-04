package android.epicurius.ui.screens.mealPlanner.weekly

import android.content.Context
import android.epicurius.domain.mealPlanner.DailyMealPlanner
import android.epicurius.services.EpicuriusService
import android.epicurius.storage.Session
import android.epicurius.ui.EpicuriusViewModel
import android.epicurius.ui.screens.utils.LoadState
import android.epicurius.ui.screens.utils.apiSuccess
import android.epicurius.ui.screens.utils.idle
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

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
            result.isSuccess -> weeklyMealPlannerFlow.value = apiSuccess(result.getValueOrThrow().planner)
        }
        enableButtons()
    }
}