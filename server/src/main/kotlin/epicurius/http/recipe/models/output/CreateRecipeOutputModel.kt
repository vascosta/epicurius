package epicurius.http.recipe.models.output

import epicurius.domain.recipe.Recipe

data class CreateRecipeOutputModel(val recipe: Recipe)

typealias GetRecipeOutputModel = CreateRecipeOutputModel

typealias UpdateRecipeOutputModel = CreateRecipeOutputModel
