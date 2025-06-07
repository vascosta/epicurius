package android.epicurius.ui.screens.collections.favourites.folder

import android.content.Context
import android.epicurius.domain.collection.CollectionProfile
import android.epicurius.services.EpicuriusService
import android.epicurius.services.api.collection.models.input.CreateCollectionInputModel
import android.epicurius.storage.Session
import android.epicurius.ui.screens.collections.CollectionsViewModel
import android.epicurius.ui.screens.utils.LoadState
import android.epicurius.ui.screens.utils.apiFailure
import android.epicurius.ui.screens.utils.apiSuccess
import android.epicurius.ui.screens.utils.idle
import android.epicurius.ui.screens.utils.loading
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.viewModelScope
import epicurius.domain.collection.CollectionType
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class FavouritesViewModel(
    service: EpicuriusService,
    session: Session,
    context: Context
): CollectionsViewModel(service, session, context) {

    private val favouritesFlow = MutableStateFlow<LoadState<List<CollectionProfile>>>(idle())
    private val cachedFavourites = MutableStateFlow<List<CollectionProfile>>(emptyList())
    val favourites = favouritesFlow.asStateFlow()

    var enableButtons by mutableStateOf(true)

    var limit by mutableIntStateOf(10)

    fun getFavourites(navigateTo: () -> Unit) {
        favouritesFlow.value = loading()
        viewModelScope.launch {
            fetchFavourites(navigateTo)
        }
    }

    fun createFavouriteCollection(name: String, navigateTo: (Int) -> Unit) {
        disableButtons()
        if (!validateCollectionName(name)) {
            enableButtons()
            return
        }
        val createCollectionInfo = CreateCollectionInputModel(name, CollectionType.FAVOURITE)
        viewModelScope.launch {
            handleCreateFavouriteCollection(createCollectionInfo, navigateTo)
        }
    }

    fun deleteFavouriteCollection(id: Int) {
        disableButtons()
        viewModelScope.launch {
            handleDeleteFavouriteCollection(id)
        }
    }

    private suspend fun fetchFavourites(navigateTo: () -> Unit) {
        val result = request {
            val token = session.getToken()
            val lastCollectionId = cachedFavourites.value.lastOrNull()?.id
            service.collectionService.getCollections(
                token,
                CollectionType.FAVOURITE,
                lastCollectionId,
                limit
            )
        }
        when {
            result.isFailure -> {
                navigateTo()
            }
            result.isSuccess -> {
                val fetchedFavourites = result.getValueOrThrow().collections
                cachedFavourites.value = fetchedFavourites
                favouritesFlow.value = apiSuccess(fetchedFavourites)
            }
        }
    }

    private suspend fun handleCreateFavouriteCollection(
        createCollectionInfo: CreateCollectionInputModel,
        navigateTo: (Int) -> Unit
    ) {
        val result = request {
            val token = session.getToken()
            service.collectionService.createCollection(token, createCollectionInfo)
        }
        when {
            result.isFailure -> {
                enableButtons()
            }
            result.isSuccess -> {
                navigateTo(result.getValueOrThrow().collection.id)
            }
        }
    }

    private suspend fun handleDeleteFavouriteCollection(id: Int) {
        val result = request {
            val token = session.getToken()
            service.collectionService.deleteCollection(token, id)
        }
        when {
            result.isSuccess -> {
                val updatedFavourites = cachedFavourites.value.filter { it.id != id }
                cachedFavourites.value = updatedFavourites
                favouritesFlow.value = apiSuccess(updatedFavourites)
            }
        }
        enableButtons()
    }

    private fun enableButtons() {
        enableButtons = true
    }

    private fun disableButtons() {
        enableButtons = false
    }
}