package epicurius.unit.repository.rateRecipe

import epicurius.unit.repository.RepositoryTest
import epicurius.utils.createTestRecipe
import epicurius.utils.createTestUser

open class RateRecipeRepositoryTest : RepositoryTest() {

    companion object {

        val testUserPublic = createTestUser(tm)
        val testUserPrivate = createTestUser(tm, true)
        private val testAuthor = createTestUser(tm)
        val testRecipe = createTestRecipe(tm, fs, testAuthor.user)
        val testRecipe2 = createTestRecipe(tm, fs, testAuthor.user)
        val testRecipe3 = createTestRecipe(tm, fs, testAuthor.user)
        val testRecipe4 = createTestRecipe(tm, fs, testAuthor.user)
        val testRecipe5 = createTestRecipe(tm, fs, testAuthor.user)

        fun getRecipeRate(recipeId: Int) =
            tm.run { it.rateRecipeRepository.getRecipeRate(recipeId) }

        fun rateRecipe(recipeId: Int, userId: Int, rating: Int) =
            tm.run { it.rateRecipeRepository.rateRecipe(recipeId, userId, rating) }

        fun updateRecipeRate(recipeId: Int, userId: Int, rating: Int) =
            tm.run { it.rateRecipeRepository.updateRecipeRate(recipeId, userId, rating) }

        fun deleteRecipeRate(recipeId: Int, userId: Int) =
            tm.run { it.rateRecipeRepository.deleteRecipeRate(recipeId, userId) }
    }
}
