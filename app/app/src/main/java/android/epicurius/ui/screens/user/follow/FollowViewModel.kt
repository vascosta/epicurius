package android.epicurius.ui.screens.user.follow

import android.content.Context
import android.epicurius.domain.user.FollowUser
import android.epicurius.domain.user.FollowingUser
import android.epicurius.domain.user.SearchUser
import android.epicurius.services.EpicuriusService
import android.epicurius.services.http.utils.CachedResult
import android.epicurius.storage.Session
import android.epicurius.ui.EpicuriusViewModel
import android.epicurius.ui.screens.utils.LoadState
import android.epicurius.ui.screens.utils.apiSuccess
import android.epicurius.ui.screens.utils.idle
import android.epicurius.ui.screens.utils.loading
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlin.collections.plus

class FollowViewModel(
    service: EpicuriusService,
    session: Session,
    context: Context
): EpicuriusViewModel(service, session, context) {

    private val followersFlow = MutableStateFlow<LoadState<List<FollowUser>>>(idle())
    private val cacheFollowersFlow = MutableStateFlow<List<FollowUser>>(emptyList())

    private val followingFlow = MutableStateFlow<LoadState<List<FollowingUser>>>(idle())
    private val cacheFollowingFlow = MutableStateFlow<List<FollowUser>>(emptyList())

    val followers = followersFlow.asStateFlow()
    val following = followingFlow.asStateFlow()

    private val searchUsersFlow = MutableStateFlow<LoadState<List<SearchUser>>>(idle())
    private val cacheSearchUsersFlow = MutableStateFlow<List<SearchUser>>(emptyList())

    val searchedUsers = searchUsersFlow.asStateFlow()

    fun getFollowers(username: String, partialFollowerName: String?) {
        disableButtons()
        if (partialFollowerName != null)
            searchUsersFlow.value = loading(CachedResult(cacheSearchUsersFlow.value))
        else
            followersFlow.value = loading(CachedResult(cacheFollowersFlow.value))
        viewModelScope.launch { fetchFollowers(username, partialFollowerName) }
    }

    fun getFollowing(username: String, partialFollowingName: String?) {
        disableButtons()
        if (partialFollowingName != null)
            searchUsersFlow.value = loading(CachedResult(cacheSearchUsersFlow.value))
        else
            followingFlow.value = loading(CachedResult(cacheFollowingFlow.value))
        viewModelScope.launch { fetchFollowing(username, partialFollowingName) }
    }

    fun clearSearchUsers() {
        searchUsersFlow.value = idle()
        cacheSearchUsersFlow.value = emptyList()
    }

    private suspend fun fetchFollowers(username: String, partialFollowerName: String?) {
        val result = request {
            val token = session.getToken()
            val lastFollowerId = if (partialFollowerName != null) cacheSearchUsersFlow.value.lastOrNull()?.id
                else cacheFollowersFlow.value.lastOrNull()?.id
            service.userService.getUserFollowers(
                token,
                username,
                partialFollowerName,
                lastFollowerId,
                limit
            )
        }
        when {
            result.isFailure -> {
                if (partialFollowerName != null) handleCachedSearchUsers()
                else handleCachedFollowers()
            }
            result.isSuccess -> {
                val fetchedFollowers = result.getValueOrThrow().users
                if (partialFollowerName != null) {
                    if (fetchedFollowers.isNotEmpty()) handleSearchUsers(fetchedFollowers)
                    else handleCachedSearchUsers()
                }
                else {
                    if (fetchedFollowers.isNotEmpty()) {
                        val updatedFollowers = cacheFollowersFlow.value + fetchedFollowers
                        followersFlow.value = apiSuccess(updatedFollowers)
                        cacheFollowersFlow.value = updatedFollowers
                    }
                    else handleCachedFollowers()
                }
            }
        }
        enableButtons()
    }

    private fun handleCachedFollowers() { followersFlow.value = apiSuccess(cacheFollowersFlow.value) }

    private suspend fun fetchFollowing(username: String, partialFollowingName: String?) {
        val result = request {
            val token = session.getToken()
            val lastFollowingId = if (partialFollowingName != null) cacheSearchUsersFlow.value.lastOrNull()?.id
            else cacheFollowingFlow.value.lastOrNull()?.id
            service.userService.getUserFollowing(
                token,
                username,
                partialFollowingName,
                lastFollowingId,
                limit
            )
        }
        when {
            result.isFailure -> {
                if (partialFollowingName != null) handleCachedSearchUsers()
                else handleCachedFollowing()
            }
            result.isSuccess -> {
                val fetchedFollowing = result.getValueOrThrow().users
                if (partialFollowingName != null) {
                    if (fetchedFollowing.isNotEmpty()) handleSearchUsers(fetchedFollowing)
                    else handleCachedSearchUsers()
                }
                else {
                    if (fetchedFollowing.isNotEmpty()) {
                        val updatedFollowing = cacheFollowingFlow.value + fetchedFollowing
                        followingFlow.value = apiSuccess(updatedFollowing)
                        cacheFollowingFlow.value = updatedFollowing
                    } else handleCachedFollowing()
                }
            }
        }
        enableButtons()
    }

    private fun handleCachedFollowing() { followingFlow.value = apiSuccess(cacheFollowingFlow.value) }

    private fun handleCachedSearchUsers() { searchUsersFlow.value = apiSuccess(cacheSearchUsersFlow.value) }

    private fun handleSearchUsers(fetchedUsers: List<SearchUser>) {
        val updatedSearchedUsers = cacheSearchUsersFlow.value + fetchedUsers
        searchUsersFlow.value = apiSuccess(updatedSearchedUsers)
        cacheSearchUsersFlow.value = updatedSearchedUsers
    }
}