package epicurius.repository.jdbi.token

import epicurius.repository.jdbi.token.contract.TokenRepository
import org.jdbi.v3.core.Handle
import java.time.LocalDate

class JdbiTokenRepository(private val handle: Handle) : TokenRepository {

    override fun createToken(tokenHash: String, lastUsed: LocalDate, userId: Int) {
        handle.createUpdate(
            """
                INSERT INTO dbo.token(hash, last_used, user_id)
                VALUES (:token_hash, :last_used, :userId)
            """
        )
            .bind("token_hash", tokenHash)
            .bind("last_used", lastUsed)
            .bind("userId", userId)
            .execute()
    }

    override fun deleteToken(userId: Int) {
        handle.createUpdate(
            """
                UPDATE dbo.token
                SET hash = NULL
                WHERE user_id = :userId
            """
        )
            .bind("userId", userId)
            .execute()
    }
}
