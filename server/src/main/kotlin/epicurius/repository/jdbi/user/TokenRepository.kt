package epicurius.repository.jdbi.user

interface TokenRepository {

    fun createToken(tokenHash: String, name: String? = null, email: String? = null)
    fun deleteToken(name: String? = null, email: String? = null)
}
