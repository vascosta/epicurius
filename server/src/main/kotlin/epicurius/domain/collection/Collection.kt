package epicurius.domain.collection

import epicurius.domain.recipe.RecipeInfo

data class Collection(
    val id: Int,
    val name: String,
    val type: CollectionType,
    val recipes: List<RecipeInfo>
)
