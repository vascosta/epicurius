package epicurius.repository.jdbi.collection.models

import epicurius.domain.collection.CollectionType
import epicurius.repository.jdbi.recipe.models.JdbiRecipeInfo

data class JdbiCollectionModel(
    val id: Int,
    val ownerId: Int,
    val name: String,
    val type: CollectionType,
    val recipes: List<JdbiRecipeInfo>
)
