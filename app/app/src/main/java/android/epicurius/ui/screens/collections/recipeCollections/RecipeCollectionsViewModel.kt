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

class RecipeCollectionsViewModel(
    service: EpicuriusService,
    session: Session,
    context: Context
): EpicuriusViewModel(service, session, context) {

    private val collectionsToAddRecipeFlow =
        MutableStateFlow<LoadState<List<CollectionProfile>>>(idle())
    private val cacheCollectionsToAddRecipeFlow =
        MutableStateFlow<List<CollectionProfile>>(emptyList())

    val collectionsToAddRecipe = collectionsToAddRecipeFlow.asStateFlow()
    private val cacheCollectionsToAddRecipe = cacheCollectionsToAddRecipeFlow.asStateFlow()

    private val collectionsToRemoveRecipeFlow =
        MutableStateFlow<LoadState<List<CollectionProfile>>>(idle())
    private val cacheCollectionsToRemoveRecipeFlow =
        MutableStateFlow<List<CollectionProfile>>(emptyList())

    val collectionsToRemoveRecipe = collectionsToRemoveRecipeFlow.asStateFlow()
    private val cacheCollectionsToRemoveRecipe = cacheCollectionsToRemoveRecipeFlow.asStateFlow()

    private val lastFetchedCollectionIdFlow = MutableStateFlow<Int?>(null)

    fun getRecipeCollections(recipeId: Int, collectionType: CollectionType) {
        disableButtons()
        collectionsToAddRecipeFlow.value =
            loading(APIResult(cacheCollectionsToAddRecipe.value))
        collectionsToRemoveRecipeFlow.value =
            loading(APIResult(cacheCollectionsToRemoveRecipe.value))
        viewModelScope.launch { fetchRecipeCollections(recipeId, collectionType) }
    }

    fun addRecipeToCollections(recipeId: Int, collectionsToAdd: List<CollectionProfile>) {
        disableButtons()
        if (cacheCollectionsToAddRecipe.value.isEmpty()) {
            enableButtons()
            return
        }
        val addRecipeInfo = AddRecipeToCollectionInputModel(recipeId)
        viewModelScope.launch {
            handleAddRecipeToCollections(
                addRecipeInfo,
                collectionsToAdd
            )
        }
    }

    fun removeRecipeFromCollections(recipeId: Int, collectionsToRemove: List<CollectionProfile>) {
        disableButtons()
        if (cacheCollectionsToRemoveRecipe.value.isEmpty()) {
            enableButtons()
            return
        }
        viewModelScope.launch {
            handleRemoveRecipeFromCollections(
                recipeId,
                collectionsToRemove
            )
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
                collectionType,
                lastCollectionId,
                limit
            )
        }
        when {
            result.isFailure -> handleCachedCollections()
            result.isSuccess -> {
                val fetchedCollections =
                    result.getValueOrThrow().collections.mapNotNull { collection ->
                        fetchCollection(collection.id)
                    }
                if (fetchedCollections.isNotEmpty()) {
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
                    collectionsToRemoveRecipeFlow.value =
                        apiSuccess(updatedCollectionsToRemoveRecipe)
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
        collectionsToAddRecipeFlow.value = cache(cacheCollectionsToAddRecipe.value)
        collectionsToRemoveRecipeFlow.value = cache(cacheCollectionsToRemoveRecipe.value)
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

        val collectionsAdded = cacheCollectionsToAddRecipeFlow.value.filter { !collectionsIdsAdded.contains(it.id) }
        collectionsToAddRecipeFlow.value = apiSuccess(collectionsAdded)
        cacheCollectionsToAddRecipeFlow.value = collectionsAdded

        val collectionsNotAdded = cacheCollectionsToRemoveRecipe.value +
                cacheCollectionsToAddRecipeFlow.value.filter { collectionsIdsAdded.contains(it.id) }
        collectionsToRemoveRecipeFlow.value = apiSuccess(collectionsNotAdded)
        cacheCollectionsToRemoveRecipeFlow.value = collectionsNotAdded

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

        val collectionsRemoved = cacheCollectionsToRemoveRecipe.value.filter { !collectionsIdsRemoved.contains(it.id) }
        collectionsToRemoveRecipeFlow.value = apiSuccess(collectionsRemoved)
        cacheCollectionsToRemoveRecipeFlow.value = collectionsRemoved

        val collectionsNotRemoved = cacheCollectionsToAddRecipeFlow.value +
                cacheCollectionsToRemoveRecipe.value.filter { collectionsIdsRemoved.contains(it.id) }
        collectionsToAddRecipeFlow.value = apiSuccess(collectionsNotRemoved)
        cacheCollectionsToAddRecipeFlow.value = collectionsNotRemoved

        enableButtons()
    }
}