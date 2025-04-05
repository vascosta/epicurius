package epicurius.services

import epicurius.domain.recipe.RecipeProfile
import epicurius.domain.recipe.SearchRecipes
import epicurius.http.recipe.models.input.SearchRecipesInputModel
import epicurius.repository.spoonacular.SpoonacularManager
import epicurius.repository.transaction.TransactionManager
import org.springframework.stereotype.Component

@Component
class RecipeService(
    private val tm: TransactionManager,
    private val sm: SpoonacularManager
) {

    fun searchRecipes(userId: Int, name: String?, form: SearchRecipesInputModel): List<RecipeProfile> {
        val fillForm = form.toSearchRecipe(name)
        return tm.run {
            it.recipeRepository.searchRecipes(userId, fillForm)
        }
    }

    private fun SearchRecipesInputModel.toSearchRecipe(name: String?) =
        SearchRecipes(
            name,
            this.cuisine,
            this.mealType,
            this.ingredients,
            this.intolerances,
            this.diets,
            this.minCalories,
            this.maxCalories,
            this.minCarbs,
            this.maxCarbs,
            this.minFat,
            this.maxFat,
            this.minProtein,
            this.maxProtein,
            this.minTime,
            this.maxTime,
            this.maxResults
        )
}