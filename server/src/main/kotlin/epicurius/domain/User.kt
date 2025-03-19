data class User(
    val username: String,
    val email: String,
    val passwordHash: String,
    val tokenHash: String?,
    val country: String,
    val privacy: Boolean,
    val intolerances: Array<String>,
    val diet: Array<String>,
    val profilePictureName: String?
)