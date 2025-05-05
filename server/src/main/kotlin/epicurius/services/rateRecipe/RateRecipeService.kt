package epicurius.services.rateRecipe

import epicurius.domain.exceptions.AuthorCannotDeleteRating
import epicurius.domain.exceptions.AuthorCannotRateOwnRecipe
import epicurius.domain.exceptions.AuthorCannotUpdateRating
import epicurius.domain.exceptions.RecipeNotAccessible
import epicurius.domain.exceptions.RecipeNotFound
import epicurius.domain.exceptions.UserAlreadyRated
import epicurius.domain.exceptions.UserHasNotRated
import epicurius.repository.jdbi.recipe.models.JdbiRecipeModel
import epicurius.repository.transaction.TransactionManager
import org.springframework.stereotype.Component

@Component
class RateRecipeService(private val tm: TransactionManager) {

    fun getRecipeRate(username: String, recipeId: Int): Double {
        val recipe = checkIfRecipeExists(recipeId) ?: throw RecipeNotFound()
        checkRecipeAccessibility(recipe.authorUsername, username)
        return tm.run { it.rateRecipeRepository.getRecipeRate(recipeId) }
    }

    fun rateRecipe(userId: Int, username: String, recipeId: Int, rating: Int) {
        val recipe = checkIfRecipeExists(recipeId) ?: throw RecipeNotFound()
        if (userId == recipe.authorId) throw AuthorCannotRateOwnRecipe()
        checkRecipeAccessibility(recipe.authorUsername, username)
        if (checkIfUserAlreadyRated(userId, recipeId)) throw UserAlreadyRated(userId, recipeId)

        tm.run { it.rateRecipeRepository.rateRecipe(recipeId, userId, rating) }
    }

    fun updateRecipeRate(userId: Int, username: String, recipeId: Int, rating: Int) {
        val recipe = checkIfRecipeExists(recipeId) ?: throw RecipeNotFound()
        if (userId == recipe.authorId) throw AuthorCannotUpdateRating()
        checkRecipeAccessibility(recipe.authorUsername, username)
        if (!checkIfUserAlreadyRated(userId, recipeId)) throw UserHasNotRated(userId, recipeId)

        tm.run { it.rateRecipeRepository.updateRecipeRate(recipeId, userId, rating) }
    }

    fun deleteRecipeRate(userId: Int, username: String, recipeId: Int) {
        val recipe = checkIfRecipeExists(recipeId) ?: throw RecipeNotFound()
        if (userId == recipe.authorId) throw AuthorCannotDeleteRating()
        checkRecipeAccessibility(recipe.authorUsername, username)
        if (!checkIfUserAlreadyRated(userId, recipeId)) throw UserHasNotRated(userId, recipeId)

        tm.run { it.rateRecipeRepository.deleteRecipeRate(recipeId, userId) }
    }

    private fun checkIfRecipeExists(recipeId: Int): JdbiRecipeModel? =
        tm.run { it.recipeRepository.getRecipe(recipeId) }

    private fun checkRecipeAccessibility(authorUsername: String, username: String) {
        if (!tm.run { it.userRepository.checkUserVisibility(authorUsername, username) })
            throw RecipeNotAccessible(authorUsername)
    }

    private fun checkIfUserAlreadyRated(userId: Int, recipeId: Int) =
        tm.run { it.rateRecipeRepository.checkIfUserAlreadyRated(userId, recipeId) }
}
