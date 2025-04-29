package epicurius.http.ingredients.models.output

data class GetIngredientsOutputModel(val ingredients: List<String>)

typealias GetIngredientsFromPictureOutputModel = GetIngredientsOutputModel