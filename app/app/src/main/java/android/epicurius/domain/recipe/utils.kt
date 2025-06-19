package android.epicurius.domain.recipe

import android.epicurius.domain.Picture

const val MIN_RECIPE_NAME_LENGTH = 3
const val MAX_RECIPE_NAME_LENGTH = 50
const val RECIPE_NAME_LENGTH_MSG = "recipe name must be between $MIN_RECIPE_NAME_LENGTH and $MAX_RECIPE_NAME_LENGTH characters"

const val MAX_RECIPE_DESCRIPTION_LENGTH = 200
const val RECIPE_DESCRIPTION_LENGTH_MSG = "recipe description must be less than $MAX_RECIPE_DESCRIPTION_LENGTH characters"

const val MIN_INGREDIENT_NAME_LENGTH = 1
const val MAX_INGREDIENT_NAME_LENGTH = 20
const val MAX_NUMBER_OF_INGREDIENTS = 30
const val INGREDIENTS_SIZE_MSG = "the number of ingredients must be at most $MAX_NUMBER_OF_INGREDIENTS"

const val MIN_INGREDIENT_QUANTITY = 0.1

const val MIN_INSTRUCTIONS_STEP_LENGTH = 1
const val MAX_INSTRUCTIONS_STEP_LENGTH = 200
const val MAX_NUMBER_OF_INSTRUCTIONS_STEPS = 20
const val INSTRUCTIONS_STEPS_SIZE_MSG =
    "the number of instructions steps must be at most $MAX_NUMBER_OF_INSTRUCTIONS_STEPS steps"

const val INSTRUCTIONS_STEP_NUMBER_MSG = "instructions step identifier must be a number"

const val MIN_NUMBER_OF_RECIPE_PICTURES = 1
const val MAX_NUMBER_OF_RECIPE_PICTURES = 3
const val RECIPE_PICTURES_MSG = "the number of pictures must be at least $MIN_NUMBER_OF_RECIPE_PICTURES and at most $MAX_NUMBER_OF_RECIPE_PICTURES"

const val MIN_RECIPE_RATING = 1
const val MAX_RECIPE_RATING = 5
const val RATING_MSG = "Rating must be between $MIN_RECIPE_RATING and $MAX_RECIPE_RATING"

fun getPositiveNumberMessage(number: String) = "$number must be a positive number"

fun getIngredientNameMessage(name: String) =
    "$name name must be between $MIN_INGREDIENT_NAME_LENGTH and $MAX_INGREDIENT_NAME_LENGTH characters"

fun getIngredientQuantityMessage(name: Double) =
    "$name quantity must be greater than $MIN_INGREDIENT_QUANTITY"

fun getInstructionStepMessage(stepIdentifier: String) =
    "the length of the $stepIdentifier step must be between $MIN_INSTRUCTIONS_STEP_LENGTH and $MAX_INSTRUCTIONS_STEP_LENGTH characters"

fun validateName(
    name: String,
    showErrorMessage: (message: String) -> Unit
): Boolean {
    if (name.length !in MIN_RECIPE_NAME_LENGTH..MAX_RECIPE_NAME_LENGTH) {
        showErrorMessage(RECIPE_NAME_LENGTH_MSG)
        return false
    }
    return true
}

fun validateDescription(
    description: String,
    showErrorMessage: (message: String) -> Unit
): Boolean {
    if (description.length > MAX_RECIPE_DESCRIPTION_LENGTH) {
        showErrorMessage(RECIPE_DESCRIPTION_LENGTH_MSG)
        return false
    }
    return true
}

fun validateServings(
    servings: Int,
    showErrorMessage: (message: String) -> Unit
): Boolean = validateNumber(servings, "servings", showErrorMessage)

fun validatePreparationTime(
    preparationTime: Int,
    showErrorMessage: (message: String) -> Unit
): Boolean = validateNumber(preparationTime, "preparation time", showErrorMessage)

fun validateIngredients(
    ingredients: List<Ingredient>,
    showErrorMessage: (message: String) -> Unit
): Boolean {
    if (ingredients.size > MAX_NUMBER_OF_INGREDIENTS) {
        showErrorMessage(INGREDIENTS_SIZE_MSG)
        return false
    }

    ingredients.forEach { ingredient ->
        if (ingredient.name.length !in MIN_INGREDIENT_NAME_LENGTH..MAX_INGREDIENT_NAME_LENGTH) {
            showErrorMessage(getIngredientNameMessage(ingredient.name))
            return false
        }

        if (ingredient.quantity < MIN_INGREDIENT_QUANTITY) {
            showErrorMessage(getIngredientQuantityMessage(ingredient.quantity))
            return false
        }
    }
    return true
}

fun validateCalories(
    calories: Int,
    showErrorMessage: (message: String) -> Unit
): Boolean = validateNumber(calories, "calories", showErrorMessage)

fun validateProtein(
    protein: Int,
    showErrorMessage: (message: String) -> Unit
): Boolean = validateNumber(protein, "protein", showErrorMessage)

fun validateFat(
    fat: Int,
    showErrorMessage: (message: String) -> Unit
): Boolean = validateNumber(fat, "fat", showErrorMessage)

fun validateCarbs(
    carbs: Int,
    showErrorMessage: (message: String) -> Unit
): Boolean = validateNumber(carbs, "carbs", showErrorMessage)

fun validateInstructions(
    instructions: Instructions,
    showErrorMessage: (message: String) -> Unit
): Boolean {
    if (instructions.steps.size > MAX_NUMBER_OF_INSTRUCTIONS_STEPS) {
        showErrorMessage(INSTRUCTIONS_STEPS_SIZE_MSG)
        return false
    }

    instructions.steps.forEach { (stepNumber, stepDescription) ->
        if (stepNumber.toIntOrNull() == null) {
            showErrorMessage(INSTRUCTIONS_STEP_NUMBER_MSG)
            return false
        }

        if (stepDescription.length !in MIN_INSTRUCTIONS_STEP_LENGTH..MAX_INSTRUCTIONS_STEP_LENGTH) {
            showErrorMessage(getInstructionStepMessage(stepNumber))
            return false
        }
    }
    return true
}

fun validatePictures(
    pictures: List<Picture>,
    showErrorMessage: (message: String) -> Unit
): Boolean {
    if (pictures.size !in MIN_NUMBER_OF_RECIPE_PICTURES..MAX_NUMBER_OF_RECIPE_PICTURES) {
        showErrorMessage(RECIPE_PICTURES_MSG)
        return false
    }
    return true
}

private fun validateNumber(
    number: Int,
    recipeParamName: String,
    showErrorMessage: (message: String) -> Unit
): Boolean {
    if (number < 0) {
        showErrorMessage(getPositiveNumberMessage(recipeParamName))
        return false
    }
    return true
}