package epicurius.repository.jdbi.recipe

import epicurius.repository.jdbi.recipe.models.JdbiRecipeModel
import epicurius.domain.recipe.RecipeProfile
import epicurius.domain.recipe.SearchRecipesModel

interface RecipeRepository {

    fun createRecipe(recipeInfo: JdbiRecipeModel): Int

    fun searchRecipes(userId: Int, form: SearchRecipesModel): List<RecipeProfile>
}
