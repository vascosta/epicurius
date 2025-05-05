package epicurius.repository.firestore.recipe

import com.google.cloud.firestore.DocumentReference
import com.google.cloud.firestore.Firestore
import epicurius.domain.exceptions.RecipeNotFound
import epicurius.domain.recipe.Instructions
import epicurius.repository.firestore.recipe.contract.RecipeRepository
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

    override suspend fun getRecipeById(recipeId: Int): FirestoreRecipeModel? {
        val doc = withContext(Dispatchers.IO) {
            getDocumentReference(RECIPES_COLLECTION, recipeId.toString()).get().await()
        }

        if (!doc.exists()) return null

        val instructionsMap = getMap<String>(doc.get("instructions"))
        val stepsMap = getMap<String>(instructionsMap["steps"])

        return FirestoreRecipeModel(
            recipeId,
            doc.getString("description") ?: "", // description cannot be null so the empty string will not happen
            Instructions(stepsMap)
        )
    }

    override suspend fun updateRecipe(recipeInfo: FirestoreUpdateRecipeModel): FirestoreRecipeModel {
        val oldRecipe = getRecipeById(recipeInfo.id) ?: throw RecipeNotFound()

        if (recipeInfo.description != null) {
            withContext(Dispatchers.IO) {
                getDocumentReference(RECIPES_COLLECTION, recipeInfo.id.toString())
                    .update(mapOf("description" to recipeInfo.description,)).get()
            }
        }

        if (recipeInfo.instructions != null) {
            withContext(Dispatchers.IO) {
                getDocumentReference(RECIPES_COLLECTION, recipeInfo.id.toString())
                    .update(mapOf("instructions" to recipeInfo.instructions)).get()
            }
        }

        return FirestoreRecipeModel(recipeInfo.id, recipeInfo.description ?: oldRecipe.description, recipeInfo.instructions ?: oldRecipe.instructions)
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
