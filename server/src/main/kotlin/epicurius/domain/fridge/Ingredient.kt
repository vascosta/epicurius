package epicurius.domain.fridge

import kotlinx.serialization.Serializable

@Serializable
data class Ingredient(val name: String, val image: String)