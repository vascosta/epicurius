package android.epicurius.domain.collection

import android.epicurius.domain.recipe.RecipeInfo
import epicurius.domain.collection.CollectionType


data class Collection(
    val id: Int,
    val name: String,
    val type: CollectionType,
    val recipes: List<RecipeInfo>
)
