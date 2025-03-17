

data class User(
    val id: Int,
    val name: String,
    val email: String,
    val country: String,
    val diet: Array<String>,
    val intolerances: Array<String>,
    val privacy: Boolean,
    val passwordHash: String,
)