package epicurius.repository.jdbi.token

import epicurius.repository.jdbi.token.contract.TokenRepository
import org.jdbi.v3.core.Handle

class JdbiTokenRepository(private val handle: Handle) : TokenRepository {

    override fun createToken(tokenHash: String, name: String?, email: String?) {
        handle.createUpdate(
            """
            UPDATE dbo.user
            SET token_hash = :token_hash
            WHERE name = :name OR email = :email
            """
        )
            .bind("token_hash", tokenHash)
            .bind("name", name)
            .bind("email", email)
            .execute()
    }

    override fun deleteToken(name: String?, email: String?) {
        handle.createUpdate(
            """
            UPDATE dbo.user
            SET token_hash = NULL
            WHERE name = :name OR email = :email
            """
        )
            .bind("name", name)
            .bind("email", email)
            .execute()
    }
}
