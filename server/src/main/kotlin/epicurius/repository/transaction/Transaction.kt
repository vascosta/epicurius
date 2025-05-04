package epicurius.repository.transaction

import epicurius.repository.jdbi.collection.contract.CollectionRepository
import epicurius.repository.jdbi.feed.contract.FeedRepository
import epicurius.repository.jdbi.fridge.contract.FridgeRepository
import epicurius.repository.jdbi.mealPlanner.contract.MealPlannerRepository
import epicurius.repository.jdbi.rateRecipe.contract.RateRecipeRepository
import epicurius.repository.jdbi.recipe.contract.RecipeRepository
import epicurius.repository.jdbi.token.contract.TokenRepository
import epicurius.repository.jdbi.user.contract.UserRepository

interface Transaction {
    val userRepository: UserRepository
    val tokenRepository: TokenRepository
    val fridgeRepository: FridgeRepository
    val recipeRepository: RecipeRepository
    val rateRecipeRepository: RateRecipeRepository
    val mealPlannerRepository: MealPlannerRepository
    val feedRepository: FeedRepository
    val collectionRepository: CollectionRepository
}
