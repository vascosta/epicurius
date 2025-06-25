package android.epicurius.ui.screens.search

import android.content.Context
import android.epicurius.domain.Diet
import android.epicurius.domain.Intolerance
import android.epicurius.domain.Picture
import android.epicurius.domain.recipe.Cuisine
import android.epicurius.domain.recipe.MealType
import android.epicurius.domain.recipe.RecipeInfo
import android.epicurius.domain.recipe.validateCalories
import android.epicurius.domain.recipe.validateCarbs
import android.epicurius.domain.recipe.validateFat
import android.epicurius.domain.recipe.validateName
import android.epicurius.domain.recipe.validatePreparationTime
import android.epicurius.domain.recipe.validateProtein
import android.epicurius.domain.user.SearchUser
import android.epicurius.domain.user.UserInfo
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

class SearchViewModel(
    service: EpicuriusService,
    session: Session,
    context: Context
): RecipeCollectionsViewModel(service, session, context) {

    private val searchedRecipesFlow = MutableStateFlow<LoadState<List<RecipeInfo>>>(idle())
    private val cacheSearchedRecipesFlow = MutableStateFlow<List<RecipeInfo>>(emptyList())
    private val lastFetchedRecipeIdFlow = MutableStateFlow<Int?>(null)

    val searchedRecipes = searchedRecipesFlow.asStateFlow()
    private val cacheSearchedRecipes = cacheSearchedRecipesFlow.asStateFlow()

    private val searchedUsersFlow = MutableStateFlow<LoadState<List<SearchUser>>>(idle())
    private val cacheSearchedUsersFlow = MutableStateFlow<List<SearchUser>>(emptyList())
    private val lastFetchedUserIdFlow = MutableStateFlow<Int?>(null)

    val searchedUsers = searchedUsersFlow.asStateFlow()
    private val cacheSearchedUsers = cacheSearchedUsersFlow.asStateFlow()

    private val ingredientsFlow = MutableStateFlow<LoadState<List<String>>>(idle())
    val ingredients = ingredientsFlow.asStateFlow()

    private val userInfoFlow = MutableStateFlow<LoadState<UserInfo>>(idle())
    val userInfo = userInfoFlow.asStateFlow()

    fun searchRecipes(
        name: String?,
        cuisine: List<Cuisine>?,
        mealType: List<MealType>?,
        ingredients: List<String>?,
        intolerances: List<Intolerance>?,
        diets: List<Diet>?,
        servings: Int?,
        minCalories: Int?,
        maxCalories: Int?,
        minCarbs: Int?,
        maxCarbs: Int?,
        minFat: Int?,
        maxFat: Int?,
        minProtein: Int?,
        maxProtein: Int?,
        minTime: Int?,
        maxTime: Int?
    ) {
        disableButtons()
        searchedRecipesFlow.value = loading(CachedResult(cacheSearchedRecipes.value))
        if (
            !validateSearchRecipesInfo(
                name,
                minCalories,
                maxCalories,
                minCarbs,
                maxCarbs,
                minFat,
                maxFat,
                minProtein,
                maxProtein,
                minTime,
                maxTime
            )
        ) {
            enableButtons()
            searchedRecipesFlow.value = cache(cacheSearchedRecipes.value)
            return
        }
        viewModelScope.launch {
            fetchRecipes(
                name,
                cuisine,
                mealType,
                ingredients,
                intolerances,
                diets,
                servings,
                minCalories,
                maxCalories,
                minCarbs,
                maxCarbs,
                minFat,
                maxFat,
                minProtein,
                maxProtein,
                minTime,
                maxTime
            )
        }
    }

    fun searchUsers(name: String) {
        disableButtons()
        searchedUsersFlow.value = loading(CachedResult(cacheSearchedUsers.value))
        viewModelScope.launch { fetchUsers(name) }
    }

    fun identifyIngredientsInPicture(pictureBytes: ByteArray) {
        disableButtons()
        ingredientsFlow.value = loading()
        viewModelScope.launch { handleIdentifyIngredientsOnPicture(pictureBytes) }
    }

    fun getUserInfo() {
        disableButtons()
        userInfoFlow.value = loading()
        viewModelScope.launch { getCachedUserInfo() }
    }

    fun clearSearchRecipes() {
        searchedRecipesFlow.value = idle()
        cacheSearchedRecipesFlow.value = emptyList()
        lastFetchedRecipeIdFlow.value = null
    }

    fun clearSearchUsers() {
        searchedUsersFlow.value = idle()
        cacheSearchedUsersFlow.value = emptyList()
        lastFetchedUserIdFlow.value = null
    }

    fun clearIngredients() {
        ingredientsFlow.value = idle()
    }

    private suspend fun fetchRecipes(
        name: String?,
        cuisine: List<Cuisine>?,
        mealType: List<MealType>?,
        ingredients: List<String>?,
        intolerances: List<Intolerance>?,
        diets: List<Diet>?,
        servings: Int?,
        minCalories: Int?,
        maxCalories: Int?,
        minCarbs: Int?,
        maxCarbs: Int?,
        minFat: Int?,
        maxFat: Int?,
        minProtein: Int?,
        maxProtein: Int?,
        minTime: Int?,
        maxTime: Int?
    ) {
        val result = request {
            val token = session.getToken()
            val lastRecipeId = lastFetchedRecipeIdFlow.value
            service.recipeService.searchRecipes(
                token,
                name,
                cuisine,
                mealType,
                ingredients,
                intolerances,
                diets,
                servings,
                minCalories,
                maxCalories,
                minCarbs,
                maxCarbs,
                minFat,
                maxFat,
                minProtein,
                maxProtein,
                minTime,
                maxTime,
                lastRecipeId,
                limit
            )
        }
        when {
            result.isFailure -> searchedRecipesFlow.value = cache(cacheSearchedRecipes.value)
            result.isSuccess -> {
                val fetchedRecipes = result.getValueOrThrow().recipes

                if (fetchedRecipes.isNotEmpty()) {
                    val updatedSearchedRecipes = cacheSearchedRecipes.value + fetchedRecipes
                    searchedRecipesFlow.value = apiSuccess(updatedSearchedRecipes)
                    cacheSearchedRecipesFlow.value = updatedSearchedRecipes
                    lastFetchedRecipeIdFlow.value = fetchedRecipes.last().id
                }
                else searchedRecipesFlow.value = cache(cacheSearchedRecipes.value)
            }
        }
        enableButtons()
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

    private suspend fun handleIdentifyIngredientsOnPicture(pictureBytes: ByteArray) {
        val result = request {
            val token = session.getToken()
            service.ingredientsService.identifyIngredientsInPicture(
                token,
                pictureBytes
            )
        }
        when {
            result.isFailure -> ingredientsFlow.value = cache(emptyList())
            result.isSuccess -> {
                val identifiedIngredients = result.getValueOrThrow().ingredients
                ingredientsFlow.value = apiSuccess(identifiedIngredients)
            }
        }
        enableButtons()
    }

    private suspend fun getCachedUserInfo() {
        val userInfo = session.getUserInfo()
        userInfoFlow.value = cache(userInfo)
        enableButtons()
    }

    private fun validateSearchRecipesInfo(
        name: String?,
        minCalories: Int?,
        maxCalories: Int?,
        minCarbs: Int?,
        maxCarbs: Int?,
        minFat: Int?,
        maxFat: Int?,
        minProtein: Int?,
        maxProtein: Int?,
        minTime: Int?,
        maxTime: Int?
    ): Boolean =
        when {
            name != null && !validateName(name, ::showToast) -> false
            minCalories != null && !validateCalories(minCalories, ::showToast) -> false
            maxCalories != null && !validateCalories(maxCalories, ::showToast) -> false
            minCalories != null && maxCalories != null && minCalories > maxCalories -> {
                showToast("max calories must be greater then min calories")
                false
            }
            minCarbs != null && !validateCarbs(minCarbs, ::showToast) -> false
            maxCarbs != null && !validateCarbs(maxCarbs, ::showToast) -> false
            minCarbs != null && maxCarbs != null && minCarbs > maxCarbs -> {
                showToast("max carbs must be greater then min carbs")
                false
            }
            minFat != null && !validateFat(minFat, ::showToast) -> false
            maxFat != null && !validateFat(maxFat, ::showToast) -> false
            minFat != null && maxFat != null && minFat > maxFat -> {
                showToast("max fat must be greater then min fat")
                false
            }
            minProtein != null && !validateProtein(minProtein, ::showToast) -> false
            maxProtein != null && !validateProtein(maxProtein, ::showToast) -> false
            minProtein != null && maxProtein != null && minProtein > maxProtein -> {
                showToast("max protein must be greater then min protein")
                false
            }
            minTime != null && !validatePreparationTime(minTime, ::showToast) -> false
            maxTime != null && !validatePreparationTime(maxTime, ::showToast) -> false
            minTime != null && maxTime != null && minTime > maxTime -> {
                showToast("max preparation time must be greater then min preparation time")
                false
            }
            else -> true
        }
}