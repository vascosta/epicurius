package epicurius.repository.jdbi.token.contract

import java.time.LocalDate

interface TokenRepository {

    fun createToken(tokenHash: String, lastUsed: LocalDate, userId: Int)
    fun deleteToken(userId: Int)
}
