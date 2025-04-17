package epicurius.repository.jdbi.user.models

data class JdbiUpdateUserModel(
    val name: String? = null,
    val email: String? = null,
    val country: String? = null,
    val passwordHash: String? = null,
    val privacy: Boolean? = null,
    val intolerances: List<Int>? = null,
    val diets: List<Int>? = null,
    val profilePictureName: String? = null
)
