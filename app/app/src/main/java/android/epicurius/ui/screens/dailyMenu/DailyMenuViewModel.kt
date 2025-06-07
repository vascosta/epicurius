package android.epicurius.ui.screens.dailyMenu

import android.content.Context
import android.epicurius.domain.collection.CollectionProfile
import android.epicurius.domain.recipe.RecipeInfo
import android.epicurius.services.EpicuriusService
import android.epicurius.services.api.collection.models.input.AddRecipeToCollectionInputModel
import android.epicurius.storage.Session
import android.epicurius.ui.EpicuriusViewModel
import android.epicurius.ui.screens.utils.LoadState
import android.epicurius.ui.screens.utils.idle
import android.epicurius.ui.screens.utils.loading
import android.epicurius.ui.screens.utils.apiSuccess
import android.epicurius.ui.screens.utils.cache
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
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
): EpicuriusViewModel(service, session, context) {

    private val dailyMenuFlow = MutableStateFlow<LoadState<Map<String, RecipeInfo?>>>(idle())
    private val collectionsFlow = MutableStateFlow<LoadState<List<CollectionProfile>>>(idle())
    private val cachedCollectionsFlow = MutableStateFlow<List<CollectionProfile>>(emptyList())

    val dailyMenu = dailyMenuFlow.asStateFlow()
    val collections = collectionsFlow.asStateFlow()

    var enableButtons by mutableStateOf(true)

    var limit by mutableIntStateOf(10)

    fun getDailyMenu(navigateTo: () -> Unit) {
        dailyMenuFlow.value = loading()
        viewModelScope.launch {
            val storedDailyMenu = session.getDailyMenu()
            val today = LocalDate.now()

            if (storedDailyMenu != null) {
                if (today == storedDailyMenu.date) {
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

    fun getCollections() {
        disableButtons()
        collectionsFlow.value = loading()
        viewModelScope.launch {
            fetchCollections()
        }
    }

    fun addRecipeToCollection(collectionId: Int, recipeId: Int, navigateTo: () -> Unit) {
        disableButtons()
        val addRecipeInfo = AddRecipeToCollectionInputModel(recipeId)
        viewModelScope.launch {
            handleAddRecipeToFavouriteCollection(collectionId, addRecipeInfo, navigateTo)
        }
    }

    fun removeRecipeFromCollection(collectionId: Int, recipeId: Int, navigateTo: () -> Unit) {
        disableButtons()
        viewModelScope.launch {
            handleRemoveRecipeFromFavouriteCollection(collectionId, recipeId, navigateTo)
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
                dailyMenuFlow.value = apiSuccess(result.getValueOrThrow().menu)
            }
        }
    }

    private suspend fun fetchCollections() {
        val result = request {
            val token = session.getToken()
            val lastCollectionId = cachedCollectionsFlow.value.lastOrNull()?.id
            service.collectionService.getCollections(
                token,
                CollectionType.FAVOURITE,
                lastCollectionId,
                limit
            )
        }
        when {
            result.isSuccess -> {
                val fetchedCollections = result.getValueOrThrow().collections
                cachedCollectionsFlow.value = fetchedCollections
                collectionsFlow.value = apiSuccess(fetchedCollections)
            }
        }
        enableButtons()
    }

    private suspend fun handleAddRecipeToFavouriteCollection(
        collectionId: Int,
        addRecipeInfo: AddRecipeToCollectionInputModel,
        navigateTo: () -> Unit
    ) {
        val result = request {
            val token = session.getToken()
            service.collectionService.addRecipeToCollection(token, collectionId, addRecipeInfo)
        }
        when {
            result.isFailure -> {
                enableButtons()
            }
            result.isSuccess -> {
                getDailyMenu(navigateTo)
            }
        }
    }

    private suspend fun handleRemoveRecipeFromFavouriteCollection(
        collectionId: Int,
        recipeId: Int,
        navigateTo: () -> Unit
    ) {
        val result = request {
            val token = session.getToken()
            service.collectionService.removeRecipeFromCollection(token, collectionId, recipeId)
        }
        when {
            result.isFailure -> {
                enableButtons()
            }
            result.isSuccess -> {
                getDailyMenu(navigateTo)
            }
        }
    }

    private fun enableButtons() {
        enableButtons = true
    }

    private fun disableButtons() {
        enableButtons = false
    }
}