package android.epicurius.ui.screens.user.profile

import android.content.Context
import android.epicurius.domain.collection.CollectionProfile
import android.epicurius.domain.recipe.RecipeInfo
import android.epicurius.domain.user.FollowRequestType
import android.epicurius.domain.user.FollowingStatus
import android.epicurius.domain.user.UserProfile
import android.epicurius.services.EpicuriusService
import android.epicurius.services.http.utils.CachedResult
import android.epicurius.storage.Session
import android.epicurius.ui.EpicuriusViewModel
import android.epicurius.ui.screens.utils.LoadState
import android.epicurius.ui.screens.utils.apiSuccess
import android.epicurius.ui.screens.utils.cache
import android.epicurius.ui.screens.utils.getOrThrow
import android.epicurius.ui.screens.utils.idle
import android.epicurius.ui.screens.utils.loading
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.viewModelScope
import epicurius.domain.collection.CollectionType
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.util.Base64

class UserProfileViewModel(
    service: EpicuriusService,
    session: Session,
    context: Context
): EpicuriusViewModel(service, session, context) {

    private val userProfileFlow = MutableStateFlow<LoadState<UserProfile>>(idle())
    val userProfile = userProfileFlow.asStateFlow()

    private val userRecipesFlow = MutableStateFlow<LoadState<List<RecipeInfo>>>(idle())
    private val cachedUserRecipesFlow = MutableStateFlow<List<RecipeInfo>>(emptyList())

    val userRecipes = userRecipesFlow.asStateFlow()

    private val userKitchenBookFlow = MutableStateFlow<LoadState<List<CollectionProfile>>>(idle())
    private val cachedUserKitchenBookFlow = MutableStateFlow<List<CollectionProfile>>(emptyList())

    val userKitchenBook = userKitchenBookFlow.asStateFlow()

    var isAnotherUserProfile by mutableStateOf(false)
    var userProfileVisibility by mutableStateOf(false)

    fun getUserProfile(name: String) {
        disableButtons()
        userProfileFlow.value = loading()
        viewModelScope.launch {
            checkIfIsAnotherUserProfile(name)
            fetchUserProfile(name)
        }
    }

    fun getUserRecipes(username: String?) {
        disableButtons()
        userRecipesFlow.value = loading(CachedResult<List<RecipeInfo>>(cachedUserRecipesFlow.value))
        viewModelScope.launch { fetchUserRecipes(username) }
    }

    fun getUserKitchenBook(username: String?) {
        disableButtons()
        userKitchenBookFlow.value = loading(CachedResult<List<CollectionProfile>>(cachedUserKitchenBookFlow.value))
        viewModelScope.launch { fetchUserKitchenBook(username) }
    }

    fun updateUserProfilePicture(pictureBytes: ByteArray?) {
        disableButtons()
        viewModelScope.launch { handleUpdateUserProfilePicture(pictureBytes) }
    }

    fun follow(username: String) {
        disableButtons()
        viewModelScope.launch { handleFollow(username) }
    }

    fun unfollow(username: String) {
        disableButtons()
        viewModelScope.launch { handleUnfollow(username) }
    }

    fun cancelFollow(username: String) {
        disableButtons()
        viewModelScope.launch { handleCancelFollow(username) }
    }

    fun clearUserRecipes() {
        cachedUserRecipesFlow.value = emptyList()
        userRecipesFlow.value = idle()
    }

    fun clearUserKitchenBook() {
        cachedUserKitchenBookFlow.value = emptyList()
        userKitchenBookFlow.value = idle()
    }

    private suspend fun fetchUserProfile(name: String) {
        val result = request {
            val token = session.getToken()
            service.userService.getUserProfile(token, name)
        }
        when {
            result.isSuccess -> {
                userProfileFlow.value = apiSuccess(result.getValueOrThrow().userProfile)
                checkUserProfileVisibility()
            }
        }
        enableButtons()
    }

    private suspend fun fetchUserRecipes(username: String?) {
        val result = request {
            val token = session.getToken()
            val lastRecipeId = cachedUserRecipesFlow.value.lastOrNull()?.id
            service.recipeService.getUserRecipes(token, username, lastRecipeId, limit)
        }
        when {
            result.isFailure -> handleCachedUserRecipes()
            result.isSuccess -> {
                val fetchedUserRecipes = result.getValueOrThrow().recipes
                if (fetchedUserRecipes.isNotEmpty()) {
                    val updatedUserRecipes = cachedUserRecipesFlow.value + fetchedUserRecipes
                    userRecipesFlow.value = apiSuccess(updatedUserRecipes)
                    cachedUserRecipesFlow.value = updatedUserRecipes
                }
                else handleCachedUserRecipes()
            }
        }
        enableButtons()
    }

    private fun handleCachedUserRecipes() {
        userRecipesFlow.value = cache(cachedUserRecipesFlow.value)
    }

    private suspend fun fetchUserKitchenBook(username: String?) {
        val result = request {
            val token = session.getToken()
            val lastCollectionId = cachedUserKitchenBookFlow.value.lastOrNull()?.id
            service.collectionService.getCollections(
                token,
                username,
                CollectionType.KITCHEN_BOOK,
                lastCollectionId,
                limit
            )
        }
        when {
            result.isFailure -> handleCachedUserKitchenBook()
            result.isSuccess -> {
                val fetchedUserKitchenBook = result.getValueOrThrow().collections
                if (fetchedUserKitchenBook.isNotEmpty()) {
                    val updatedUserKitchenBook = cachedUserKitchenBookFlow.value + fetchedUserKitchenBook
                    userKitchenBookFlow.value = apiSuccess(updatedUserKitchenBook)
                    cachedUserKitchenBookFlow.value = updatedUserKitchenBook
                }
                else handleCachedUserKitchenBook()
            }
        }
        enableButtons()
    }

    private fun handleCachedUserKitchenBook() {
        userKitchenBookFlow.value = cache(cachedUserKitchenBookFlow.value)
    }

    private suspend fun handleUpdateUserProfilePicture(pictureBytes: ByteArray?) {
        val result = request {
            val token = session.getToken()
            service.userService.updateUserProfilePicture(token, pictureBytes)
        }
        when {
            result.isSuccess -> {
                val oldUserProfile = userProfileFlow.value.getOrThrow()
                val newProfilePictureName = result.getValueOrNull()?.profilePictureName
                if (newProfilePictureName != null && pictureBytes != null) {
                    val updatedUserProfile = oldUserProfile.copy(profilePicture = Base64.getEncoder().encodeToString(pictureBytes))
                    userProfileFlow.value = apiSuccess(updatedUserProfile)
                    session.updateUserProfilePicture(context, newProfilePictureName, pictureBytes)
                }
                else {
                    val updatedUserProfile = oldUserProfile.copy(profilePicture = null)
                    userProfileFlow.value = apiSuccess(updatedUserProfile)
                    session.deleteProfilePicture(context)
                }
            }
        }
        enableButtons()
    }

    private suspend fun handleFollow(username: String) {
        val result = request {
            val token = session.getToken()
            service.userService.follow(token, username)
        }
        when {
            result.isSuccess -> {
                val userProfilePrivacy = userProfileFlow.value.getOrThrow().privacy
                if (userProfilePrivacy) {
                    handleChangeOnFollowingStatus(FollowingStatus.PENDING)
                    showToast("Following request sent!")
                } else {
                    handleChangeOnFollowingStatus(FollowingStatus.ACCEPTED)
                    checkUserProfileVisibility()
                }
            }
        }
        enableButtons()
    }

    private suspend fun handleUnfollow(username: String) {
        val result = request {
            val token = session.getToken()
            service.userService.unfollow(token, username)
        }
        when {
            result.isSuccess -> {
                handleChangeOnFollowingStatus(FollowingStatus.NOT_FOLLOWING)
                checkUserProfileVisibility()
            }
        }
    }

    private suspend fun handleCancelFollow(username: String) {
        val result = request {
            val token = session.getToken()
            service.userService.followRequest(token, username, FollowRequestType.CANCEL)
        }
        when {
            result.isSuccess -> {
                handleChangeOnFollowingStatus(FollowingStatus.NOT_FOLLOWING, true)
                checkUserProfileVisibility()
            }
        }
    }

    private fun handleChangeOnFollowingStatus(
        followingStatus: FollowingStatus,
        isCancelFollow: Boolean = false
    ) {
        val oldUserProfile = userProfileFlow.value.getOrThrow()
        val updatedFollowersCount =
            if (followingStatus == FollowingStatus.ACCEPTED)
                oldUserProfile.followersCount + 1
            else if (followingStatus == FollowingStatus.NOT_FOLLOWING && !isCancelFollow)
                oldUserProfile.followersCount - 1
            else oldUserProfile.followersCount
        val updatedUserProfile = oldUserProfile.copy(
            followersCount = updatedFollowersCount,
            followingStatus = followingStatus
        )
        userProfileFlow.value = apiSuccess(updatedUserProfile)
    }

    private suspend fun checkIfIsAnotherUserProfile(name: String) {
        val userName = session.getUserName()
        isAnotherUserProfile = userName != name
    }

    // only called after user profile flow is loaded
    private fun checkUserProfileVisibility() {
        userProfileVisibility =
            if (!isAnotherUserProfile) true // own profile
            else {
                if (!userProfileFlow.value.getOrThrow().privacy) true // public profile
                else if (userProfileFlow.value.getOrThrow().followingStatus == FollowingStatus.ACCEPTED) // private profile and following
                    true
                else false // private profile and not following
            }
    }
}