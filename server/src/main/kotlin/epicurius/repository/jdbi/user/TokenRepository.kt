package epicurius.repository.jdbi.user

interface TokenRepository {

    fun createToken(tokenHash: String, username: String? = null, email: String? = null)
    fun deleteToken(username: String? = null, email: String? = null)
}
