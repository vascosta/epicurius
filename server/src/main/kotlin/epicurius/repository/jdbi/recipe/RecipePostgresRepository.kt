package epicurius.repository.jdbi.recipe

import epicurius.domain.recipe.RecipePostrgresModel
import epicurius.domain.recipe.RecipeProfile
import epicurius.domain.recipe.SearchRecipes

interface RecipePostgresRepository {

    fun createRecipe(recipe: RecipePostrgresModel): Int

    fun searchRecipes(userId: Int, form: SearchRecipes): List<RecipeProfile>
}