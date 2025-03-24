package epicurius.repository.firestore

import RecipeFirestoreRepository
import com.google.cloud.firestore.Firestore

class FirestoreRecipeRepository(private val firestore: Firestore): RecipeFirestoreRepository {

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

    private fun getDocumentReference(collectionName: String, name: String) =
        firestore.collection(collectionName).document(name)

/*    private inline fun <reified T> getSnapshotValue(snapshot: DocumentSnapshot, documentName: String) =
        snapshot.get(documentName) as T

    private inline fun <reified T> getSnapshotValue(snapshot: DocumentSnapshot, documentName: String): T? {
        return try {
            snapshot.get(documentName) as? T
        } catch (e: Exception) {
            null
        }
    }*/

    companion object {
        private const val RECIPE_INSTRUCTIONS_COLLECTION = "RecipeInstructions"
    }
}