package epicurius.repository.firestore.recipe

import com.google.cloud.firestore.DocumentReference
import com.google.cloud.firestore.Firestore
import epicurius.domain.recipe.Instructions
import epicurius.repository.firestore.recipe.models.FirestoreRecipeModel
import epicurius.repository.firestore.recipe.models.FirestoreUpdateRecipeModel
import epicurius.repository.firestore.utils.await
import epicurius.repository.firestore.utils.getMap
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class FirestoreRecipeRepository(private val firestore: Firestore) : RecipeRepository {

    override fun createRecipe(recipe: FirestoreRecipeModel) {
        getDocumentReference(RECIPES_COLLECTION, recipe.id.toString()).set(recipe).get()
    }

    override suspend fun getRecipe(recipeId: Int): FirestoreRecipeModel {
        val doc = withContext(Dispatchers.IO) {
            getDocumentReference(RECIPES_COLLECTION, recipeId.toString()).get().await()
        }

        val instructionsMap = getMap<String>(doc.get("instructions"))
        val stepsMap = getMap<String>(instructionsMap["steps"])

        return FirestoreRecipeModel(
            recipeId,
            doc.getString("description") ?: "",
            Instructions(stepsMap)
        )
    }

    override suspend fun updateRecipe(recipeInfo: FirestoreUpdateRecipeModel): FirestoreRecipeModel {
        val oldRecipe = getRecipe(recipeInfo.id)

        if (recipeInfo.description != null) {
            getDocumentReference(RECIPES_COLLECTION, recipeInfo.id.toString())
                .update(mapOf("description" to recipeInfo.description,)).get()

            return oldRecipe.copy(description = recipeInfo.description)
        } else if (recipeInfo.instructions != null) {
            getDocumentReference(RECIPES_COLLECTION, recipeInfo.id.toString())
                .update(mapOf("instructions" to recipeInfo.instructions)).get()

            return oldRecipe.copy(instructions = recipeInfo.instructions)
        }

        return oldRecipe
    }

    override fun deleteRecipe(recipeId: Int) {
        deleteDocument(getDocumentReference(RECIPES_COLLECTION, recipeId.toString()))
    }

    private fun getDocumentReference(collectionName: String, documentName: String) =
        firestore.collection(collectionName).document(documentName)

    private fun deleteDocument(document: DocumentReference) = document.delete().get()

    companion object {
        private const val RECIPES_COLLECTION = "Recipes"
    }
}
