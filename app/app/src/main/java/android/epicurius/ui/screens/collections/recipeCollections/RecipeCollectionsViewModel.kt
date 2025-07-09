package android.epicurius.ui.screens.collections.recipeCollections

import android.content.Context
import android.epicurius.domain.collection.Collection
import android.epicurius.domain.collection.CollectionProfile
import android.epicurius.services.EpicuriusService
import android.epicurius.services.api.collection.models.input.AddRecipeToCollectionInputModel
import android.epicurius.services.http.utils.APIResult
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

open class RecipeCollectionsViewModel(
    service: EpicuriusService,
    session: Session,
    context: Context
): EpicuriusViewModel(service, session, context) {

    private val collectionsToAddRecipeFlow = MutableStateFlow<LoadState<List<CollectionProfile>>>(idle())
    private val cacheCollectionsToAddRecipeFlow = MutableStateFlow<List<CollectionProfile>>(emptyList())

    val collectionsToAddRecipe = collectionsToAddRecipeFlow.asStateFlow()

    private val collectionsToRemoveRecipeFlow = MutableStateFlow<LoadState<List<CollectionProfile>>>(idle())
    private val cacheCollectionsToRemoveRecipeFlow = MutableStateFlow<List<CollectionProfile>>(emptyList())

    val collectionsToRemoveRecipe = collectionsToRemoveRecipeFlow.asStateFlow()

    private val lastFetchedCollectionIdFlow = MutableStateFlow<Int?>(null)

    fun getRecipeCollections(recipeId: Int, collectionType: CollectionType) {
        disableButtons()
        collectionsToAddRecipeFlow.value = loading(APIResult(cacheCollectionsToAddRecipeFlow.value))
        collectionsToRemoveRecipeFlow.value = loading(APIResult(cacheCollectionsToRemoveRecipeFlow.value))
        viewModelScope.launch { fetchRecipeCollections(recipeId, collectionType) }
    }

    fun addRecipeToCollections(recipeId: Int, collectionsToAdd: List<CollectionProfile>) {
        disableButtons()
        if (cacheCollectionsToAddRecipeFlow.value.isEmpty()) {
            enableButtons()
            return
        }
        val addRecipeInfo = AddRecipeToCollectionInputModel(recipeId)
        viewModelScope.launch {
            handleAddRecipeToCollections(addRecipeInfo, collectionsToAdd)
        }
    }

    fun removeRecipeFromCollections(recipeId: Int, collectionsToRemove: List<CollectionProfile>) {
        disableButtons()
        if (cacheCollectionsToRemoveRecipeFlow.value.isEmpty()) {
            enableButtons()
            return
        }
        viewModelScope.launch {
            handleRemoveRecipeFromCollections(recipeId, collectionsToRemove)
        }
    }

    fun clearRecipeCollections() {
        collectionsToAddRecipeFlow.value = idle()
        collectionsToRemoveRecipeFlow.value = idle()
        cacheCollectionsToAddRecipeFlow.value = emptyList()
        cacheCollectionsToRemoveRecipeFlow.value = emptyList()
        lastFetchedCollectionIdFlow.value = null
    }

    private suspend fun fetchRecipeCollections(recipeId: Int, collectionType: CollectionType) {
        val result = request {
            val token = session.getToken()
            val lastCollectionId = lastFetchedCollectionIdFlow.value
            service.collectionService.getCollections(
                token,
                null,
                collectionType,
                lastCollectionId,
                limit
            )
        }
        when {
            result.isFailure -> handleCachedCollections()
            result.isSuccess -> {
                val fetchedCollections = result.getValueOrThrow().collections
                    .mapNotNull { collection -> fetchCollection(collection.id) }
                if (fetchedCollections.isNotEmpty()) {
                    val collectionsToAddRecipe = fetchedCollections.filter { collection ->
                        val recipesIds = collection.recipes.map { it.id }
                        !recipesIds.contains(recipeId)
                    }.map { it.toCollectionProfile() }

                    val collectionsToRemoveRecipe = fetchedCollections.filter { collection ->
                        val recipesIds = collection.recipes.map { it.id }
                        recipesIds.contains(recipeId)
                    }.map { it.toCollectionProfile() }

                    val updatedCollectionsToAddRecipe = cacheCollectionsToAddRecipeFlow.value + collectionsToAddRecipe
                    val updatedCollectionsToRemoveRecipe = cacheCollectionsToRemoveRecipeFlow.value + collectionsToRemoveRecipe
                    collectionsToAddRecipeFlow.value = apiSuccess(updatedCollectionsToAddRecipe)
                    cacheCollectionsToAddRecipeFlow.value = updatedCollectionsToAddRecipe
                    collectionsToRemoveRecipeFlow.value = apiSuccess(updatedCollectionsToRemoveRecipe)
                    cacheCollectionsToRemoveRecipeFlow.value = updatedCollectionsToRemoveRecipe
                    lastFetchedCollectionIdFlow.value = fetchedCollections.last().id
                }
                else handleCachedCollections()
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
            result.isSuccess -> return result.getValueOrThrow().collection
        }
        return null
    }

    private fun handleCachedCollections() {
        collectionsToAddRecipeFlow.value = cache(cacheCollectionsToAddRecipeFlow.value)
        collectionsToRemoveRecipeFlow.value = cache(cacheCollectionsToRemoveRecipeFlow.value)
    }

    private suspend fun handleAddRecipeToCollections(
        addRecipeInfo: AddRecipeToCollectionInputModel,
        collectionsToAdd: List<CollectionProfile>,
    ) {
        val collectionsIds = mutableListOf<Int?>()
        for (collection in collectionsToAdd) {
            val result = request {
                val token = session.getToken()
                service.collectionService.addRecipeToCollection(token, collection.id, addRecipeInfo)
            }
            when {
                result.isFailure -> collectionsIds.add(null)
                result.isSuccess -> collectionsIds.add(collection.id)
            }
        }
        val collectionsIdsAdded = collectionsIds.filterNotNull()

        val collectionsAvailableToAdd = cacheCollectionsToAddRecipeFlow.value.filter { !collectionsIdsAdded.contains(it.id) }
        val updatedCollectionsToRemove = cacheCollectionsToRemoveRecipeFlow.value +
                cacheCollectionsToAddRecipeFlow.value.filter { collectionsIdsAdded.contains(it.id) }

        collectionsToAddRecipeFlow.value = apiSuccess(collectionsAvailableToAdd)
        cacheCollectionsToAddRecipeFlow.value = collectionsAvailableToAdd

        collectionsToRemoveRecipeFlow.value = apiSuccess(updatedCollectionsToRemove)
        cacheCollectionsToRemoveRecipeFlow.value = updatedCollectionsToRemove

        enableButtons()
    }

    private suspend fun handleRemoveRecipeFromCollections(
        recipeId: Int,
        collectionsToAdd: List<CollectionProfile>
    ) {
        val collectionsIds = mutableListOf<Int?>()
        for (collection in collectionsToAdd) {
            val result = request {
                val token = session.getToken()
                service.collectionService.removeRecipeFromCollection(token, collection.id, recipeId)
            }
            when {
                result.isFailure -> collectionsIds.add(null)
                result.isSuccess -> collectionsIds.add(collection.id)
            }
        }
        val collectionsIdsRemoved = collectionsIds.filterNotNull()

        val collectionsAvailableToRemove = cacheCollectionsToRemoveRecipeFlow.value.filter { !collectionsIdsRemoved.contains(it.id) }
        val updatedCollectionsToAdd = cacheCollectionsToAddRecipeFlow.value +
                cacheCollectionsToRemoveRecipeFlow.value.filter { collectionsIdsRemoved.contains(it.id) }


        collectionsToAddRecipeFlow.value = apiSuccess(updatedCollectionsToAdd)
        cacheCollectionsToAddRecipeFlow.value = updatedCollectionsToAdd

        collectionsToRemoveRecipeFlow.value = apiSuccess(collectionsAvailableToRemove)
        cacheCollectionsToRemoveRecipeFlow.value = collectionsAvailableToRemove

        enableButtons()
    }
}