package android.epicurius.services.http.api.auth.models.input

data class SignUpInputModel(
    val name: String,
    val email: String,
    val password: String,
    val confirmPassword: String,
    val country: String
)
