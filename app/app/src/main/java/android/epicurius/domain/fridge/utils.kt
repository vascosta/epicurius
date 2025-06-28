package android.epicurius.domain.fridge

import android.epicurius.domain.utils.validateNumber


val productNameRegex = Regex("^[A-Za-zÀ-ÿ\\s]+$")
const val VALID_PRODUCT_NAME_MSG = "product name can only contain letters"

const val MIN_PRODUCT_QUANTITY = 1
const val MAX_PRODUCT_QUANTITY = 20
const val PRODUCT_QUANTITY_MSG = "quantity must be between $MIN_PRODUCT_QUANTITY and $MAX_PRODUCT_QUANTITY"


fun validateName(name: String, showErrorMessage: (message: String) -> Unit): Boolean {
    if (!name.matches(productNameRegex)) {
        showErrorMessage(VALID_PRODUCT_NAME_MSG)
        return false
    }
    return true
}

fun validateQuantity(quantity: Int, showErrorMessage: (message: String) -> Unit): Boolean {
    if (quantity !in MIN_PRODUCT_QUANTITY..MAX_PRODUCT_QUANTITY) {
        showErrorMessage(PRODUCT_QUANTITY_MSG)
        return false
    }
    if (!validateNumber(quantity, "quantity", showErrorMessage)) return false
    return true
}

