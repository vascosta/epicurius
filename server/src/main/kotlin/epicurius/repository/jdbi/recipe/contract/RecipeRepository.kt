package epicurius.repository.jdbi.recipe.contract

import epicurius.domain.recipe.MealType
import epicurius.domain.recipe.SearchRecipesModel
import epicurius.repository.jdbi.recipe.models.JdbiCreateRecipeModel
import epicurius.repository.jdbi.recipe.models.JdbiRecipeInfo
import epicurius.repository.jdbi.recipe.models.JdbiRecipeModel
import epicurius.repository.jdbi.recipe.models.JdbiUpdateRecipeModel

interface RecipeRepository {

    fun createRecipe(recipeInfo: JdbiCreateRecipeModel): Int

    fun getRecipe(recipeId: Int): JdbiRecipeModel?
    fun getRandomRecipeFromFollowing(userId: Int, mealType: MealType): JdbiRecipeModel?
    fun getRandomRecipeFromPublicUsers(mealType: MealType): JdbiRecipeModel?
    fun searchRecipes(userId: Int, form: SearchRecipesModel): List<JdbiRecipeInfo>
    fun searchRecipesByIngredients(userId: Int, ingredientsList: List<String>): List<JdbiRecipeInfo>

    fun updateRecipe(recipeInfo: JdbiUpdateRecipeModel): JdbiRecipeModel

    fun deleteRecipe(recipeId: Int)
}
