package epicurius.repository.jdbi.recipe

import epicurius.domain.recipe.RecipeInfo
import epicurius.domain.recipe.SearchRecipesModel
import epicurius.repository.jdbi.recipe.models.JdbiCreateRecipeModel
import epicurius.repository.jdbi.recipe.models.JdbiRecipeModel
import epicurius.repository.jdbi.recipe.models.JdbiUpdateRecipeModel

interface RecipeRepository {

    fun createRecipe(recipeInfo: JdbiCreateRecipeModel): Int

    fun getRecipe(recipeId: Int): JdbiRecipeModel?
    fun searchRecipes(userId: Int, form: SearchRecipesModel): List<RecipeInfo>
    fun searchRecipesByIngredients(userId: Int, ingredientsList: List<String>): List<RecipeInfo>

    fun updateRecipe(recipeInfo: JdbiUpdateRecipeModel): JdbiRecipeModel

    fun deleteRecipe(recipeId: Int)
}
