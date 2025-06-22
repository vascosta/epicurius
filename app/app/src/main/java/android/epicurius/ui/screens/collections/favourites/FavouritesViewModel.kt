package android.epicurius.ui.screens.collections.favourites

import android.content.Context
import android.epicurius.services.EpicuriusService
import android.epicurius.services.api.collection.models.input.CreateCollectionInputModel
import android.epicurius.storage.Session
import android.epicurius.ui.screens.collections.CollectionsViewModel
import androidx.lifecycle.viewModelScope
import epicurius.domain.collection.CollectionType
import kotlinx.coroutines.launch

class FavouritesViewModel(
    service: EpicuriusService,
    session: Session,
    context: Context
): CollectionsViewModel(service, session, context) {

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

    private suspend fun handleCreateFavouriteCollection(
        createCollectionInfo: CreateCollectionInputModel,
        navigateTo: (Int) -> Unit
    ) {
        val result = request {
            val token = session.getToken()
            service.collectionService.createCollection(token, createCollectionInfo)
        }
        when {
            result.isFailure -> enableButtons()
            result.isSuccess -> navigateTo(result.getValueOrThrow().collection.id)
        }
    }
}