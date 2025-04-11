package epicurius.repository.firestore.recipe

import epicurius.repository.firestore.recipe.models.FirestoreRecipeModel
import epicurius.repository.firestore.recipe.models.FirestoreUpdateRecipeModel

interface RecipeRepository {
    fun createRecipe(recipe: FirestoreRecipeModel)
    suspend fun getRecipe(recipeId: Int): FirestoreRecipeModel
    suspend fun updateRecipe(recipeInfo: FirestoreUpdateRecipeModel): FirestoreRecipeModel
    fun deleteRecipe(recipeId: Int)
}
