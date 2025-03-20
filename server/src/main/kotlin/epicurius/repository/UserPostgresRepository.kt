import epicurius.domain.Intolerance

interface UserPostgresRepository {

    fun createUser(username: String, email: String, country: String, passwordHash: String)
    fun getUser(username: String?, email: String?): User
    fun getUserFromTokenHash(tokenHash: String): User
    fun resetPassword(username: String, passwordHash: String)
    fun addIntolerances(username: String, intolerancesIdx: List<Int>)
    fun getIntolerances(username: String): List<Intolerance>

    fun checkIfUserExists(username: String?, email: String?, tokenHash: String?): Boolean
    fun checkIfUserIsLoggedIn(username: String?, email: String?): Boolean
}
