package android.epicurius.services.http.api.ingredients.models.output

data class GetIngredientsOutputModel(val ingredients: List<String>)

typealias GetSubstituteIngredientsOutputModel = GetIngredientsOutputModel

typealias IdentifyIngredientsInPictureOutputModel = GetIngredientsOutputModel
