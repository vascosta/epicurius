package android.epicurius.ui.screens.user.profile

import android.content.Context
import android.epicurius.domain.Picture
import android.epicurius.domain.collection.CollectionProfile
import android.epicurius.domain.recipe.RecipeInfo
import android.epicurius.domain.user.FollowUser
import android.epicurius.domain.user.FollowingUser
import android.epicurius.domain.user.UserProfile
import android.epicurius.services.EpicuriusService
import android.epicurius.services.api.collection.models.input.AddRecipeToCollectionInputModel
import android.epicurius.services.http.utils.APIResult
import android.epicurius.storage.Session
import android.epicurius.ui.EpicuriusViewModel
import android.epicurius.ui.screens.utils.LoadState
import android.epicurius.ui.screens.utils.apiFailure
import android.epicurius.ui.screens.utils.apiSuccess
import android.epicurius.ui.screens.utils.getOrThrow
import android.epicurius.ui.screens.utils.idle
import android.epicurius.ui.screens.utils.loaded
import android.epicurius.ui.screens.utils.loading
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.viewModelScope
import epicurius.domain.collection.CollectionType
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class UserProfileViewModel(
    service: EpicuriusService,
    session: Session,
    context: Context
): EpicuriusViewModel(service, session, context) {

    private val userProfileFlow = MutableStateFlow<LoadState<UserProfile>>(idle())
    private val userRecipesFlow = MutableStateFlow<LoadState<List<RecipeInfo>>>(idle())
    private val userKitchenBookFlow = MutableStateFlow<LoadState<List<CollectionProfile>>>(idle())
    private val kitchenBookCollectionRecipesFlow = MutableStateFlow<LoadState<List<RecipeInfo>>>(idle())
    private val userFollowersFlow = MutableStateFlow<LoadState<List<FollowUser>>>(idle())
    private val userFollowingFlow = MutableStateFlow<LoadState<List<FollowingUser>>>(idle())

    private val cachedUserRecipes = MutableStateFlow<List<RecipeInfo>>(emptyList())
    private val cachedUserKitchenBookFlow = MutableStateFlow<List<CollectionProfile>>(emptyList())
    private val cachedUserFollowersFlow = MutableStateFlow<List<FollowUser>>(emptyList())
    private val cachedUserFollowingFlow = MutableStateFlow<List<FollowingUser>>(emptyList())

    val userProfile = userProfileFlow.asStateFlow()
    val userRecipes = userRecipesFlow.asStateFlow()
    val userKitchenBook = userKitchenBookFlow.asStateFlow()
    val kitchenBookCollectionRecipes = kitchenBookCollectionRecipesFlow.asStateFlow()
    val userFollowers = userFollowersFlow.asStateFlow()
    val userFollowing = userFollowingFlow.asStateFlow()

    var isAnotherUserProfile by mutableStateOf(true)

    var userProfileVisibility by mutableStateOf(true)

    var followEnable by mutableStateOf(true)

    var limit by mutableIntStateOf(10)

    fun getUserProfile(name: String) {
        userProfileFlow.value = loading()
        viewModelScope.launch {
            checkIfIsAnotherUserProfile(name)
            fetchUserProfile(name)
            checkUserVisibility(name, userProfile.value.getOrThrow())
        }
    }

    fun getUserRecipes() {
        userRecipesFlow.value = loaded(CachedResult<List<RecipeInfo>>(cachedUserRecipes.value))
        viewModelScope.launch {
            fetchUserRecipes()
        }
    }

    fun getUserKitchenBook() {
        userKitchenBookFlow.value = loaded(CachedResult<List<CollectionProfile>>(cachedUserKitchenBookFlow.value))
        viewModelScope.launch {
            fetchUserKitchenBook()
        }
    }

    fun getKitchenBookCollectionRecipes(id: Int) {
        disableFollow()
        viewModelScope.launch {
            fetchUserKitchenBookCollectionRecipes(id)
        }
    }

    fun getUserFollowers() {
        userFollowersFlow.value = loaded(CachedResult<List<FollowUser>>(cachedUserFollowersFlow.value))
        viewModelScope.launch {
            fetchUserFollowers()
        }
    }

    fun getUserFollowing() {
        userFollowingFlow.value = loaded(CachedResult<List<FollowUser>>(cachedUserFollowingFlow.value))
        viewModelScope.launch {
            fetchUserFollowing()
        }
    }

    fun follow(name: String) {
        disableFollow()
        viewModelScope.launch {
            handleFollow(name)
        }
    }

    fun unfollow(name: String) {
        disableFollow()
        viewModelScope.launch {
            handleUnfollow(name)
        }
    }

    fun updateUserProfilePicture(picture: Picture) {
        disableFollow()
        viewModelScope.launch {
            handleUpdateUserProfilePicture(picture)
        }
    }

    fun addRecipeToKitchenBookCollection(collectionId: Int, recipeId: Int) {
        disableFollow()
        viewModelScope.launch {
            val addRecipeInfo = AddRecipeToCollectionInputModel(recipeId)
            handleAddRecipeToKitchenBookCollection(collectionId, addRecipeInfo)
        }
    }

    fun refreshUserProfile(name: String) {
        getUserProfile(name)
        getUserRecipes()
        getUserKitchenBook()
    }

    private suspend fun fetchUserProfile(name: String) {
        val result = request {
            val token = session.getToken()
            service.userService.getUserProfile(token, name)
        }
        when {
            result.isFailure -> {
                userProfileFlow.value = apiFailure(result.getProblemOrThrow())
            }
            result.isSuccess -> {
                userProfileFlow.value = apiSuccess(result.getValueOrThrow().userProfile)
            }
        }
    }

    private suspend fun fetchUserRecipes() {
        val result = request {
            val token = session.getToken()
            val lastRecipeId = cachedUserRecipes.value.lastOrNull()?.id
            service.recipeService.getUserRecipes(token, lastRecipeId, limit)
        }
        when {
            result.isFailure -> {
                userRecipesFlow.value = apiFailure(result.getProblemOrThrow())
            }
            result.isSuccess -> {
                val fetchedUserRecipes = result.getValueOrThrow().recipes
                val updatedUserRecipes = (cachedUserRecipes.value + fetchedUserRecipes).distinctBy { it.id }
                cachedUserRecipes.value = updatedUserRecipes
                userRecipesFlow.value = apiSuccess(updatedUserRecipes)
            }
        }
    }

    private suspend fun fetchUserKitchenBook() {
        val result = request {
            val token = session.getToken()
            val lastCollectionId = cachedUserKitchenBookFlow.value.lastOrNull()?.id
            service.collectionService.getCollections(token, CollectionType.KITCHEN_BOOK, lastCollectionId, limit)
        }
        when {
            result.isFailure -> {
                userKitchenBookFlow.value = apiFailure(result.getProblemOrThrow())
            }
            result.isSuccess -> {
                val fetchedUserKitchenBook = result.getValueOrThrow().collections
                val updatedUserKitchenBook = (cachedUserKitchenBookFlow.value + fetchedUserKitchenBook).distinctBy { it.id }
                cachedUserKitchenBookFlow.value = updatedUserKitchenBook
                userKitchenBookFlow.value = apiSuccess(updatedUserKitchenBook)
            }
        }
    }

    private suspend fun fetchUserKitchenBookCollectionRecipes(id: Int) {
        val result = request {
            val token = session.getToken()
            service.collectionService.getCollection(token, id)
        }
        when {
            result.isFailure -> {
                kitchenBookCollectionRecipesFlow.value = apiFailure(result.getProblemOrThrow())
            }
            result.isSuccess -> {
                kitchenBookCollectionRecipesFlow.value = apiSuccess(result.getValueOrThrow().collection.recipes)
            }
        }
        enableFollow()
    }

    private suspend fun fetchUserFollowers() {
        val result = request {
            val token = session.getToken()
            val lastFollowerId = cachedUserFollowersFlow.value.lastOrNull()?.id
            service.userService.getUserFollowers(token, lastFollowerId, limit)
        }
        when {
            result.isFailure -> {
                userFollowersFlow.value = apiFailure(result.getProblemOrThrow())
            }
            result.isSuccess -> {
                val fetchedUserFollowers = result.getValueOrThrow().users
                val updatedUserFollowers = (cachedUserFollowersFlow.value + fetchedUserFollowers).distinctBy { it.id }
                cachedUserFollowersFlow.value = updatedUserFollowers
                userFollowersFlow.value = apiSuccess(updatedUserFollowers)
            }
        }
    }

    private suspend fun fetchUserFollowing() {
        val result = request {
            val token = session.getToken()
            val lastFollowingId = cachedUserFollowersFlow.value.lastOrNull()?.id
            service.userService.getUserFollowing(token, lastFollowingId, limit)
        }
        when {
            result.isFailure -> {
                userFollowersFlow.value = apiFailure(result.getProblemOrThrow())
            }
            result.isSuccess -> {
                val fetchedUserFollowing = result.getValueOrThrow().users
                val updatedUserFollowing = (cachedUserFollowingFlow.value + fetchedUserFollowing).distinctBy { it.id }
                cachedUserFollowingFlow.value = updatedUserFollowing
                userFollowingFlow.value = apiSuccess(updatedUserFollowing)
            }
        }
    }

    private suspend fun handleFollow(name: String) {
        val result = request {
            val token = session.getToken()
            service.userService.follow(token, name)
        }
        when {
            result.isFailure -> {
                showToast(result.getProblemOrThrow().detail)
            }
            result.isSuccess -> {
                refreshUserProfile(name)
            }
        }
    }

    private suspend fun handleUnfollow(name: String) {
        val result = request {
            val token = session.getToken()
            service.userService.unfollow(token, name)
        }
        when {
            result.isFailure -> {
                showToast(result.getProblemOrThrow().detail)
            }
            result.isSuccess -> {
                refreshUserProfile(name)
            }
        }
    }

    private suspend fun handleUpdateUserProfilePicture(picture: Picture) {
        val result = request {
            val token = session.getToken()
            service.userService.updateUserProfilePicture(token, picture)
        }
        when {
            result.isFailure -> {
                showToast(result.getProblemOrThrow().detail)
            }
            result.isSuccess -> {
                val oldUserProfile = userProfileFlow.value.getOrThrow()
                val newProfilePictureName = result.getValueOrThrow()?.profilePictureName
                if (newProfilePictureName != null) {
                    userProfileFlow.value = apiSuccess(
                        APIResult<UserProfile>(
                            oldUserProfile.copy(profilePicture = picture.second)
                        ).getValueOrThrow()
                    )
                    session.updateUserProfilePicture(context, newProfilePictureName, picture.second)
                }
                else {
                    session.deleteProfilePicture(context)
                }
            }
        }
        enableFollow()
    }

    private suspend fun handleAddRecipeToKitchenBookCollection(
        collectionId: Int,
        addRecipeInfo: AddRecipeToCollectionInputModel
    ) {
        val result = request {
            val token = session.getToken()
            service.collectionService.addRecipeToCollection(token, collectionId, addRecipeInfo)
        }
        when {
            result.isFailure -> {
                kitchenBookCollectionRecipesFlow.value = apiFailure(result.getProblemOrThrow())
            }
            result.isSuccess -> {
                kitchenBookCollectionRecipesFlow.value = apiSuccess(result.getValueOrThrow().collection.recipes)
            }
        }
        enableFollow()
    }

    private suspend fun checkIfIsAnotherUserProfile(name: String) {
        val userName = session.getUserName()
        isAnotherUserProfile = userName != name
    }

    private fun checkUserVisibility(name: String, userProfile: UserProfile): Boolean {
        if (!isAnotherUserProfile) {
            userProfileVisibility = true
            return true
        }
        else {
            if (!userProfile.privacy) {
                userProfileVisibility = true
                return true
            }
            else if (userFollowers.value.getOrThrow().firstOrNull { it.name == name } != null) {
                userProfileVisibility = true
                return true
            }
            userProfileVisibility = false
            return false
        }
    }

    private fun enableFollow() {
        followEnable = true
    }

    private fun disableFollow() {
        followEnable = false
    }
}