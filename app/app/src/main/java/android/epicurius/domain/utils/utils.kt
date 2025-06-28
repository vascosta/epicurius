package android.epicurius.domain.utils

fun getPositiveNumberMessage(number: String) = "$number must be a positive number"

fun validateNumber(
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