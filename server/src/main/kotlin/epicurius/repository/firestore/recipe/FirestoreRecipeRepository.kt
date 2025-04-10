package epicurius.repository.firestore.recipe

import com.google.cloud.firestore.DocumentReference
import com.google.cloud.firestore.Firestore
import epicurius.repository.firestore.recipe.models.FirestoreRecipeModel
import epicurius.repository.firestore.recipe.models.FirestoreUpdateRecipeModel

class FirestoreRecipeRepository(private val firestore: Firestore) : RecipeRepository {

    override fun createRecipe(recipe: FirestoreRecipeModel) {
        getDocumentReference(RECIPES_COLLECTION, recipe.id.toString()).set(recipe).get()
    }

    override fun getRecipe(recipeId: Int): FirestoreRecipeModel {
        TODO("Not yet implemented")
    }

    override fun updateRecipe(recipeInfo: FirestoreUpdateRecipeModel): FirestoreRecipeModel {
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

/*    override fun addRecipeInstructions(user: Strin, usernameToFollow: SocialUser) {
        val future: ApiFuture<Unit> = firestore.runTransaction { transaction ->
            val userRef = getDocumentReference(FOLLOWERS_AND_FOLLOWING_COLLECTION, user.username)
            val userSnapshot = transaction.get(userRef).get()
            val userToFollow = getDocumentReference(FOLLOWERS_AND_FOLLOWING_COLLECTION, usernameToFollow.username)
            val userToFollowSnapshot = transaction.get(userToFollow).get()

            if (!userSnapshot.exists()) {
                throw UserNotFound(user.username)
            }

            if (!userToFollowSnapshot.exists()) {
                throw UserNotFound(usernameToFollow.username)
            }

            val privacy = userToFollowSnapshot.get("privacy") as Boolean

            if (privacy) {
                val followingRequests = userToFollowSnapshot.get("followersRequests") as List<Map<String, String>>
                if (!followingRequests.any { it["username"] == user.username }) {
                    val newRequest = mapOf(
                        "username" to user.username,
                        "profilePictureName" to user.profilePictureName
                    )
                    transaction.update(userToFollow, "followersRequests", followingRequests + newRequest)
                }
            }
            else {
                val followers = userToFollowSnapshot.get("followers") as List<Map<String, String>>
                if (!followers.any { it["username"] == user.username }) {
                    val newFollower = mapOf(
                        "username" to user.username,
                        "profilePictureName" to user.profilePictureName
                    )
                    transaction.update(userToFollow, "followers", followers + newFollower)
                }

                val following = userSnapshot.get("following") as List<Map<String, String>>
                if (!following.any { it["username"] == usernameToFollow.username }) {
                    val newFollowing = mapOf(
                        "username" to usernameToFollow.username,
                        "profilePictureName" to usernameToFollow.profilePictureName
                    )
                    transaction.update(userRef, "following", following + newFollowing)
                }
            }
        }

        future.get()
    }*/

    private fun getDocumentReference(collectionName: String, documentName: String) =
        firestore.collection(collectionName).document(documentName)

    private fun deleteDocument(document: DocumentReference) =
        document.delete().get()

    companion object {
        private const val RECIPES_COLLECTION = "Recipes"
    }
}
