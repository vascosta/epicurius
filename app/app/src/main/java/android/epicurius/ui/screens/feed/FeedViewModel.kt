package android.epicurius.ui.screens.feed

import android.content.Context
import android.epicurius.domain.recipe.RecipeInfo
import android.epicurius.services.EpicuriusService
import android.epicurius.services.http.utils.CachedResult
import android.epicurius.storage.Session
import android.epicurius.ui.EpicuriusViewModel
import android.epicurius.ui.screens.collections.CollectionsViewModel
import android.epicurius.ui.screens.utils.LoadState
import android.epicurius.ui.screens.utils.apiFailure
import android.epicurius.ui.screens.utils.apiSuccess
import android.epicurius.ui.screens.utils.cache
import android.epicurius.ui.screens.utils.idle
import android.epicurius.ui.screens.utils.loading
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class FeedViewModel(
    service: EpicuriusService,
    session: Session,
    context: Context
): CollectionsViewModel(service, session, context) {

    private val userFeedFlow = MutableStateFlow<LoadState<List<RecipeInfo>>>(idle())
    private val cacheUserFeedFlow = MutableStateFlow<List<RecipeInfo>>(emptyList())
    private val lastFetchedRecipeIdFlow = MutableStateFlow<Int?>(null)

    val userFeed = userFeedFlow.asStateFlow()
    val cacheUserFeed = cacheUserFeedFlow.asStateFlow()

    fun getUserFeed() {
        userFeedFlow.value = loading(CachedResult(cacheUserFeed.value))
        viewModelScope.launch {
            fetchUserFeed()
        }
    }

    fun refreshUserFeed() {
        getUserFeed()
        // when swipe to refresh is implemented
    }

    private suspend fun fetchUserFeed() {
        val result = request {
            val token = session.getToken()
            val lastRecipeId = lastFetchedRecipeIdFlow.value
            service.userService.getUserFeed(token, lastRecipeId, limit)
        }
        when {
            result.isSuccess -> {
                val fetchedFeed = result.getValueOrThrow().feed
                if (fetchedFeed.isNotEmpty()) {
                    val updatedFeed = cacheUserFeed.value + fetchedFeed
                    userFeedFlow.value = apiSuccess(updatedFeed)
                    cacheUserFeedFlow.value = updatedFeed
                    lastFetchedRecipeIdFlow.value = fetchedFeed.last().id
                }
                else {
                    userFeedFlow.value = cache(cacheUserFeed.value)
                }
            }
        }
    }
}