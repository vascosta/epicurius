package epicurius.repository.transaction.jdbi

import epicurius.repository.jdbi.JdbiFridgeRepository
import epicurius.repository.jdbi.JdbiRecipeRepository
import epicurius.repository.jdbi.JdbiTokenRepository
import epicurius.repository.jdbi.JdbiUserRepository
import epicurius.repository.transaction.Transaction
import org.jdbi.v3.core.Handle

class JdbiTransaction(handle: Handle) : Transaction {
    override val userRepository = JdbiUserRepository(handle)
    override val tokenRepository = JdbiTokenRepository(handle)
    override val fridgeRepository = JdbiFridgeRepository(handle)
    override val recipeRepository = JdbiRecipeRepository(handle)
}
