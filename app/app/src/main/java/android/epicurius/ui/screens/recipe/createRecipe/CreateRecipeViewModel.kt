package android.epicurius.ui.screens.recipe.createRecipe

import android.content.Context
import android.epicurius.domain.Diet
import android.epicurius.domain.Intolerance
import android.epicurius.domain.recipe.Cuisine
import android.epicurius.domain.recipe.Ingredient
import android.epicurius.domain.recipe.Instructions
import android.epicurius.domain.recipe.MealType
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
import android.epicurius.services.api.recipe.models.input.CreateRecipeInputModel
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

class CreateRecipeViewModel(
    service: EpicuriusService,
    session: Session,
    context: Context
): EpicuriusViewModel(service, session, context) {

    private val searchedIngredientsFlow = MutableStateFlow<LoadState<List<String>>>(idle())
    val searchedIngredients = searchedIngredientsFlow.asStateFlow()

    fun createRecipe(
        name: String,
        description: String,
        servings: Int,
        preparationTime: Int,
        cuisine: Cuisine,
        mealType: MealType,
        intolerances: Set<Intolerance>,
        diets: Set<Diet>,
        ingredients: List<Ingredient>,
        calories: Int?,
        protein: Int?,
        fat: Int?,
        carbs: Int?,
        instructions: Instructions,
        picturesBytes: List<ByteArray>,
        navigateTo: (recipeId: Int) -> Unit
    ) {
        disableButtons()
        if (
            !validateCreateRecipeInfo(
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
            ) ||
            !validatePictures(picturesBytes, ::showToast)
        ) {
            enableButtons()
            return
        }
        val createRecipeInfo = CreateRecipeInputModel(
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
        viewModelScope.launch {
            handleCreateRecipe(createRecipeInfo, picturesBytes, navigateTo)
        }
    }

    fun searchIngredients(partialName: String) {
        disableButtons()
        searchedIngredientsFlow.value = loading()
        viewModelScope.launch { fetchIngredients(partialName) }
    }

    private suspend fun handleCreateRecipe(
        createRecipeInfo: CreateRecipeInputModel,
        recipePicturesBytes: List<ByteArray>,
        navigateTo: (recipeId: Int) -> Unit) {
        val result = request {
            val token = session.getToken()
            service.recipeService.createRecipe(token, createRecipeInfo, recipePicturesBytes)
        }
        when {
            result.isSuccess -> navigateTo(result.getValueOrThrow().recipe.id)
        }
        enableButtons()
    }

    private suspend fun fetchIngredients(partialName: String) {
        val result = request {
            val token = session.getToken()
            service.ingredientsService.getIngredients(token, partialName)
        }
        when {
            result.isSuccess -> {
                val fetchedProducts = result.getValueOrThrow().ingredients
                searchedIngredientsFlow.value = apiSuccess(fetchedProducts)
            }
        }
        enableButtons()
    }

    private fun validateCreateRecipeInfo(
        name: String,
        description: String,
        servings: Int,
        preparationTime: Int,
        ingredients: List<Ingredient>,
        calories: Int?,
        protein: Int?,
        fat: Int?,
        carbs: Int?,
        instructions: Instructions,
    ): Boolean =
        when {
            !validateName(name, ::showToast) || !validateDescription(description, ::showToast) ||
                    !validateServings(servings, ::showToast) || !validatePreparationTime(preparationTime, ::showToast) ||
                         !validateIngredients(ingredients, ::showToast) || !validateInstructions(instructions, ::showToast)
                             -> false
            calories != null && !validateCalories(calories, ::showToast) -> false
            protein != null && !validateProtein(protein, ::showToast) -> false
            fat != null && !validateFat(fat, ::showToast) -> false
            carbs != null && !validateCarbs(carbs, ::showToast) -> false
            else -> true
        }
}