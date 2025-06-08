package android.epicurius.ui.screens.recipe.createRecipe

import android.content.Context
import android.epicurius.domain.Diet
import android.epicurius.domain.Intolerance
import android.epicurius.domain.Picture
import android.epicurius.domain.recipe.Cuisine
import android.epicurius.domain.recipe.Ingredient
import android.epicurius.domain.recipe.Instructions
import android.epicurius.domain.recipe.MealType
import android.epicurius.services.EpicuriusService
import android.epicurius.services.api.recipe.models.input.CreateRecipeInputModel
import android.epicurius.storage.Session
import android.epicurius.ui.screens.recipe.RecipeViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch

class CreateRecipeViewModel(
    service: EpicuriusService,
    session: Session,
    context: Context
): RecipeViewModel(service, session, context) {

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
        pictures: List<Picture>,
        navigateTo: (Int) -> Unit
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
            !validatePictures(pictures)
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
            handleCreateRecipe(createRecipeInfo, pictures, navigateTo)
        }
    }

    private suspend fun handleCreateRecipe(
        createRecipeInfo: CreateRecipeInputModel,
        recipePictures: List<Picture>,
        navigateTo: (Int) -> Unit) {
        val result = request {
            val token = session.getToken()
            service.recipeService.createRecipe(token, createRecipeInfo, recipePictures)
        }
        when {
            result.isFailure -> {
                enableButtons()
            }
            result.isSuccess -> {
                val recipeId = result.getValueOrThrow().recipe.id
                navigateTo(recipeId)
            }
        }
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
            !validateName(name) || !validateDescription(description) ||
                 !validateServings(servings) || !validatePreparationTime(preparationTime) ||
                         !validateIngredients(ingredients) || !validateInstructions(instructions)
                             -> false
            calories != null && !validateCalories(calories) -> false
            protein != null && !validateProtein(protein) -> false
            fat != null && !validateFat(fat) -> false
            carbs != null && !validateCarbs(carbs) -> false
            else -> true
        }

}