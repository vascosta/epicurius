package android.epicurius.ui.screens.collections.favourites.list

import android.content.Context
import android.epicurius.domain.recipe.RecipeInfo
import android.epicurius.services.EpicuriusService
import android.epicurius.storage.Session
import android.epicurius.ui.screens.collections.CollectionsViewModel
import android.epicurius.ui.screens.utils.LoadState
import android.epicurius.ui.screens.utils.apiFailure
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

    var deleteCollectionEnable by mutableStateOf(true)
        private set

    var updateCollectionEnable by mutableStateOf(true)
        private set

    var removeRecipeFromCollectionEnable by mutableStateOf(true)
        private set

    fun getCollection(id: Int, navigateTo: () -> Unit) {
        if (id == -1) {
            showToast("Missing RECIPE_ID in intent")
            navigateTo()
            return
        }
        viewModelScope.launch {
            fetchCollection(id)
        }
    }

    fun deleteFavouriteCollection(id: Int, navigateTo: () -> Unit) {
        disableButtons()
        viewModelScope.launch {
            handleDeleteFavouriteCollection(id, navigateTo)
        }
    }

    fun updateFavouriteCollection(id: Int, name: String) {
        disableButtons()
        favouritesListNameFlow.value = loading()
        if (!validateCollectionName(name)) {
            enableButtons()
            return
        }
        val updateCollectionInfo = UpdateCollectionInputModel(name)
        viewModelScope.launch {
            handleUpdateFavouriteCollection(id, updateCollectionInfo)
        }
    }

    fun removeRecipeFromFavouriteCollection(id: Int, recipeId: Int) {
        disableButtons()
        recipesFlow.value = loading()
        viewModelScope.launch {
            handleRemoveRecipeFromFavouriteCollection(id, recipeId)
        }
    }

    private suspend fun fetchCollection(id: Int) {
        val result = request {
            val token = session.getToken()
            service.collectionService.getCollection(token, id)
        }
        when {
            result.isFailure -> {
                recipesFlow.value = apiFailure(result.getProblemOrThrow())
                favouritesListNameFlow.value = apiFailure(result.getProblemOrThrow())
            }
            result.isSuccess -> {
                recipesFlow.value = apiSuccess(result.getValueOrThrow().collection.recipes)
                favouritesListNameFlow.value = apiSuccess(result.getValueOrThrow().collection.name)
            }
        }
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
                recipesFlow.value = apiFailure(result.getProblemOrThrow())
                enableButtons()
            }
            result.isSuccess -> {
                navigateTo()
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
            result.isFailure -> {
                favouritesListNameFlow.value = apiFailure(result.getProblemOrThrow())
            }
            result.isSuccess -> {
                favouritesListNameFlow.value = apiSuccess(result.getValueOrThrow().collection.name)
            }
        }
        enableButtons()
    }

    private suspend fun handleRemoveRecipeFromFavouriteCollection(
        id: Int,
        recipeId: Int
    ) {
        val result = request {
            val token = session.getToken()
            service.collectionService.removeRecipeFromCollection(token, id, recipeId)
        }
        when {
            result.isFailure -> {
                recipesFlow.value = apiFailure(result.getProblemOrThrow())
                enableDeleteCollection()
            }
            result.isSuccess -> {
                recipesFlow.value = apiSuccess(result.getValueOrThrow().collection.recipes)
            }
        }
        enableButtons()
    }

    private fun enableDeleteCollection() {
        deleteCollectionEnable = true
    }

    private fun disableDeleteCollection() {
        deleteCollectionEnable = false
    }

    private fun enableUpdateCollection() {
        updateCollectionEnable = true
    }

    private fun disableUpdateCollection() {
        updateCollectionEnable = false
    }

    private fun enableRemoveRecipeFromCollection() {
        removeRecipeFromCollectionEnable = true
    }

    private fun disableRemoveRecipeFromCollection() {
        removeRecipeFromCollectionEnable = false
    }

    private fun enableButtons() {
        enableDeleteCollection()
        enableUpdateCollection()
        enableRemoveRecipeFromCollection()
    }

    private fun disableButtons() {
        disableDeleteCollection()
        disableUpdateCollection()
        disableRemoveRecipeFromCollection()
    }
}