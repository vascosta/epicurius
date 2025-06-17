package android.epicurius.ui.screens.collections

import android.content.Context
import android.epicurius.domain.collection.COLLECTION_NAME_LENGTH_MSG
import android.epicurius.domain.collection.Collection
import android.epicurius.domain.collection.CollectionProfile
import android.epicurius.domain.collection.MAX_COLLECTION_NAME_LENGTH
import android.epicurius.domain.collection.MIN_COLLECTION_NAME_LENGTH
import android.epicurius.services.EpicuriusService
import android.epicurius.services.api.collection.models.input.AddRecipeToCollectionInputModel
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

open class CollectionsViewModel(
    service: EpicuriusService,
    session: Session,
    context: Context
): EpicuriusViewModel(service, session, context) {

    private val collectionsToAddRecipeFlow = MutableStateFlow<LoadState<List<CollectionProfile>>>(idle())
    private val cacheCollectionsToAddRecipeFlow = MutableStateFlow<List<CollectionProfile>>(emptyList())
    private val collectionsToRemoveRecipeFlow = MutableStateFlow<LoadState<List<CollectionProfile>>>(idle())
    private val cacheCollectionsToRemoveRecipeFlow = MutableStateFlow<List<CollectionProfile>>(emptyList())
    private val collectionsFlow = MutableStateFlow<LoadState<List<CollectionProfile>>>(idle())
    private val cacheCollectionsFlow = MutableStateFlow<List<CollectionProfile>>(emptyList())
    private val lastFetchedCollectionIdFlow = MutableStateFlow<Int?>(null)

    val collectionsToAddRecipe = collectionsToAddRecipeFlow.asStateFlow()
    private val cacheCollectionsToAddRecipe = cacheCollectionsToAddRecipeFlow.asStateFlow()
    val collectionsToRemoveRecipe = collectionsToRemoveRecipeFlow.asStateFlow()
    private val cacheCollectionsToRemoveRecipe = cacheCollectionsToRemoveRecipeFlow.asStateFlow()
    val collections = collectionsFlow.asStateFlow()
    private val cacheCollections = cacheCollectionsFlow.asStateFlow()

    fun getCollections(recipeId: Int?, collectionType: CollectionType) {
        disableButtons()
        if (recipeId != null) {
            collectionsToAddRecipeFlow.value = loading(CachedResult(cacheCollectionsToAddRecipe.value))
            collectionsToRemoveRecipeFlow.value = loading(CachedResult(cacheCollectionsToRemoveRecipe.value))
        }
        else collectionsFlow.value = loading(CachedResult(cacheCollections.value))
        viewModelScope.launch {
            fetchCollections(recipeId, collectionType)
        }
    }

    fun addRecipeToCollections(
        collectionsAvailableToAdd: List<CollectionProfile>,
        collectionsAvailableToRemove: List<CollectionProfile>,
        collectionsToAdd: List<CollectionProfile>,
        recipeId: Int
    ) {
        disableButtons()
        if (collectionsAvailableToAdd.isEmpty()) {
            enableButtons()
            return
        }
        val addRecipeInfo = AddRecipeToCollectionInputModel(recipeId)
        viewModelScope.launch {
            handleAddRecipeToFavouriteCollections(
                collectionsAvailableToAdd,
                collectionsAvailableToRemove,
                collectionsToAdd,
                addRecipeInfo
            )
        }
    }

    fun removeRecipeFromCollections(
        collectionsAvailableToAdd: List<CollectionProfile>,
        collectionsAvailableToRemove: List<CollectionProfile>,
        collectionsToRemove: List<CollectionProfile>,
        recipeId: Int
    ) {
        disableButtons()
        if (collectionsAvailableToRemove.isEmpty()) {
            enableButtons()
            return
        }
        viewModelScope.launch {
            handleRemoveRecipeFromFavouriteCollections(
                collectionsAvailableToAdd,
                collectionsAvailableToRemove,
                collectionsToRemove,
                recipeId
            )
        }
    }

    fun deleteCollection(id: Int) {
        disableButtons()
        viewModelScope.launch {
            handleDeleteCollection(id)
        }
    }

    fun clearCollections() {
        collectionsToAddRecipeFlow.value = idle()
        collectionsToRemoveRecipeFlow.value = idle()
        cacheCollectionsToAddRecipeFlow.value = emptyList()
        cacheCollectionsToRemoveRecipeFlow.value = emptyList()
        lastFetchedCollectionIdFlow.value = null
    }

    private suspend fun fetchCollections(recipeId: Int?, collectionType: CollectionType) {
        val result = request {
            val token = session.getToken()
            val lastCollectionId = lastFetchedCollectionIdFlow.value
            service.collectionService.getCollections(
                token,
                collectionType,
                lastCollectionId,
                limit
            )
        }
        when {
            result.isSuccess -> {
                val fetchedCollections =
                    result.getValueOrThrow().collections.mapNotNull { collection ->
                        fetchCollection(collection.id)
                    }
                if (fetchedCollections.isNotEmpty()) {
                    if (recipeId == null) {
                        val updatedCollections =
                            cacheCollections.value + fetchedCollections.map { it.toCollectionProfile() }
                        collectionsFlow.value = apiSuccess(updatedCollections)
                        cacheCollectionsFlow.value = updatedCollections
                    }
                    else {
                        val collectionsToAddRecipe = fetchedCollections.filter { collection ->
                            val recipesIds = collection.recipes.map { it.id }
                            !recipesIds.contains(recipeId)
                        }.map { it.toCollectionProfile() }

                        val collectionsToRemoveRecipe = fetchedCollections.filter { collection ->
                            val recipesIds = collection.recipes.map { it.id }
                            recipesIds.contains(recipeId)
                        }.map { it.toCollectionProfile() }

                        val updatedCollectionsToAddRecipe =
                            cacheCollectionsToAddRecipe.value + collectionsToAddRecipe
                        val updatedCollectionsToRemoveRecipe =
                            cacheCollectionsToRemoveRecipe.value + collectionsToRemoveRecipe
                        collectionsToAddRecipeFlow.value = apiSuccess(updatedCollectionsToAddRecipe)
                        cacheCollectionsToAddRecipeFlow.value = updatedCollectionsToAddRecipe
                        collectionsToRemoveRecipeFlow.value = apiSuccess(updatedCollectionsToRemoveRecipe)
                        cacheCollectionsToRemoveRecipeFlow.value = updatedCollectionsToRemoveRecipe
                    }
                    lastFetchedCollectionIdFlow.value = fetchedCollections.last().id
                }
                else {
                    if (recipeId == null) collectionsFlow.value = cache(cacheCollections.value)
                    else {
                        collectionsToAddRecipeFlow.value = cache(cacheCollectionsToAddRecipe.value)
                        collectionsToRemoveRecipeFlow.value = cache(cacheCollectionsToRemoveRecipe.value)
                    }
                }
            }
        }
        enableButtons()
    }

    private suspend fun fetchCollection(id: Int): Collection? {
        val result = request {
            val token = session.getToken()
            service.collectionService.getCollection(token, id)
        }
        when {
            result.isSuccess -> {
                return result.getValueOrThrow().collection
            }
        }
        return null
    }

    private suspend fun handleAddRecipeToFavouriteCollections(
        collectionsAvailableToAdd: List<CollectionProfile>,
        collectionsAvailableToRemove: List<CollectionProfile>,
        collectionsToAdd: List<CollectionProfile>,
        addRecipeInfo: AddRecipeToCollectionInputModel
    ) {
        val collectionsIds = mutableListOf<Int?>()
        for (collection in collectionsToAdd) {
            val result = request {
                val token = session.getToken()
                service.collectionService.addRecipeToCollection(token, collection.id, addRecipeInfo)
            }
            when {
                result.isFailure -> {
                    showToast("Error while adding the recipe to ${collection.name} collection")
                    collectionsIds.add(null)
                }
                result.isSuccess -> collectionsIds.add(collection.id)
            }
        }
        val collectionsIdsAdded = collectionsIds.filterNotNull()

        val collectionsAdded = collectionsAvailableToAdd.filter { !collectionsIdsAdded.contains(it.id) }
        collectionsToAddRecipeFlow.value = apiSuccess(collectionsAdded)
        cacheCollectionsToAddRecipeFlow.value = collectionsAdded

        val collectionsNotAdded = collectionsAvailableToRemove +
                collectionsAvailableToAdd.filter { collectionsIdsAdded.contains(it.id) }
        collectionsToRemoveRecipeFlow.value = apiSuccess(collectionsNotAdded)
        cacheCollectionsToRemoveRecipeFlow.value = collectionsNotAdded
        enableButtons()
    }

    private suspend fun handleRemoveRecipeFromFavouriteCollections(
        collectionsAvailableToAdd: List<CollectionProfile>,
        collectionsAvailableToRemove: List<CollectionProfile>,
        collectionsToAdd: List<CollectionProfile>,
        recipeId: Int
    ) {
        val collectionsIds = mutableListOf<Int?>()
        for (collection in collectionsToAdd) {
            val result = request {
                val token = session.getToken()
                service.collectionService.removeRecipeFromCollection(token, collection.id, recipeId)
            }
            when {
                result.isFailure -> {
                    showToast("Error while removing the recipe from ${collection.name} collection")
                    collectionsIds.add(null)
                }
                result.isSuccess -> collectionsIds.add(collection.id)
            }
        }
        val collectionsIdsRemoved = collectionsIds.filterNotNull()

        val collectionsRemoved = collectionsAvailableToRemove.filter { !collectionsIdsRemoved.contains(it.id) }
        collectionsToRemoveRecipeFlow.value = apiSuccess(collectionsRemoved)
        cacheCollectionsToRemoveRecipeFlow.value = collectionsRemoved

        val collectionsNotRemoved = collectionsAvailableToAdd +
                collectionsAvailableToRemove.filter { collectionsIdsRemoved.contains(it.id) }
        collectionsToAddRecipeFlow.value = apiSuccess(collectionsNotRemoved)
        cacheCollectionsToAddRecipeFlow.value = collectionsNotRemoved
        enableButtons()
    }

    private suspend fun handleDeleteCollection(id: Int) {
        val result = request {
            val token = session.getToken()
            service.collectionService.deleteCollection(token, id)
        }
        when {
            result.isSuccess -> {
                val updatedCollections = cacheCollections.value.filter { it.id != id }
                collectionsFlow.value = apiSuccess(updatedCollections)
                cacheCollectionsFlow.value = updatedCollections
            }
        }
        enableButtons()
    }

    fun validateCollectionName(name: String): Boolean = validateName(name)

    private fun validateName(name: String): Boolean {
        println(name.length)
        if (name.length !in MIN_COLLECTION_NAME_LENGTH..MAX_COLLECTION_NAME_LENGTH) {
            showToast(COLLECTION_NAME_LENGTH_MSG)
            return false
        }
        return true
    }
}