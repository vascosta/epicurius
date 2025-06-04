package android.epicurius.ui.screens.favourites.folder

import android.content.Context
import android.epicurius.domain.collection.CollectionProfile
import android.epicurius.services.EpicuriusService
import android.epicurius.storage.Session
import android.epicurius.ui.EpicuriusViewModel
import android.epicurius.ui.screens.utils.LoadState
import android.epicurius.ui.screens.utils.apiFailure
import android.epicurius.ui.screens.utils.apiSuccess
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

class FavouritesViewModel(
    service: EpicuriusService,
    session: Session,
    context: Context
): EpicuriusViewModel(service, session, context) {

    private val favouritesFlow = MutableStateFlow<LoadState< List<CollectionProfile>>>(idle())
    val favourites = favouritesFlow.asStateFlow()

    var skip by mutableIntStateOf(0)
        private set

    var limit by mutableIntStateOf(10)

    fun getFavourites() {
        favouritesFlow.value = loading()
        viewModelScope.launch {
            fetchFavourites()
        }
    }

    fun refreshFavourites() {
        resetSkip()
        getFavourites()
    }

    private suspend fun fetchFavourites() {
        val result = request {
            val token = session.getToken()
            service.collectionService.getCollections(token, CollectionType.FAVOURITE, skip, limit)
        }
        when {
            result.isFailure -> {
                favouritesFlow.value = apiFailure(result.getProblemOrThrow())
            }
            result.isSuccess -> {
                favouritesFlow.value = apiSuccess(result.getValueOrThrow().collections)
                increaseSkip()
            }
        }
    }

    private fun increaseSkip() {
        skip += 10
    }

    private fun resetSkip() {
        skip = 0
    }
}