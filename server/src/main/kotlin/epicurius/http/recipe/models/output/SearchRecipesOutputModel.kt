package epicurius.http.recipe.models.output

import epicurius.domain.recipe.RecipeInfo

data class SearchRecipesOutputModel(val recipes: List<RecipeInfo>)
