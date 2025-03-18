package epicurius.repository.firestore

import UserFirestoreRepository
import com.google.cloud.firestore.Firestore

class FirestoreUserRepository(private val firestore: Firestore):UserFirestoreRepository {
    override fun addFollowing(username: String, usernameToFollow: String) {
        firestore.runTransaction { transaction ->
            val userRef = firestore.collection(FOLLOWERS_AND_FOLLOWING_COLLECTION).document(username)
            val userSnapshot = transaction.get(userRef).get()
            val userToFollow = firestore.collection(FOLLOWERS_AND_FOLLOWING_COLLECTION).document(usernameToFollow)
            val userToFollowSnapshot = transaction.get(userToFollow).get()

            if (!userSnapshot.exists()) {
                throw IllegalStateException("$username not found")
            }

            if (!userToFollowSnapshot.exists()) {
                throw IllegalStateException("$usernameToFollow not found")
            }

            val privacy = userToFollowSnapshot.get("privacy") as Boolean

            if (privacy) {
                val followingRequests = userToFollowSnapshot.get("followingRequests") as List<String>
                if (!followingRequests.contains(username)) {
                    transaction.update(userToFollow, "followersRequests", followingRequests + username)
                }
            }
            else {
                val followers = userToFollowSnapshot.get("followers") as List<String>
                if (!followers.contains(username)) {
                    transaction.update(userToFollow, "followers", followers + username)
                }

                val following = userSnapshot.get("following") as List<String>
                if (!following.contains(usernameToFollow)) {
                    transaction.update(userRef, "following", following + usernameToFollow)
                }
            }
        }
    }

    companion object {
        private const val FOLLOWERS_AND_FOLLOWING_COLLECTION = "FollowersAndFollowing"
    }
}