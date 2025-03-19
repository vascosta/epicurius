
interface UserFirestoreRepository {

    fun createUserFollowersAndFollowing(username: String, privacy: Boolean)
    fun addFollowing(username: String, usernameToFollow: String)
}
