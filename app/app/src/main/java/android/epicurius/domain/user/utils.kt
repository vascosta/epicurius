package android.epicurius.domain.user

const val MIN_USERNAME_LENGTH = 3
const val MAX_USERNAME_LENGTH = 25

const val MIN_EMAIL_LENGTH = 3
const val MAX_EMAIL_LENGTH = MAX_USERNAME_LENGTH + 30

const val MIN_PASSWORD_LENGTH = 8
const val MAX_PASSWORD_LENGTH = 30

val usernameRegex = Regex("(?=.*[a-zA-Z])[a-zA-Z0-9]*$")
const val VALID_USERNAME_MSG = "username must have at least one letter and one number"
const val USERNAME_LENGTH_MSG = "username must be between $MIN_USERNAME_LENGTH and $MAX_USERNAME_LENGTH characters"

val passwordRegex = Regex("(?=.*[a-z])(?=.*[A-Z])(?=.*[0-9])(?=.*[!@#\$%^&*()\\-__+.]).*$")
const val VALID_PASSWORD_MSG = "password must have upper/lower case letters, a number and a special character"
const val PASSWORD_LENGTH_MSG = "password must be between $MIN_PASSWORD_LENGTH and $MAX_PASSWORD_LENGTH characters"

val emailRegex = Regex("^[A-Za-z0-9+_.-]+@(.+)\$")
const val VALID_EMAIL_MSG = "email must be a valid email address"
const val EMAIL_LENGTH_MSG = "email must be between $MIN_EMAIL_LENGTH and $MAX_EMAIL_LENGTH characters"

fun validateName(
    name: String,
    showErrorMessage: (message: String) -> Unit
): Boolean {
    if (!name.matches(usernameRegex)) {
        showErrorMessage(VALID_USERNAME_MSG)
        return false
    }

    if (name.length !in MIN_USERNAME_LENGTH..MAX_USERNAME_LENGTH) {
        showErrorMessage(USERNAME_LENGTH_MSG)
        return false
    }

    return true
}

fun validateEmail(
    email: String,
    showErrorMessage: (message: String) -> Unit
): Boolean {
    if (!email.matches(emailRegex)) {
        showErrorMessage(VALID_EMAIL_MSG)
        return false
    }
    if (email.length !in MIN_EMAIL_LENGTH..MAX_EMAIL_LENGTH) {
        showErrorMessage(EMAIL_LENGTH_MSG)
        return false
    }
    return true
}

fun validatePassword(
    password: String,
    showErrorMessage: (message: String) -> Unit
): Boolean {
    if (!password.matches(passwordRegex)) {
        showErrorMessage(VALID_PASSWORD_MSG)
        return false
    }
    if (password.length !in MIN_PASSWORD_LENGTH..MAX_PASSWORD_LENGTH) {
        showErrorMessage(PASSWORD_LENGTH_MSG)
        return false
    }
    return true
}