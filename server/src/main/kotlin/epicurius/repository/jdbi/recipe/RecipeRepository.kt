package epicurius.repository.jdbi.recipe

import epicurius.domain.recipe.RecipeProfile
import epicurius.domain.recipe.SearchRecipesModel
import epicurius.repository.jdbi.recipe.models.JdbiRecipeModel

interface RecipeRepository {

    fun createRecipe(recipeInfo: JdbiRecipeModel): Int

    fun searchRecipes(userId: Int, form: SearchRecipesModel): List<RecipeProfile>
}
