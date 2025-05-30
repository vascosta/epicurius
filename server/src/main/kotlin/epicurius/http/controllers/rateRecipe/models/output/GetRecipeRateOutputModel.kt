package epicurius.http.controllers.rateRecipe.models.output

data class GetRecipeRateOutputModel(val rating: Double)

typealias RateRecipeOutputModel = GetRecipeRateOutputModel

typealias UpdateRecipeRateOutputModel = GetRecipeRateOutputModel
