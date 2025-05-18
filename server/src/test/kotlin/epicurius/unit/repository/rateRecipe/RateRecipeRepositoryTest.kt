package epicurius.unit.repository.rateRecipe

import epicurius.domain.recipe.Recipe
import epicurius.domain.user.User
import epicurius.unit.repository.RepositoryTest
import epicurius.utils.createTestRecipe
import epicurius.utils.createTestUser
import org.junit.jupiter.api.BeforeEach

open class RateRecipeRepositoryTest : RepositoryTest() {

    lateinit var testUserPublic : User
    lateinit var testUserPrivate : User
    lateinit var testRecipe : Recipe

    @BeforeEach
    fun setup() {
        testUserPublic = createTestUser(tm).user
        testUserPrivate = createTestUser(tm, true).user
        testRecipe = createTestRecipe(tm, fs, testUserPublic)
    }

    companion object {

        fun getRecipeRate(recipeId: Int) =
            tm.run { it.rateRecipeRepository.getRecipeRate(recipeId) }

        fun getRecipeUserRate(recipeId: Int, userId: Int) =
            tm.run { it.rateRecipeRepository.getUserRecipeRate(recipeId, userId) }

        fun rateRecipe(recipeId: Int, userId: Int, rating: Int) =
            tm.run { it.rateRecipeRepository.rateRecipe(recipeId, userId, rating) }

        fun updateRecipeRate(recipeId: Int, userId: Int, rating: Int) =
            tm.run { it.rateRecipeRepository.updateRecipeRate(recipeId, userId, rating) }

        fun deleteRecipeRate(recipeId: Int, userId: Int) =
            tm.run { it.rateRecipeRepository.deleteRecipeRate(recipeId, userId) }
    }
}
