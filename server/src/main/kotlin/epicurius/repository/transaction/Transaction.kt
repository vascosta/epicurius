package epicurius.repository.transaction

import epicurius.repository.jdbi.fridge.contract.FridgeRepository
import epicurius.repository.jdbi.mealPlanner.contract.MealPlannerRepository
import epicurius.repository.jdbi.recipe.contract.RecipeRepository
import epicurius.repository.jdbi.token.contract.TokenRepository
import epicurius.repository.jdbi.user.contract.UserRepository

interface Transaction {
    val userRepository: UserRepository
    val tokenRepository: TokenRepository
    val fridgeRepository: FridgeRepository
    val recipeRepository: RecipeRepository
    val mealPlannerRepository: MealPlannerRepository
}
