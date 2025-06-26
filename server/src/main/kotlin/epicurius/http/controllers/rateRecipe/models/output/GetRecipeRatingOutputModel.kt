package epicurius.http.controllers.rateRecipe.models.output

data class GetRecipeRatingOutputModel(val rating: Double)

typealias RateRecipeOutputModel = GetRecipeRatingOutputModel

typealias UpdateUserRecipeRatingOutputModel = GetRecipeRatingOutputModel
