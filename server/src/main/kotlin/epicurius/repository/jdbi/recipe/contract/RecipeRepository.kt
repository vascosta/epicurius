package epicurius.repository.jdbi.recipe.contract

import epicurius.domain.Diet
import epicurius.domain.Intolerance
import epicurius.domain.PagingParams
import epicurius.domain.recipe.MealType
import epicurius.domain.recipe.SearchRecipesModel
import epicurius.repository.jdbi.recipe.models.JdbiCreateRecipeModel
import epicurius.repository.jdbi.recipe.models.JdbiRecipeInfo
import epicurius.repository.jdbi.recipe.models.JdbiRecipeModel
import epicurius.repository.jdbi.recipe.models.JdbiUpdateRecipeModel

interface RecipeRepository {

    fun createRecipe(recipeInfo: JdbiCreateRecipeModel): Int

    fun getRecipeById(recipeId: Int): JdbiRecipeModel?
    fun getRandomRecipesFromPublicUsers(
        mealType: MealType,
        intolerances: List<Intolerance>,
        diets: List<Diet>,
        limit: Int
    ): List<JdbiRecipeInfo>
    fun searchRecipes(userId: Int, form: SearchRecipesModel, pagingParams: PagingParams): List<JdbiRecipeInfo>

    fun updateRecipe(recipeInfo: JdbiUpdateRecipeModel): JdbiRecipeModel

    fun deleteRecipe(recipeId: Int)
}
