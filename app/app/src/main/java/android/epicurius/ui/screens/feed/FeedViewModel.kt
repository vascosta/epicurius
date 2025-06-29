package android.epicurius.ui.screens.feed

import android.content.Context
import android.epicurius.domain.recipe.RecipeInfo
import android.epicurius.domain.user.FollowRequestType
import android.epicurius.domain.user.SearchUser
import android.epicurius.services.EpicuriusService
import android.epicurius.services.http.utils.CachedResult
import android.epicurius.storage.Session
import android.epicurius.ui.screens.collections.recipeCollections.RecipeCollectionsViewModel
import android.epicurius.ui.screens.utils.LoadState
import android.epicurius.ui.screens.utils.apiSuccess
import android.epicurius.ui.screens.utils.cache
import android.epicurius.ui.screens.utils.idle
import android.epicurius.ui.screens.utils.loading
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class FeedViewModel(
    service: EpicuriusService,
    session: Session,
    context: Context
): RecipeCollectionsViewModel(service, session, context) {

    private val userFeedFlow = MutableStateFlow<LoadState<List<RecipeInfo>>>(idle())
    private val cacheUserFeedFlow = MutableStateFlow<List<RecipeInfo>>(emptyList())
    private val userFollowRequestsFlow = MutableStateFlow<LoadState<List<SearchUser>>>(idle())
    private val cacheUserFollowRequestsFlow = MutableStateFlow<List<SearchUser>>(emptyList())
    private val lastFetchedRecipeIdFlow = MutableStateFlow<Int?>(null)

    val userFeed = userFeedFlow.asStateFlow()
    val userFollowRequests = userFollowRequestsFlow.asStateFlow()

    fun getUserFeed() {
        disableButtons()
        userFeedFlow.value = loading(CachedResult(cacheUserFeedFlow.value))
        viewModelScope.launch { fetchUserFeed() }
    }

    fun getUserFollowRequests() {
        disableButtons()
        userFollowRequestsFlow.value = loading(CachedResult(cacheUserFollowRequestsFlow.value))
        viewModelScope.launch { fetchUserFollowRequests() }
    }

    fun acceptFollowRequest(name: String) {
        disableButtons()
        viewModelScope.launch { handleFollowRequest(name, FollowRequestType.ACCEPT) }
    }

    fun rejectFollowRequest(name: String) {
        disableButtons()
        viewModelScope.launch { handleFollowRequest(name, FollowRequestType.REJECT) }
    }

    private suspend fun fetchUserFeed() {
        val result = request {
            val token = session.getToken()
            val lastRecipeId = lastFetchedRecipeIdFlow.value
            service.userService.getUserFeed(token, lastRecipeId, limit)
        }
        when {
            result.isFailure -> handleCachedUserFeed()
            result.isSuccess -> {
                val fetchedFeed = result.getValueOrThrow().feed
                if (fetchedFeed.isNotEmpty()) {
                    val updatedFeed = cacheUserFeedFlow.value + fetchedFeed
                    userFeedFlow.value = apiSuccess(updatedFeed)
                    cacheUserFeedFlow.value = updatedFeed
                    lastFetchedRecipeIdFlow.value = fetchedFeed.last().id
                }
                else handleCachedUserFeed()
            }
        }
        enableButtons()
    }

    private fun handleCachedUserFeed() { userFeedFlow.value = cache(cacheUserFeedFlow.value) }

    private suspend fun fetchUserFollowRequests() {
        val result = request {
            val token = session.getToken()
            service.userService.getUserFollowRequests(token)
        }
        when {
            result.isFailure -> userFollowRequestsFlow.value = cache(cacheUserFollowRequestsFlow.value)
            result.isSuccess -> {
                val fetchedUserFollowRequests = result.getValueOrThrow().users
                val updatedUserFollowRequests = cacheUserFollowRequestsFlow.value + fetchedUserFollowRequests
                userFollowRequestsFlow.value = apiSuccess(updatedUserFollowRequests)
                cacheUserFollowRequestsFlow.value = updatedUserFollowRequests
            }
        }
        enableButtons()
    }

    private suspend fun handleFollowRequest(
        name: String,
        followRequestType: FollowRequestType
    ) {
        val result = request {
            val token = session.getToken()
            service.userService.followRequest(token, name, followRequestType)
        }
        when {
            result.isSuccess -> {
                val updatedUserFollowRequests = cacheUserFollowRequestsFlow.value.filter {
                    it.name != name
                }
                userFollowRequestsFlow.value = apiSuccess(updatedUserFollowRequests)
                cacheUserFollowRequestsFlow.value = updatedUserFollowRequests
            }
        }
        enableButtons()
    }
}