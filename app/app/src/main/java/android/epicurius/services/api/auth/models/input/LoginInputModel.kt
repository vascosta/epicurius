package android.epicurius.services.api.auth.models.input

data class LoginInputModel(
    val name: String? = null,
    val email: String? = null,
    val password: String
)
