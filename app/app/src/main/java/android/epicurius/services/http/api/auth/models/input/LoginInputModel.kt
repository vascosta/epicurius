package android.epicurius.services.http.api.auth.models.input

data class LoginInputModel(
    val name: String? = null,
    val email: String? = null,
    val password: String
)
