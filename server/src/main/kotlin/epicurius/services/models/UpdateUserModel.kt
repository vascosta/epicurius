package epicurius.services.models

data class UpdateUserModel(
    val username: String?,
    val email: String?,
    val country: String?,
    val passwordHash: String?,
    val privacy: Boolean?,
    val intolerances: List<Int>?,
    val diet: List<Int>?
)
