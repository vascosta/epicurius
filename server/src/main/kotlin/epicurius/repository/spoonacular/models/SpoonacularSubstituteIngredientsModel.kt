package epicurius.repository.spoonacular.models

import kotlinx.serialization.Serializable

@Serializable
data class SpoonacularSubstituteIngredientsModel(
    val status: String,
    val ingredient: String,
    val substitutes: List<String>,
    val message: String
)
