package epicurius.repository

import epicurius.domain.recipe.RecipeProfile
import epicurius.domain.recipe.SearchRecipes

interface RecipePostgresRepository {

    fun searchRecipes(userId: Int, form: SearchRecipes): List<RecipeProfile>
}