package android.epicurius.ui.screens.collections.collection

import android.content.Context
import android.epicurius.domain.collection.validateName
import android.epicurius.domain.recipe.RecipeInfo
import android.epicurius.services.EpicuriusService
import android.epicurius.storage.Session
import android.epicurius.ui.EpicuriusViewModel
import android.epicurius.ui.screens.utils.LoadState
import android.epicurius.ui.screens.utils.apiSuccess
import android.epicurius.ui.screens.utils.idle
import android.epicurius.ui.screens.utils.loading
import androidx.lifecycle.viewModelScope
import epicurius.http.controllers.collection.models.input.UpdateCollectionInputModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

open class CollectionViewModel(
    service: EpicuriusService,
    session: Session,
    context: Context
): EpicuriusViewModel(service, session, context) {

    private val collectionNameFlow = MutableStateFlow<LoadState<String>>(idle())
    val collectionName = collectionNameFlow.asStateFlow()

    private val collectionRecipesFlow = MutableStateFlow<LoadState<List<RecipeInfo>>>(idle())
    val collectionRecipes = collectionRecipesFlow.asStateFlow()

    fun getCollection(id: Int, onErrorNavigateTo: () -> Unit) {
        disableButtons()
        collectionNameFlow.value = loading()
        collectionRecipesFlow.value = loading()
        if (id == -1) {
            showToast("Missing COLLECTION_ID in intent")
            onErrorNavigateTo()
            return
        }
        viewModelScope.launch { fetchCollection(id, onErrorNavigateTo) }
    }

    fun updateCollection(id: Int, name: String, onErrorNavigateTo: () -> Unit) {
        disableButtons()
        if (id == -1) {
            showToast("Missing COLLECTION_ID in intent")
            onErrorNavigateTo()
            return
        }
        if (!validateName(name, ::showToast)) {
            enableButtons()
            return
        }
        val updateCollectionInfo = UpdateCollectionInputModel(name)
        viewModelScope.launch { handleUpdateCollection(id, updateCollectionInfo) }
    }

    fun removeRecipeFromCollection(collectionId: Int, recipeId: Int) {
        disableButtons()
        viewModelScope.launch {
            handleRemoveRecipeFromFavouriteCollection(collectionId, recipeId)
        }
    }

    fun deleteCollection(id: Int, onSuccessNavigateTo: () -> Unit) {
        disableButtons()
        viewModelScope.launch { handleDeleteCollection(id, onSuccessNavigateTo) }
    }

    private suspend fun fetchCollection(id: Int, onErrorNavigateTo: () -> Unit) {
        val result = request {
            val token = session.getToken()
            service.collectionService.getCollection(token, id)
        }
        when {
            result.isFailure -> onErrorNavigateTo()
            result.isSuccess -> {
                val fetchedCollection = result.getValueOrThrow().collection
                collectionRecipesFlow.value = apiSuccess(fetchedCollection.recipes)
                collectionNameFlow.value = apiSuccess(fetchedCollection.name)
                enableButtons()
            }
        }
    }

    private suspend fun handleUpdateCollection(
        id: Int,
        updateCollectionInfo: UpdateCollectionInputModel
    ) {
        val result = request {
            val token = session.getToken()
            service.collectionService.updateCollection(token, id, updateCollectionInfo)
        }
        when {
            result.isSuccess ->
                collectionNameFlow.value = apiSuccess(result.getValueOrThrow().collection.name)
        }
        enableButtons()
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
            result.isSuccess ->
                collectionRecipesFlow.value =
                    apiSuccess(result.getValueOrThrow().collection.recipes)
        }
        enableButtons()
    }

    private suspend fun handleDeleteCollection(id: Int, onSuccessNavigateTo: () -> Unit) {
        val result = request {
            val token = session.getToken()
            service.collectionService.deleteCollection(token, id)
        }
        when {
            result.isFailure -> enableButtons()
            result.isSuccess -> onSuccessNavigateTo()
        }
    }
}