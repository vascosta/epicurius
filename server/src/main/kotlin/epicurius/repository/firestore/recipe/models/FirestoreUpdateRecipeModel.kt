package epicurius.repository.firestore.recipe.models

import epicurius.domain.recipe.Instructions

data class FirestoreUpdateRecipeModel(val id: Int, val description: String?, val instructions: Instructions?)
