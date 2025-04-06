package epicurius.repository.transaction

import TokenRepository
import epicurius.repository.jdbi.fridge.FridgeRepository
import epicurius.repository.jdbi.recipe.RecipeRepository
import epicurius.repository.jdbi.user.UserRepository

interface Transaction {
    val userRepository: UserRepository
    val tokenRepository: TokenRepository
    val fridgeRepository: FridgeRepository
    val recipeRepository: RecipeRepository
}
