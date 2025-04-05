package epicurius.http.recipe.models.output

import epicurius.domain.recipe.RecipeProfile

data class SearchRecipesOutputModel(val recipes: List<RecipeProfile>)
