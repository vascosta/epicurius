package android.epicurius.ui.screens.recipe.profile

import android.content.Context
import android.epicurius.domain.Diet
import android.epicurius.domain.Intolerance
import android.epicurius.domain.recipe.Cuisine
import android.epicurius.domain.recipe.Ingredient
import android.epicurius.domain.recipe.Instructions
import android.epicurius.domain.recipe.MealType
import android.epicurius.domain.recipe.Recipe
import android.epicurius.domain.recipe.validateCalories
import android.epicurius.domain.recipe.validateCarbs
import android.epicurius.domain.recipe.validateDescription
import android.epicurius.domain.recipe.validateFat
import android.epicurius.domain.recipe.validateIngredients
import android.epicurius.domain.recipe.validateInstructions
import android.epicurius.domain.recipe.validateName
import android.epicurius.domain.recipe.validatePictures
import android.epicurius.domain.recipe.validatePreparationTime
import android.epicurius.domain.recipe.validateProtein
import android.epicurius.domain.recipe.validateServings
import android.epicurius.services.EpicuriusService
import android.epicurius.services.api.recipe.models.input.RateRecipeInputModel
import android.epicurius.services.api.recipe.models.input.UpdateRecipeInputModel
import android.epicurius.storage.Session
import android.epicurius.ui.EpicuriusViewModel
import android.epicurius.ui.screens.utils.LoadState
import android.epicurius.ui.screens.utils.apiSuccess
import android.epicurius.ui.screens.utils.cache
import android.epicurius.ui.screens.utils.getOrThrow
import android.epicurius.ui.screens.utils.idle
import android.epicurius.ui.screens.utils.loading
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.util.Base64

