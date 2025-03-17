package epicurius.repository.jdbi

import TokenRepository
import org.jdbi.v3.core.Handle

class JdbiTokenRepository(
    private val handle: Handle
) : TokenRepository {
    override fun createToken(tokenHash: String, username: String?, email: String?) {
        handle.createUpdate(
            """
            UPDATED User
            SET token_hash = :token_hash
            WHERE username = :username OR email = :email
            """
        )
            .bind("token_hash", tokenHash)
            .bind("username", username)
            .bind("email", email)
            .execute()
    }
}