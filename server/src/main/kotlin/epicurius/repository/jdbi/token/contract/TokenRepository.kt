package epicurius.repository.jdbi.token.contract

interface TokenRepository {

    fun createToken(tokenHash: String, name: String? = null, email: String? = null)
    fun deleteToken(name: String? = null, email: String? = null)
}