class RecipeProfileViewModel(
    service: EpicuriusService,
    session: Session,
    context: Context
): EpicuriusViewModel(service, session, context) {

    private val recipeFlow = MutableStateFlow<LoadState<Recipe>>(idle())
    private val recipeNameFlow = MutableStateFlow<LoadState<String>>(idle())

    val recipe = recipeFlow.asStateFlow()
    val recipeName = recipeNameFlow.asStateFlow()

    private val usernameFlow = MutableStateFlow<LoadState<String>>(idle())
    private val userRecipeRatingFlow = MutableStateFlow<LoadState<Int?>>(idle())

    val username = usernameFlow.asStateFlow()
    val userRecipeRating = userRecipeRatingFlow.asStateFlow()

    private val substituteIngredientsFlow = MutableStateFlow<LoadState<List<String>>>(idle())
    val substituteIngredients = substituteIngredientsFlow.asStateFlow()

    fun getRecipeProfile(id: Int, onErrorNavigateTo: () -> Unit) {
        disableButtons()
        if (id == -1) {
            showToast("Missing RECIPE_ID in intent")
            onErrorNavigateTo()
            return
        }
        recipeFlow.value = loading()
        recipeNameFlow.value = loading()
        viewModelScope.launch { fetchRecipeProfile(id, onErrorNavigateTo) }
    }

    fun getUserRecipeRating(id: Int, onErrorNavigateTo: () -> Unit) {
        disableButtons()
        if (id == -1) {
            showToast("Missing RECIPE_ID in intent")
            onErrorNavigateTo()
            return
        }
        userRecipeRatingFlow.value = loading()
        viewModelScope.launch { fetchUserRecipeRating(id, onErrorNavigateTo) }
    }

    fun getUsername() {
        disableButtons()
        usernameFlow.value = loading()
        viewModelScope.launch { getCachedUsername() }
    }

    fun getSubstituteIngredients(ingredientName: String) {
        disableButtons()
        substituteIngredientsFlow.value = loading()
        viewModelScope.launch { fetchSubstituteIngredients(ingredientName) }
    }

    fun rateRecipe(id: Int, rating: Int) {
        disableButtons()
        val rateRecipeInfo = RateRecipeInputModel(rating)
        viewModelScope.launch { handleRateRecipe(id, rateRecipeInfo) }
    }

    fun updateRecipe(
        recipeId: Int,
        name: String?,
        description: String?,
        servings: Int?,
        preparationTime: Int?,
        cuisine: Cuisine?,
        mealType: MealType?,
        intolerances: Set<Intolerance>?,
        diets: Set<Diet>?,
        ingredients: List<Ingredient>?,
        calories: Int?,
        protein: Int?,
        fat: Int?,
        carbs: Int?,
        instructions: Instructions?
    ) {
        disableButtons()
        if (
            !validateUpdateRecipeInfo(
                name,
                description,
                servings,
                preparationTime,
                ingredients,
                calories,
                protein,
                fat,
                carbs,
                instructions
            )
        ) {
            enableButtons()
            return
        }
        val updateRecipeInfo = UpdateRecipeInputModel(
            name,
            description,
            servings,
            preparationTime,
            cuisine,
            mealType,
            intolerances,
            diets,
            ingredients,
            calories,
            protein,
            fat,
            carbs,
            instructions
        )
        viewModelScope.launch { handleUpdateRecipe(recipeId, updateRecipeInfo) }
    }

    fun updateRecipePictures(id: Int, picturesBytes: List<ByteArray>) {
        disableButtons()
        if (!validatePictures(picturesBytes, ::showToast) ) {
            enableButtons()
            return
        }
        viewModelScope.launch { handleUpdateRecipePictures(id, picturesBytes) }
    }

    fun updateUserRecipeRating(recipeId: Int, rating: Int) {
        disableButtons()
        val rateRecipeInfo = RateRecipeInputModel(rating)
        viewModelScope.launch { handleUpdateUserRecipeRating(recipeId, rateRecipeInfo) }
    }

    fun deleteRecipe(recipeId: Int, onSuccessNavigateTo: () -> Unit) {
        disableButtons()
        viewModelScope.launch { handleDeleteRecipe(recipeId, onSuccessNavigateTo) }
    }

    fun deleteUserRecipeRating(id: Int) {
        disableButtons()
        viewModelScope.launch { handleDeleteUserRecipeRating(id) }
    }

    private suspend fun fetchRecipeProfile(id: Int, onErrorNavigateTo: () -> Unit) {
        val result = request {
            val token = session.getToken()
            service.recipeService.getRecipe(token, id)
        }
        when {
            result.isFailure -> onErrorNavigateTo()
            result.isSuccess -> {
                val fetchedRecipe = result.getValueOrThrow().recipe
                recipeFlow.value = apiSuccess(fetchedRecipe)
                recipeNameFlow.value = apiSuccess(fetchedRecipe.name)
                enableButtons()
            }
        }
    }

    private suspend fun fetchUserRecipeRating(id: Int, onErrorNavigateTo: () -> Unit) {
        val result = request {
            val token = session.getToken()
            service.recipeService.getUserRecipeRate(token, id)
        }
        when {
            result.isFailure -> onErrorNavigateTo()
            result.isSuccess -> {
                userRecipeRatingFlow.value = apiSuccess(result.getValueOrThrow().rating)
                enableButtons()
            }
        }
    }

    private suspend fun getCachedUsername() {
        val userInfo = session.getUserInfo()
        usernameFlow.value = cache(userInfo.name)
        enableButtons()
    }

    private suspend fun fetchSubstituteIngredients(ingredientName: String) {
        val result = request {
            val token = session.getToken()
            service.ingredientsService.getSubstituteIngredients(token, ingredientName)
        }
        when {
            result.isSuccess -> substituteIngredientsFlow.value = apiSuccess(result.getValueOrThrow().ingredients)
        }
        enableButtons()
    }

    private suspend fun handleRateRecipe(id: Int, rateRecipeInfo: RateRecipeInputModel) {
        val result = request {
            val token = session.getToken()
            service.recipeService.rateRecipe(token, id, rateRecipeInfo)
        }
        when {
            result.isSuccess -> {
                val updatedRecipeRating = result.getValueOrThrow().rating
                val oldRecipe = recipeFlow.value.getOrThrow()
                val updatedRecipe = oldRecipe.copy(
                    rating = updatedRecipeRating
                )
                recipeFlow.value = apiSuccess(updatedRecipe)
                userRecipeRatingFlow.value = apiSuccess(rateRecipeInfo.rating)
            }
        }
        enableButtons()
    }

    private suspend fun handleUpdateRecipe(recipeId: Int, updateRecipeInfo: UpdateRecipeInputModel) {
        val result = request {
            val token = session.getToken()
            service.recipeService.updateRecipe(token, recipeId, updateRecipeInfo)
        }
        when {
            result.isSuccess -> {
                val recipePictures = recipeFlow.value.getOrThrow().pictures
                val updatedRecipe = result.getValueOrThrow().recipe.toRecipe(recipePictures)
                recipeFlow.value = apiSuccess(updatedRecipe)
                recipeNameFlow.value = apiSuccess(updatedRecipe.name)
            }
        }
        enableButtons()
    }

    private suspend fun handleUpdateRecipePictures(
        id: Int,
        picturesBytes: List<ByteArray>
    ) {
        val result = request {
            val token = session.getToken()
            service.recipeService.updateRecipePictures(token, id, picturesBytes)
        }
        when {
            result.isSuccess -> {
                val oldRecipe = recipeFlow.value.getOrThrow()
                val updatedRecipe = oldRecipe.copy(
                    pictures = picturesBytes.map { Base64.getEncoder().encodeToString(it) }
                )
                recipeFlow.value = apiSuccess(updatedRecipe)
            }
        }
        enableButtons()
    }

    private suspend fun handleUpdateUserRecipeRating(
        recipeId: Int,
        rateRecipeInfo: RateRecipeInputModel
    ) {
        val result = request {
            val token = session.getToken()
            service.recipeService.updateUserRecipeRating(token, recipeId, rateRecipeInfo)
        }
        when {
            result.isSuccess -> {
                val updatedRecipeRating = result.getValueOrThrow().rating
                val oldRecipe = recipeFlow.value.getOrThrow()
                val updatedRecipe = oldRecipe.copy(
                    rating = updatedRecipeRating
                )
                recipeFlow.value = apiSuccess(updatedRecipe)
                userRecipeRatingFlow.value = apiSuccess(rateRecipeInfo.rating)
            }
        }
        enableButtons()
    }

    private suspend fun handleDeleteRecipe(recipeId: Int, onSuccessNavigateTo: () -> Unit) {
        val result = request {
            val token = session.getToken()
            service.recipeService.deleteRecipe(token, recipeId)
        }
        when {
            result.isFailure -> enableButtons()
            result.isSuccess -> {
                showToast("Recipe deleted successfully")
                onSuccessNavigateTo()
            }
        }
    }

    private suspend fun handleDeleteUserRecipeRating(id: Int) {
        val result = request {
            val token = session.getToken()
            service.recipeService.deleteUserRecipeRate(token, id)
        }
        when {
            result.isFailure -> enableButtons()
            result.isSuccess -> {
                userRecipeRatingFlow.value = apiSuccess(null)
                val oldRecipe = recipeFlow.value.getOrThrow()
                val updatedRecipeRating = fetchRecipeRatings(oldRecipe.id)
                val updatedRecipe = oldRecipe.copy(
                    rating = updatedRecipeRating
                )
                recipeFlow.value = apiSuccess(updatedRecipe)
            }
        }
    }

    private suspend fun fetchRecipeRatings(recipeId: Int): Double {
        val result = request {
            val token = session.getToken()
            service.recipeService.getRecipeRating(token, recipeId)
        }
        when {
            result.isSuccess -> return result.getValueOrThrow().rating
        }
        return 0.0
    }

    private fun validateUpdateRecipeInfo(
        name: String?,
        description: String?,
        servings: Int?,
        preparationTime: Int?,
        ingredients: List<Ingredient>?,
        calories: Int?,
        protein: Int?,
        fat: Int?,
        carbs: Int?,
        instructions: Instructions?,
    ): Boolean =
        when {
            name != null && !validateName(name, ::showToast) -> false
            description != null &&!validateDescription(description, ::showToast) -> false
            servings != null && !validateServings(servings, ::showToast) -> false
            preparationTime != null && !validatePreparationTime(preparationTime, ::showToast) -> false
            ingredients != null && !validateIngredients(ingredients, ::showToast) -> false
            instructions != null && !validateInstructions(instructions, ::showToast) -> false
            calories != null && !validateCalories(calories, ::showToast) -> false
            protein != null && !validateProtein(protein, ::showToast) -> false
            fat != null && !validateFat(fat, ::showToast) -> false
            carbs != null && !validateCarbs(carbs, ::showToast) -> false
            else -> true
        }

}