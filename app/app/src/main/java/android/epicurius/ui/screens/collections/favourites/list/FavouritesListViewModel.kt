package android.epicurius.ui.screens.collections.favourites.list

import android.content.Context
import android.epicurius.domain.recipe.RecipeInfo
import android.epicurius.services.EpicuriusService
import android.epicurius.storage.Session
import android.epicurius.ui.screens.collections.CollectionsViewModel
import android.epicurius.ui.screens.utils.LoadState
import android.epicurius.ui.screens.utils.apiSuccess
import android.epicurius.ui.screens.utils.idle
import android.epicurius.ui.screens.utils.loading
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.viewModelScope
import epicurius.http.controllers.collection.models.input.UpdateCollectionInputModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class FavouritesListViewModel(
    service: EpicuriusService,
    session: Session,
    context: Context
): CollectionsViewModel(service, session, context) {

    private val recipesFlow = MutableStateFlow<LoadState<List<RecipeInfo>>>(idle())
    private val favouritesListNameFlow = MutableStateFlow<LoadState<String>>(idle())
    val recipes = recipesFlow.asStateFlow()
    val favouritesListName = favouritesListNameFlow.asStateFlow()

    fun getFavouriteCollection(id: Int, navigateTo: () -> Unit) {
        recipesFlow.value = loading()
        favouritesListNameFlow.value = loading()
        if (id == -1) {
            showToast("Missing COLLECTION_ID in intent")
            navigateTo()
            return
        }
        viewModelScope.launch {
            fetchFavouriteCollection(id, navigateTo)
        }
    }

    fun updateFavouriteCollection(id: Int, name: String, navigateTo: () -> Unit) {
        disableButtons()
        if (id == -1) {
            showToast("Missing COLLECTION_ID in intent")
            navigateTo()
            return
        }
        if (!validateCollectionName(name)) {
            enableButtons()
            return
        }
        val updateCollectionInfo = UpdateCollectionInputModel(name)
        viewModelScope.launch {
            handleUpdateFavouriteCollection(id, updateCollectionInfo)
        }
    }

    fun deleteFavouriteCollection(id: Int, navigateTo: () -> Unit) {
        disableButtons()
        viewModelScope.launch {
            handleDeleteFavouriteCollection(id, navigateTo)
        }
    }

    fun removeRecipeFromFavouriteCollection(collectionId: Int, recipeId: Int) {
        disableButtons()
        viewModelScope.launch {
            handleRemoveRecipeFromFavouriteCollection(collectionId, recipeId)
        }
    }

    private suspend fun fetchFavouriteCollection(id: Int, navigateTo: () -> Unit) {
        val result = request {
            val token = session.getToken()
            service.collectionService.getCollection(token, id)
        }
        when {
            result.isFailure -> {
                navigateTo()
            }
            result.isSuccess -> {
                val fetchedResult = result.getValueOrThrow().collection
                recipesFlow.value = apiSuccess(fetchedResult.recipes)
                favouritesListNameFlow.value = apiSuccess(fetchedResult.name)
            }
        }
    }

    private suspend fun handleUpdateFavouriteCollection(
        id: Int,
        updateCollectionInfo: UpdateCollectionInputModel,
    ) {
        val result = request {
            val token = session.getToken()
            service.collectionService.updateCollection(token, id, updateCollectionInfo)
        }
        when {
            result.isSuccess -> {
                favouritesListNameFlow.value = apiSuccess(result.getValueOrThrow().collection.name)
            }
        }
        enableButtons()
    }

    private suspend fun handleDeleteFavouriteCollection(
        id: Int,
        navigateTo: () -> Unit
    ) {
        val result = request {
            val token = session.getToken()
            service.collectionService.deleteCollection(token, id)
        }
        when {
            result.isFailure -> {
                enableButtons()
            }
            result.isSuccess -> {
                navigateTo()
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
            result.isSuccess -> {
                recipesFlow.value = apiSuccess(result.getValueOrThrow().collection.recipes)
            }
        }
        enableButtons()
    }
}