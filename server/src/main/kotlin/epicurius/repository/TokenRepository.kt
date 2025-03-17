interface TokenRepository {

    fun createToken(tokenHash: String, username: String?, email: String?)
}
