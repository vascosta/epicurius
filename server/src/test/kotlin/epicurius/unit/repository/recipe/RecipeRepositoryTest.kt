package epicurius.unit.repository.recipe

import epicurius.domain.recipe.SearchRecipesModel
import epicurius.repository.firestore.recipe.models.FirestoreRecipeModel
import epicurius.repository.firestore.recipe.models.FirestoreUpdateRecipeModel
import epicurius.repository.jdbi.recipe.models.JdbiCreateRecipeModel
import epicurius.repository.jdbi.recipe.models.JdbiUpdateRecipeModel
import epicurius.unit.repository.RepositoryTest
import epicurius.utils.createTestRecipe
import epicurius.utils.createTestUser

open class RecipeRepositoryTest : RepositoryTest() {

    companion object {
        val testUser = createTestUser(tm)
        val testAuthor = createTestUser(tm)
        val testRecipe = createTestRecipe(tm, fs, testAuthor)

        fun jdbiCreateRecipe(recipeInfo: JdbiCreateRecipeModel) = tm.run { it.recipeRepository.createRecipe(recipeInfo) }

        fun firestoreCreateRecipe(recipeInfo: FirestoreRecipeModel) {
            fs.recipeRepository.createRecipe(recipeInfo)
        }

        fun getJdbiRecipe(recipeId: Int) = tm.run { it.recipeRepository.getRecipe(recipeId) }

        suspend fun getFirestoreRecipe(recipeId: Int) = fs.recipeRepository.getRecipe(recipeId)

        fun searchRecipes(userId: Int, form: SearchRecipesModel) =
            tm.run { it.recipeRepository.searchRecipes(userId, form) }

        fun updateJdbiRecipe(recipeInfo: JdbiUpdateRecipeModel) =
            tm.run { it.recipeRepository.updateRecipe(recipeInfo) }

        suspend fun updateFirestoreRecipe(recipeInfo: FirestoreUpdateRecipeModel) =
            fs.recipeRepository.updateRecipe(recipeInfo)

        fun deleteJdbiRecipe(recipeId: Int) = tm.run { it.recipeRepository.deleteRecipe(recipeId) }
        fun deleteFirestoreRecipe(recipeId: Int) = fs.recipeRepository.deleteRecipe(recipeId)
    }
}
