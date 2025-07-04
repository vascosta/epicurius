package android.epicurius.ui.screens.search

import android.content.Context
import android.epicurius.domain.user.SearchUser
import android.epicurius.domain.user.UserInfo
import android.epicurius.services.EpicuriusService
import android.epicurius.services.http.utils.CachedResult
import android.epicurius.storage.Session
import android.epicurius.ui.EpicuriusViewModel
import android.epicurius.ui.screens.utils.LoadState
import android.epicurius.ui.screens.utils.apiSuccess
import android.epicurius.ui.screens.utils.cache
import android.epicurius.ui.screens.utils.idle
import android.epicurius.ui.screens.utils.loading
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class SearchUsersViewModel(
    service: EpicuriusService,
    session: Session,
    context: Context
): EpicuriusViewModel(service, session, context) {

    private val searchedUsersFlow = MutableStateFlow<LoadState<List<SearchUser>>>(idle())
    private val cacheSearchedUsersFlow = MutableStateFlow<List<SearchUser>>(emptyList())

    val searchedUsers = searchedUsersFlow.asStateFlow()

    private val userInfoFlow = MutableStateFlow<LoadState<UserInfo>>(idle())
    val userInfo = userInfoFlow.asStateFlow()

    fun searchUsers(name: String) {
        disableButtons()
        searchedUsersFlow.value = loading(CachedResult(cacheSearchedUsersFlow.value))
        viewModelScope.launch { fetchUsers(name) }
    }

    fun getUserInfo() {
        disableButtons()
        userInfoFlow.value = loading()
        viewModelScope.launch { getCachedUserInfo() }
    }

    fun clearSearchUsers() {
        searchedUsersFlow.value = idle()
        cacheSearchedUsersFlow.value = emptyList()
    }

    private suspend fun fetchUsers(name: String) {
        val result = request {
            val token = session.getToken()
            val lastUserId = cacheSearchedUsersFlow.value.lastOrNull()?.id
            service.userService.searchUsers(
                token,
                name,
                lastUserId,
                limit
            )
        }
        when {
            result.isFailure -> handleCachedSearchedUsers()
            result.isSuccess -> {
                val fetchedUsers = result.getValueOrThrow().users

                if (fetchedUsers.isNotEmpty()) {
                    val updatedSearchedUsers = cacheSearchedUsersFlow.value + fetchedUsers
                    searchedUsersFlow.value = apiSuccess(updatedSearchedUsers)
                    cacheSearchedUsersFlow.value = updatedSearchedUsers
                }
                else handleCachedSearchedUsers()
            }
        }
        enableButtons()
    }

    private fun handleCachedSearchedUsers() {
        searchedUsersFlow.value = cache(cacheSearchedUsersFlow.value)
    }

    private suspend fun getCachedUserInfo() {
        val userInfo = session.getUserInfo()
        userInfoFlow.value = cache(userInfo)
        enableButtons()
    }
}