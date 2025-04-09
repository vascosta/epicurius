package epicurius.repository.firestore.recipe

import epicurius.repository.firestore.recipe.models.FirestoreRecipeModel

interface RecipeRepository {
    fun createRecipe(recipe: FirestoreRecipeModel)
    fun getRecipe(recipeId: Int): FirestoreRecipeModel
    fun deleteRecipe(recipeId: Int)
}
