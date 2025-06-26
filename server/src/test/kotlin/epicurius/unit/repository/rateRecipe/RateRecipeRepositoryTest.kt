package epicurius.unit.repository.rateRecipe

import epicurius.domain.recipe.Recipe
import epicurius.domain.user.User
import epicurius.unit.repository.RepositoryTest
import epicurius.utils.createTestRecipe
import epicurius.utils.createTestUser
import org.junit.jupiter.api.BeforeEach

open class RateRecipeRepositoryTest : RepositoryTest() {

    lateinit var testUserPublic: User
    lateinit var testUserPrivate: User
    lateinit var testRecipe: Recipe

    @BeforeEach
    fun setup() {
        testUserPublic = createTestUser(tm).user
        testUserPrivate = createTestUser(tm, true).user
        testRecipe = createTestRecipe(tm, testUserPublic)
    }

    companion object {

        fun getRecipeRating(recipeId: Int) =
            tm.run { it.rateRecipeRepository.getRecipeRating(recipeId) }

        fun getUserRecipeRating(recipeId: Int, userId: Int) =
            tm.run { it.rateRecipeRepository.getUserRecipeRating(recipeId, userId) }

        fun rateRecipe(recipeId: Int, userId: Int, rating: Int) =
            tm.run { it.rateRecipeRepository.rateRecipe(recipeId, userId, rating) }

        fun updateUserRecipeRating(recipeId: Int, userId: Int, rating: Int) =
            tm.run { it.rateRecipeRepository.updateUserRecipeRating(recipeId, userId, rating) }

        fun deleteUserRecipeRating(recipeId: Int, userId: Int) =
            tm.run { it.rateRecipeRepository.deleteUserRecipeRating(recipeId, userId) }
    }
}
