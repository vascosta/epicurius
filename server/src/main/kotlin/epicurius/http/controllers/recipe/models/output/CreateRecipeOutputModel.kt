package epicurius.http.controllers.recipe.models.output

import epicurius.domain.recipe.Recipe

data class CreateRecipeOutputModel(val recipe: Recipe)

typealias GetRecipeOutputModel = CreateRecipeOutputModel
