package android.epicurius.ui.screens.feed

import android.content.Context
import android.epicurius.domain.recipe.RecipeInfo
import android.epicurius.services.EpicuriusService
import android.epicurius.storage.Session
import android.epicurius.ui.EpicuriusViewModel
import android.epicurius.ui.screens.utils.LoadState
import android.epicurius.ui.screens.utils.apiFailure
import android.epicurius.ui.screens.utils.apiSuccess
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
): EpicuriusViewModel(service, session, context) {

    private val userFeedFlow = MutableStateFlow<LoadState<List<RecipeInfo>>>(idle())
    val userFeed = userFeedFlow.asStateFlow()

    var skip by mutableIntStateOf(0)
        private set

    var limit by mutableIntStateOf(10)

    fun getUserFeed() {
        userFeedFlow.value = loading()
        viewModelScope.launch {
            fetchUserFeed()
        }
    }

    fun refreshUserFeed() {
        resetSkip()
        getUserFeed()
    }

    private suspend fun fetchUserFeed() {
        val result = request {
            val token = session.getToken()
            service.userService.getUserFeed(token, skip, limit)
        }
        when {
            result.isFailure -> {
                userFeedFlow.value = apiFailure(result.getProblemOrThrow())
            }
            result.isSuccess -> {
                userFeedFlow.value = apiSuccess(result.getValueOrThrow().feed)
                increaseSkip()
            }
        }
    }

    private fun increaseSkip() {
        skip += 10
    }

    private fun resetSkip() {
        skip = 0
    }
}