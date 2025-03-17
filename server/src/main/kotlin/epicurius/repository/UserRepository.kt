

interface UserRepository {

    fun createUser(username: String, email: String, country: String, passwordHash:String): Int

    fun checkIfUserExists(name: String?, email: String?): Boolean
    fun checkIfUserIsLoggedIn(username: String?, email: String?): Boolean
}
