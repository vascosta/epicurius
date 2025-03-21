
interface UserPostgresRepository {

    fun createUser(username: String, email: String, country: String, passwordHash: String)

    fun getUser(username: String?, email: String?): User
    fun getUserFromTokenHash(tokenHash: String): User
    fun getProfilePictureName(username: String): String

    fun resetPassword(username: String, passwordHash: String)

    fun updateIntolerances(username: String, intolerancesIdx: List<Int>)

    fun checkIfUserExists(username: String?, email: String?, tokenHash: String?): Boolean
    fun checkIfUserIsLoggedIn(username: String?, email: String?): Boolean
}
