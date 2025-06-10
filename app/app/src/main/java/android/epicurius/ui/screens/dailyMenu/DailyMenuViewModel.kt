package android.epicurius.ui.screens.dailyMenu

import android.content.Context
import android.epicurius.domain.collection.Collection
import android.epicurius.domain.collection.CollectionProfile
import android.epicurius.domain.recipe.RecipeInfo
import android.epicurius.services.EpicuriusService
import android.epicurius.services.api.collection.models.input.AddRecipeToCollectionInputModel
import android.epicurius.services.api.menu.DailyMenu
import android.epicurius.storage.Session
import android.epicurius.ui.EpicuriusViewModel
import android.epicurius.ui.screens.collections.CollectionsViewModel
import android.epicurius.ui.screens.utils.LoadState
import android.epicurius.ui.screens.utils.apiSuccess
import android.epicurius.ui.screens.utils.cache
import android.epicurius.ui.screens.utils.idle
import android.epicurius.ui.screens.utils.loading
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.viewModelScope
import epicurius.domain.collection.CollectionType
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.time.LocalDate

class DailyMenuViewModel(
    service: EpicuriusService,
    session: Session,
    context: Context
): CollectionsViewModel(service, session, context) {

    private val dailyMenuFlow = MutableStateFlow<LoadState<Map<String, RecipeInfo?>>>(idle())
    val collectionsFlow = MutableStateFlow<LoadState<List<CollectionProfile>>>(idle())
    val cachedCollectionsFlow = MutableStateFlow<List<CollectionProfile>>(emptyList())

    val dailyMenu = dailyMenuFlow.asStateFlow()
    val collections = collectionsFlow.asStateFlow()

    fun getDailyMenu(navigateTo: () -> Unit) {
        dailyMenuFlow.value = loading()
        viewModelScope.launch {
            val storedDailyMenu = session.getDailyMenu()
            val today = LocalDate.now()

            if (storedDailyMenu != null) {
                if (today == LocalDate.parse(storedDailyMenu.date)) {
                    dailyMenuFlow.value = cache(storedDailyMenu.menu)
                }
                else {
                    fetchDailyMenu(navigateTo)
                }
            }
            else {
                fetchDailyMenu(navigateTo)
            }
        }
    }

    private suspend fun fetchDailyMenu(navigateTo: () -> Unit) {
        val result = request {
            val token = session.getToken()
            service.dailyMenuService.getDailyMenu(token)
        }
        when {
            result.isFailure -> {
                navigateTo()
            }
            result.isSuccess -> {
                val dailyMenu = result.getValueOrThrow().menu
                dailyMenuFlow.value = apiSuccess(dailyMenu)
                session.updateDailyMenu(DailyMenu(LocalDate.now().toString(), dailyMenu))
            }
        }
    }

}