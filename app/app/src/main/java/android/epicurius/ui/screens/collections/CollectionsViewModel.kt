package android.epicurius.ui.screens.collections

import android.content.Context
import android.epicurius.domain.collection.CollectionProfile
import android.epicurius.domain.collection.validateName
import android.epicurius.services.EpicuriusService
import android.epicurius.services.api.collection.models.input.CreateCollectionInputModel
import android.epicurius.services.http.utils.CachedResult
import android.epicurius.storage.Session
import android.epicurius.ui.EpicuriusViewModel
import android.epicurius.ui.screens.utils.LoadState
import android.epicurius.ui.screens.utils.apiSuccess
import android.epicurius.ui.screens.utils.cache
import android.epicurius.ui.screens.utils.idle
import android.epicurius.ui.screens.utils.loading
import androidx.lifecycle.viewModelScope
import epicurius.domain.collection.CollectionType
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class CollectionsViewModel(
    service: EpicuriusService,
    session: Session,
    context: Context
): EpicuriusViewModel(service, session, context) {

    private val collectionsFlow = MutableStateFlow<LoadState<List<CollectionProfile>>>(idle())
    private val cacheCollectionsFlow = MutableStateFlow<List<CollectionProfile>>(emptyList())

    val collections = collectionsFlow.asStateFlow()

    fun getCollections(collectionType: CollectionType) {
        disableButtons()
        collectionsFlow.value = loading(CachedResult(cacheCollectionsFlow.value))
        viewModelScope.launch { fetchCollections(collectionType) }
    }

    fun createCollection(name: String, collectionType: CollectionType, onSuccessNavigateTo: (Int) -> Unit) {
        disableButtons()
        if (!validateName(name, ::showToast)) {
            enableButtons()
            return
        }
        val createCollectionInfo = CreateCollectionInputModel(name, collectionType)
        viewModelScope.launch {
            handleCreateCollection(createCollectionInfo, onSuccessNavigateTo)
        }
    }

    fun deleteCollection(id: Int) {
        disableButtons()
        viewModelScope.launch { handleDeleteCollection(id) }
    }

    private suspend fun fetchCollections(collectionType: CollectionType) {
        val result = request {
            val token = session.getToken()
            val lastCollectionId = cacheCollectionsFlow.value.lastOrNull()?.id
            service.collectionService.getCollections(
                token,
                null,
                collectionType,
                lastCollectionId,
                limit
            )
        }
        when {
            result.isFailure -> collectionsFlow.value = cache(cacheCollectionsFlow.value)
            result.isSuccess -> {
                val fetchedCollections = result.getValueOrThrow().collections
                if (fetchedCollections.isNotEmpty()) {
                    val updatedCollections = cacheCollectionsFlow.value + fetchedCollections
                    collectionsFlow.value = apiSuccess(updatedCollections)
                    cacheCollectionsFlow.value = updatedCollections
                }
                else collectionsFlow.value = cache(cacheCollectionsFlow.value)
            }
        }
        enableButtons()
    }

    private suspend fun handleCreateCollection(
        createCollectionInfo: CreateCollectionInputModel,
        onSuccessNavigateTo: (Int) -> Unit
    ) {
        val result = request {
            val token = session.getToken()
            service.collectionService.createCollection(token, createCollectionInfo)
        }
        when {
            result.isFailure -> enableButtons()
            result.isSuccess -> onSuccessNavigateTo(result.getValueOrThrow().collection.id)
        }
    }

    private suspend fun handleDeleteCollection(id: Int) {
        val result = request {
            val token = session.getToken()
            service.collectionService.deleteCollection(token, id)
        }
        when {
            result.isFailure -> collectionsFlow.value = cache(cacheCollectionsFlow.value)
            result.isSuccess -> {
                val updatedCollections = cacheCollectionsFlow.value.filter { it.id != id }
                collectionsFlow.value = apiSuccess(updatedCollections)
                cacheCollectionsFlow.value = updatedCollections
            }
        }
        enableButtons()
    }
}