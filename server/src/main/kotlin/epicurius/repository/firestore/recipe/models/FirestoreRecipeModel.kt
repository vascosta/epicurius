package epicurius.repository.firestore.recipe.models

import epicurius.domain.recipe.Instructions

data class FirestoreRecipeModel(val id: Int, val description: String, val instructions: Instructions)
