package epicurius.repository.transaction.jdbi

import epicurius.repository.jdbi.collection.JdbiCollectionRepository
import epicurius.repository.jdbi.feed.JdbiFeedRepository
import epicurius.repository.jdbi.fridge.JdbiFridgeRepository
import epicurius.repository.jdbi.mealPlanner.JdbiMealPlannerRepository
import epicurius.repository.jdbi.rateRecipe.JdbiRateRecipeRepository
import epicurius.repository.jdbi.recipe.JdbiRecipeRepository
import epicurius.repository.jdbi.token.JdbiTokenRepository
import epicurius.repository.jdbi.user.JdbiUserRepository
import epicurius.repository.transaction.Transaction
import org.jdbi.v3.core.Handle

class JdbiTransaction(handle: Handle) : Transaction {
    override val userRepository = JdbiUserRepository(handle)
    override val tokenRepository = JdbiTokenRepository(handle)
    override val fridgeRepository = JdbiFridgeRepository(handle)
    override val recipeRepository = JdbiRecipeRepository(handle)
    override val rateRecipeRepository = JdbiRateRecipeRepository(handle)
    override val mealPlannerRepository = JdbiMealPlannerRepository(handle)
    override val feedRepository = JdbiFeedRepository(handle)
    override val collectionRepository = JdbiCollectionRepository(handle)
}
