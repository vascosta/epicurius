package epicurius.repository.spoonacular.models

import kotlinx.serialization.Serializable

@Serializable
data class SpoonacularIngredientModel(val name: String, val image: String)