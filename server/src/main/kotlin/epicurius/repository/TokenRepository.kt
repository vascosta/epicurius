interface TokenRepository {

    fun createToken(tokenHash: String, username: String?, email: String?)
    fun deleteToken(username: String)
}
