package epicurius.services.rateRecipe

import epicurius.domain.exceptions.RecipeNotAccessible
import epicurius.domain.exceptions.RecipeNotFound
import epicurius.domain.exceptions.RecipesAuthorCannotRateSelf
import epicurius.domain.exceptions.UserAlreadyRated
import epicurius.repository.jdbi.recipe.models.JdbiRecipeModel
import epicurius.repository.transaction.TransactionManager
import org.springframework.stereotype.Component

@Component
class RateRecipeService(private val tm: TransactionManager) {

    fun rateRecipe(userId: Int, username: String, recipeId: Int, rating: Int) {
        val recipe = checkIfRecipeExists(recipeId) ?: throw RecipeNotFound()
        if (userId == recipe.authorId) throw RecipesAuthorCannotRateSelf()
        checkRecipeAccessibility(recipe.authorUsername, username)
        if (checkIfUserAlreadyRated(userId, recipeId)) throw UserAlreadyRated(userId, recipeId)

        tm.run { it.rateRecipeRepository.rateRecipe(recipeId, userId, rating) }
    }

    private fun checkIfRecipeExists(recipeId: Int): JdbiRecipeModel? =
        tm.run { it.recipeRepository.getRecipe(recipeId) }

    private fun checkRecipeAccessibility(authorUsername: String, username: String) {
        if (!tm.run { it.userRepository.checkUserVisibility(authorUsername, username) })
            throw RecipeNotAccessible(authorUsername)
    }

    private fun checkIfUserAlreadyRated(userId: Int, recipeId: Int) =
        tm.run { it.rateRecipeRepository.checkIfUserAlreadyRated(userId, recipeId) }

    /*
    private fun checkIfUserAlreadyRated(userId: Int, recipeId: Int): Boolean {
        return tm.run { it.recipeRepository.checkIfUserAlreadyRated(userId, recipeId) }
    }

     */
}