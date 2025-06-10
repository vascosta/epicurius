package android.epicurius.ui.screens.collections

import android.content.Context
import android.epicurius.domain.collection.COLLECTION_NAME_LENGTH_MSG
import android.epicurius.domain.collection.Collection
import android.epicurius.domain.collection.CollectionProfile
import android.epicurius.domain.collection.MAX_COLLECTION_NAME_LENGTH
import android.epicurius.domain.collection.MIN_COLLECTION_NAME_LENGTH
import android.epicurius.services.EpicuriusService
import android.epicurius.services.api.collection.models.input.AddRecipeToCollectionInputModel
import android.epicurius.storage.Session
import android.epicurius.ui.EpicuriusViewModel
import android.epicurius.ui.screens.utils.LoadState
import android.epicurius.ui.screens.utils.apiSuccess
import android.epicurius.ui.screens.utils.loading
import androidx.lifecycle.viewModelScope
import epicurius.domain.collection.CollectionType
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch

open class CollectionsViewModel(
    service: EpicuriusService,
    session: Session,
    context: Context
): EpicuriusViewModel(service, session, context) {

    fun getCollections(
        recipeId: Int,
        collectionsToAddRecipeFlow: MutableStateFlow<LoadState<List<CollectionProfile>>>,
        collectionsToRemoveRecipeFlow: MutableStateFlow<LoadState<List<CollectionProfile>>>,
        lastFetchedCollectionIdFlow: MutableStateFlow<Int?>
    ) {
        disableButtons()
        collectionsToAddRecipeFlow.value = loading()
        collectionsToRemoveRecipeFlow.value = loading()
        viewModelScope.launch {
            fetchCollections(
                recipeId,
                collectionsToAddRecipeFlow,
                collectionsToRemoveRecipeFlow,
                lastFetchedCollectionIdFlow
            )
        }
    }

    fun addRecipeToCollections(
        collectionsAvailableToAdd: List<CollectionProfile>,
        collectionsAvailableToRemove: List<CollectionProfile>,
        collectionsToAdd: List<CollectionProfile>,
        recipeId: Int,
        collectionsToAddRecipeFlow: MutableStateFlow<LoadState<List<CollectionProfile>>>,
        collectionsToRemoveRecipeFlow: MutableStateFlow<LoadState<List<CollectionProfile>>>
    ) {
        disableButtons()
        if (collectionsAvailableToAdd.isEmpty()) {
            enableButtons()
            return
        }
        val addRecipeInfo = AddRecipeToCollectionInputModel(recipeId)
        viewModelScope.launch {
            handleAddRecipeToFavouriteCollection(
                collectionsAvailableToAdd,
                collectionsAvailableToRemove,
                collectionsToAdd,
                addRecipeInfo,
                collectionsToAddRecipeFlow,
                collectionsToRemoveRecipeFlow
            )
        }
    }

    fun removeRecipeFromCollection(
        collectionsAvailableToAdd: List<CollectionProfile>,
        collectionsAvailableToRemove: List<CollectionProfile>,
        collectionsToRemove: List<CollectionProfile>,
        recipeId: Int,
        collectionsToAddRecipeFlow: MutableStateFlow<LoadState<List<CollectionProfile>>>,
        collectionsToRemoveRecipeFlow: MutableStateFlow<LoadState<List<CollectionProfile>>>,
    ) {
        disableButtons()
        if (collectionsAvailableToRemove.isEmpty()) {
            enableButtons()
            return
        }
        viewModelScope.launch {
            handleRemoveRecipeFromFavouriteCollection(
                collectionsAvailableToAdd,
                collectionsAvailableToRemove,
                collectionsToRemove,
                recipeId,
                collectionsToAddRecipeFlow,
                collectionsToRemoveRecipeFlow
            )
        }
    }

    private suspend fun fetchCollections(
        recipeId: Int,
        collectionsToAddRecipeFlow: MutableStateFlow<LoadState<List<CollectionProfile>>>,
        collectionsToRemoveRecipeFlow: MutableStateFlow<LoadState<List<CollectionProfile>>>,
        lastFetchedCollectionIdFlow: MutableStateFlow<Int?>
    ) {
        val result = request {
            val token = session.getToken()
            val lastCollectionId = lastFetchedCollectionIdFlow.value
            service.collectionService.getCollections(
                token,
                CollectionType.FAVOURITE,
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

                val collectionsToAddRecipe = fetchedCollections.filter { collection ->
                    val recipesIds = collection.recipes.map { it.id }
                    !recipesIds.contains(recipeId)
                }.map { it.toCollectionProfile() }

                val collectionsToRemoveRecipe = fetchedCollections.filter { collection ->
                    val recipesIds = collection.recipes.map { it.id }
                    recipesIds.contains(recipeId)
                }.map { it.toCollectionProfile() }

                lastFetchedCollectionIdFlow.value = fetchedCollections.last().id
                collectionsToAddRecipeFlow.value = apiSuccess(collectionsToAddRecipe)
                collectionsToRemoveRecipeFlow.value = apiSuccess(collectionsToRemoveRecipe)
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

    private suspend fun handleAddRecipeToFavouriteCollection(
        collectionsAvailableToAdd: List<CollectionProfile>,
        collectionsAvailableToRemove: List<CollectionProfile>,
        collectionsToAdd: List<CollectionProfile>,
        addRecipeInfo: AddRecipeToCollectionInputModel,
        collectionsToAddRecipeFlow: MutableStateFlow<LoadState<List<CollectionProfile>>>,
        collectionsToRemoveRecipeFlow: MutableStateFlow<LoadState<List<CollectionProfile>>>
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

        collectionsToAddRecipeFlow.value = apiSuccess(
            collectionsAvailableToAdd.filter { !collectionsIdsAdded.contains(it.id) }
        )

        collectionsToRemoveRecipeFlow.value = apiSuccess(
            collectionsAvailableToRemove +
                    collectionsAvailableToAdd.filter { collectionsIdsAdded.contains(it.id) }
        )
        enableButtons()
    }

    private suspend fun handleRemoveRecipeFromFavouriteCollection(
        collectionsAvailableToAdd: List<CollectionProfile>,
        collectionsAvailableToRemove: List<CollectionProfile>,
        collectionsToAdd: List<CollectionProfile>,
        recipeId: Int,
        collectionsToAddRecipeFlow: MutableStateFlow<LoadState<List<CollectionProfile>>>,
        collectionsToRemoveRecipeFlow: MutableStateFlow<LoadState<List<CollectionProfile>>>
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

        collectionsToRemoveRecipeFlow.value = apiSuccess(
            collectionsAvailableToRemove.filter { !collectionsIdsRemoved.contains(it.id)  }
        )

        collectionsToAddRecipeFlow.value = apiSuccess(
            collectionsAvailableToAdd +
                    collectionsAvailableToRemove.filter { collectionsIdsRemoved.contains(it.id)  }
        )

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