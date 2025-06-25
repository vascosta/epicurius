package android.epicurius.domain.collection

const val MIN_COLLECTION_NAME_LENGTH = 3
const val MAX_COLLECTION_NAME_LENGTH = 30
const val COLLECTION_NAME_LENGTH_MSG = "name must be between $MIN_COLLECTION_NAME_LENGTH and $MAX_COLLECTION_NAME_LENGTH characters"

fun validateName(name: String, showErrorMessage: (message: String) -> Unit): Boolean {
    if (name.length !in MIN_COLLECTION_NAME_LENGTH..MAX_COLLECTION_NAME_LENGTH) {
        showErrorMessage(COLLECTION_NAME_LENGTH_MSG)
        return false
    }
    return true
}