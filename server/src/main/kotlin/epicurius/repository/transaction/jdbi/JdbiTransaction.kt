package epicurius.repository.transaction.jdbi

import epicurius.repository.jdbi.user.JdbiTokenRepository
import epicurius.repository.jdbi.fridge.JdbiFridgeRepository
import epicurius.repository.jdbi.mealPlanner.JdbiMealPlannerRepository
import epicurius.repository.jdbi.recipe.JdbiRecipeRepository
import epicurius.repository.jdbi.user.JdbiUserRepository
import epicurius.repository.transaction.Transaction
import org.jdbi.v3.core.Handle

class JdbiTransaction(handle: Handle) : Transaction {
    override val userRepository = JdbiUserRepository(handle)
    override val tokenRepository = JdbiTokenRepository(handle)
    override val fridgeRepository = JdbiFridgeRepository(handle)
    override val recipeRepository = JdbiRecipeRepository(handle)
    override val mealPlannerRepository = JdbiMealPlannerRepository(handle)
}
