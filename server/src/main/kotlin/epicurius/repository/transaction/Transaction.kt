package epicurius.repository.transaction

import TokenRepository
import epicurius.repository.jdbi.fridge.FridgePostgresRepository
import epicurius.repository.jdbi.recipe.RecipePostgresRepository
import epicurius.repository.jdbi.user.UserPostgresRepository

interface Transaction {
    val userRepository: UserPostgresRepository
    val tokenRepository: TokenRepository
    val fridgeRepository: FridgePostgresRepository
    val recipeRepository: RecipePostgresRepository
}
