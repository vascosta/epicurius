package android.epicurius.ui.screens.collections

import android.content.Context
import android.epicurius.domain.collection.COLLECTION_NAME_LENGTH_MSG
import android.epicurius.domain.collection.Collection
import android.epicurius.domain.collection.CollectionProfile
import android.epicurius.domain.collection.MAX_COLLECTION_NAME_LENGTH
import android.epicurius.domain.collection.MIN_COLLECTION_NAME_LENGTH
import android.epicurius.domain.user.USERNAME_LENGTH_MSG
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
        recipeInCollection: Boolean,
        collectionsFlow: MutableStateFlow<LoadState<List<CollectionProfile>>>,
        cachedCollectionsFlow: MutableStateFlow<List<CollectionProfile>>
    ) {
        disableButtons()
        collectionsFlow.value = loading()
        viewModelScope.launch {
            fetchCollections(recipeId, recipeInCollection, collectionsFlow, cachedCollectionsFlow)
        }
    }

    fun addRecipeToCollection(collectionId: Int, recipeId: Int) {
        disableButtons()
        val addRecipeInfo = AddRecipeToCollectionInputModel(recipeId)
        viewModelScope.launch {
            handleAddRecipeToFavouriteCollection(collectionId, addRecipeInfo)
        }
    }

    fun removeRecipeFromCollection(collectionId: Int, recipeId: Int) {
        disableButtons()
        viewModelScope.launch {
            handleRemoveRecipeFromFavouriteCollection(collectionId, recipeId)
        }
    }

    private suspend fun fetchCollections(
        recipeId: Int,
        recipeInCollection: Boolean,
        collectionsFlow: MutableStateFlow<LoadState<List<CollectionProfile>>>,
        cachedCollectionsFlow: MutableStateFlow<List<CollectionProfile>>
    ) {
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
                val fetchedCollections =
                    result.getValueOrThrow().collections.mapNotNull { collection ->
                        fetchCollection(collection.id)
                    }

                val updatedCollections = if (recipeInCollection) { // collections to remove the recipe
                    fetchedCollections.filter { collection ->
                        val recipesIds = collection.recipes.map { it.id }
                        recipesIds.contains(recipeId)
                    }.map { it.toCollectionProfile() }
                }
                else { // collections to add the recipe
                    fetchedCollections.filter { collection ->
                        val recipesIds = collection.recipes.map { it.id }
                        !recipesIds.contains(recipeId)
                    }.map { it.toCollectionProfile() }
                }
                cachedCollectionsFlow.value = updatedCollections
                collectionsFlow.value = apiSuccess(updatedCollections)
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
        collectionId: Int,
        addRecipeInfo: AddRecipeToCollectionInputModel,
    ) {
        val result = request {
            val token = session.getToken()
            service.collectionService.addRecipeToCollection(token, collectionId, addRecipeInfo)
        }
        when {
            result.isFailure -> {
                enableButtons()
            }
        }
    }

    private suspend fun handleRemoveRecipeFromFavouriteCollection(
        collectionId: Int,
        recipeId: Int
    ) {
        val result = request {
            val token = session.getToken()
            service.collectionService.removeRecipeFromCollection(token, collectionId, recipeId)
        }
        when {
            result.isFailure -> {
                enableButtons()
            }
        }
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