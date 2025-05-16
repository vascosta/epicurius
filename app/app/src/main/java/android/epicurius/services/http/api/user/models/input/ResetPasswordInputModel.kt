package android.epicurius.services.http.api.user.models.input

data class ResetPasswordInputModel(
    val email: String,
    val newPassword: String,
    val confirmPassword: String
)
