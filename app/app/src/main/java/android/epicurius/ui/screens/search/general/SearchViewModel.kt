package android.epicurius.ui.screens.search.general

import android.content.Context
import android.epicurius.domain.user.SearchUser
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

class SearchViewModel(
    service: EpicuriusService,
    session: Session,
    context: Context
): EpicuriusViewModel(service, session, context) {

    private val searchedUsersFlow = MutableStateFlow<LoadState<List<SearchUser>>>(idle())
    private val cacheSearchedUsersFlow = MutableStateFlow<List<SearchUser>>(emptyList())
    private val lastFetchedUserIdFlow = MutableStateFlow<Int?>(null)

    val searchedUsers = searchedUsersFlow.asStateFlow()
    private val cacheSearchedUsers = cacheSearchedUsersFlow.asStateFlow()

    fun searchUsers(name: String) {
        disableButtons()
        searchedUsersFlow.value = loading(CachedResult(cacheSearchedUsers.value))
        viewModelScope.launch { fetchUsers(name) }
    }

    fun identifyIngredientsOnPicture() {

    }

    fun clearSearchUsers() {
        searchedUsersFlow.value = idle()
        cacheSearchedUsersFlow.value = emptyList()
        lastFetchedUserIdFlow.value = null
    }

    private suspend fun fetchUsers(name: String) {
        val result = request {
            val token = session.getToken()
            val lastUserId = lastFetchedUserIdFlow.value
            service.userService.searchUsers(
                token,
                name,
                lastUserId,
                limit
            )
        }
        when {
            result.isFailure -> searchedUsersFlow.value = cache(cacheSearchedUsers.value)
            result.isSuccess -> {
                val fetchedUsers = result.getValueOrThrow().users

                if (fetchedUsers.isNotEmpty()) {
                    val updatedSearchedUsers = cacheSearchedUsers.value + fetchedUsers
                    searchedUsersFlow.value = apiSuccess(updatedSearchedUsers)
                    cacheSearchedUsersFlow.value = updatedSearchedUsers
                    lastFetchedUserIdFlow.value = fetchedUsers.last().id
                }
                else searchedUsersFlow.value = cache(cacheSearchedUsers.value)
            }
        }
        enableButtons()
    }
}