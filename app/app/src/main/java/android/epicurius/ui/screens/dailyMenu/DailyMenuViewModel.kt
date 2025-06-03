package android.epicurius.ui.screens.dailyMenu

import android.content.Context
import android.epicurius.domain.recipe.RecipeInfo
import android.epicurius.services.EpicuriusService
import android.epicurius.storage.Session
import android.epicurius.ui.EpicuriusViewModel
import android.epicurius.ui.screens.utils.LoadState
import android.epicurius.ui.screens.utils.apiFailure
import android.epicurius.ui.screens.utils.idle
import android.epicurius.ui.screens.utils.loading
import android.epicurius.ui.screens.utils.apiSuccess
import android.epicurius.ui.screens.utils.cacheSuccess
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.time.LocalDate

class DailyMenuViewModel(
    service: EpicuriusService,
    session: Session,
    context: Context
): EpicuriusViewModel(service, session, context) {

    private val dailyMenuFlow = MutableStateFlow<LoadState<Map<String, RecipeInfo?>>>(idle())
    val dailyMenu = dailyMenuFlow.asStateFlow()

    fun getDailyMenu() {
        dailyMenuFlow.value = loading()
        viewModelScope.launch {
            val storedDailyMenu = session.getDailyMenu()
            val today = LocalDate.now()

            if (storedDailyMenu != null) {
                if (today == storedDailyMenu.date) {
                    dailyMenuFlow.value = cacheSuccess(storedDailyMenu.menu)
                }
                else {
                    fetchDailyMenu()
                }
            }
            else {
                fetchDailyMenu()
            }
        }
    }

    suspend fun fetchDailyMenu() {
        val result = request {
            val token = session.getToken()
            service.dailyMenuService.getDailyMenu(token)
        }
        when {
            result.isFailure -> {
                dailyMenuFlow.value = apiFailure(result.getProblemOrThrow())
            }
            result.isSuccess -> {
                dailyMenuFlow.value = apiSuccess(result.getValueOrThrow().menu)
            }
        }
    }
}