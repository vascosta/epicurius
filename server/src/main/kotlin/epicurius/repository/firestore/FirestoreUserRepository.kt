package epicurius.repository.firestore

import UserFirestoreRepository
import com.google.cloud.firestore.Firestore
import epicurius.domain.exceptions.UserNotFound

class FirestoreUserRepository(private val firestore: Firestore):UserFirestoreRepository {
    override fun createUserFollowersAndFollowing(username: String, privacy: Boolean) {
        val user = hashMapOf(
            "followers" to emptyList<String>(),
            "following" to emptyList<String>(),
            "followingRequests" to emptyList<String>(),
            "privacy" to privacy
        )

        firestore.collection(FOLLOWERS_AND_FOLLOWING_COLLECTION).document(username)
            .set(user).get()
    }

    override fun addFollowing(username: String, usernameToFollow: String) {
        firestore.runTransaction { transaction ->
            val userRef = firestore.collection(FOLLOWERS_AND_FOLLOWING_COLLECTION).document(username)
            val userSnapshot = transaction.get(userRef).get()
            val userToFollow = firestore.collection(FOLLOWERS_AND_FOLLOWING_COLLECTION).document(usernameToFollow)
            val userToFollowSnapshot = transaction.get(userToFollow).get()

            if (!userSnapshot.exists()) {
                throw UserNotFound(username)
            }

            if (!userToFollowSnapshot.exists()) {
                throw UserNotFound(usernameToFollow)
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

    override fun removeFollowing(username: String, usernameToUnfollow: String) {
        firestore.runTransaction { transaction ->
            val userRef = firestore.collection(FOLLOWERS_AND_FOLLOWING_COLLECTION).document(username)
            val userSnapshot = transaction.get(userRef).get()
            val userToUnfollow = firestore.collection(FOLLOWERS_AND_FOLLOWING_COLLECTION).document(usernameToUnfollow)
            val userToUnfollowSnapshot = transaction.get(userToUnfollow).get()

            if (!userSnapshot.exists()) {
                throw UserNotFound(username)
            }

            if (!userToUnfollowSnapshot.exists()) {
                throw UserNotFound(usernameToUnfollow)
            }

            val following = userSnapshot.get("following") as List<String>
            if (following.contains(usernameToUnfollow)) {
                transaction.update(userRef, "following", following - usernameToUnfollow)
            }

            val followers = userToUnfollowSnapshot.get("followers") as List<String>
            if (followers.contains(username)) {
                transaction.update(userToUnfollow, "followers", followers - username)
            }
        }
    }

    companion object {
        private const val FOLLOWERS_AND_FOLLOWING_COLLECTION = "FollowersAndFollowing"
    }
}