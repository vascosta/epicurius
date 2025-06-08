package android.epicurius.ui.screens.recipe

import android.content.Context
import android.epicurius.domain.recipe.INGREDIENTS_SIZE_MSG
import android.epicurius.domain.recipe.INSTRUCTIONS_STEPS_SIZE_MSG
import android.epicurius.domain.recipe.INSTRUCTIONS_STEP_NUMBER_MSG
import android.epicurius.domain.recipe.Ingredient
import android.epicurius.domain.recipe.Instructions
import android.epicurius.domain.recipe.MAX_INGREDIENT_NAME_LENGTH
import android.epicurius.domain.recipe.MAX_INSTRUCTIONS_STEP_LENGTH
import android.epicurius.domain.recipe.MAX_NUMBER_OF_INGREDIENTS
import android.epicurius.domain.recipe.MAX_NUMBER_OF_INSTRUCTIONS_STEPS
import android.epicurius.domain.recipe.MAX_RECIPE_DESCRIPTION_LENGTH
import android.epicurius.domain.recipe.MAX_RECIPE_NAME_LENGTH
import android.epicurius.domain.recipe.MIN_INGREDIENT_NAME_LENGTH
import android.epicurius.domain.recipe.MIN_INGREDIENT_QUANTITY
import android.epicurius.domain.recipe.MIN_INSTRUCTIONS_STEP_LENGTH
import android.epicurius.domain.recipe.MIN_RECIPE_NAME_LENGTH
import android.epicurius.domain.recipe.RECIPE_DESCRIPTION_LENGTH_MSG
import android.epicurius.domain.recipe.RECIPE_NAME_LENGTH_MSG
import android.epicurius.domain.recipe.getIngredientNameMessage
import android.epicurius.domain.recipe.getIngredientQuantityMessage
import android.epicurius.domain.recipe.getInstructionStepMessage
import android.epicurius.domain.recipe.getPositiveNumberMessage
import android.epicurius.services.EpicuriusService
import android.epicurius.storage.Session
import android.epicurius.ui.EpicuriusViewModel

class RecipeViewModel(
    service: EpicuriusService,
    session: Session,
    context: Context
): EpicuriusViewModel(service, session, context) {

    fun validateName(name: String): Boolean {
        if (name.length !in MIN_RECIPE_NAME_LENGTH..MAX_RECIPE_NAME_LENGTH) {
            showToast(RECIPE_NAME_LENGTH_MSG)
            return false
        }
        return true
    }

    fun validateDescription(description: String): Boolean {
        if (description.length > MAX_RECIPE_DESCRIPTION_LENGTH) {
            showToast(RECIPE_DESCRIPTION_LENGTH_MSG)
            return false
        }
        return true
    }

    fun validateServings(servings: Int): Boolean = validateNumber(servings, "servings")

    fun validatePreparationTime(preparationTime: Int): Boolean = validateNumber(preparationTime, "preparation time")

    fun validateIngredients(ingredients: List<Ingredient>): Boolean {
        if (ingredients.size > MAX_NUMBER_OF_INGREDIENTS) {
            showToast(INGREDIENTS_SIZE_MSG)
            return false
        }

        ingredients.forEach { ingredient ->
            if (ingredient.name.length !in MIN_INGREDIENT_NAME_LENGTH..MAX_INGREDIENT_NAME_LENGTH) {
                showToast(getIngredientNameMessage(ingredient.name))
                return false
            }

            if (ingredient.quantity < MIN_INGREDIENT_QUANTITY) {
                showToast(getIngredientQuantityMessage(ingredient.quantity))
                return false
            }
        }
        return true
    }

    fun validateCalories(calories: Int): Boolean = validateNumber(calories, "calories")

    fun validateProtein(protein: Int): Boolean = validateNumber(protein, "protein")

    fun validateFat(fat: Int): Boolean = validateNumber(fat, "fat")

    fun validateCarbs(carbs: Int): Boolean = validateNumber(carbs, "carbs")

    fun validateInstructions(instructions: Instructions): Boolean {
        if (instructions.steps.size > MAX_NUMBER_OF_INSTRUCTIONS_STEPS) {
            showToast(INSTRUCTIONS_STEPS_SIZE_MSG)
            return false
        }

        instructions.steps.forEach { (stepNumber, stepDescription) ->
            if (stepNumber.toIntOrNull() == null) {
                showToast(INSTRUCTIONS_STEP_NUMBER_MSG)
                return false
            }

            if (stepDescription.length !in MIN_INSTRUCTIONS_STEP_LENGTH..MAX_INSTRUCTIONS_STEP_LENGTH) {
                showToast(getInstructionStepMessage(stepNumber))
                return false
            }
        }
        return true
    }

    private fun validateNumber(number: Int, recipeParamName: String): Boolean {
        if (number < 0) {
            showToast(getPositiveNumberMessage(recipeParamName))
            return false
        }
        return true
    }
}