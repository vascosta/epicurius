package epicurius.repository.jdbi.recipe

import epicurius.domain.recipe.CreateRecipeModel
import epicurius.domain.recipe.RecipeProfile
import epicurius.domain.recipe.SearchRecipesModel

interface RecipePostgresRepository {

    fun createRecipe(recipeInfo: CreateRecipeModel): Int

    fun searchRecipes(userId: Int, form: SearchRecipesModel): List<RecipeProfile>
}