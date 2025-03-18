
interface UserFirestoreRepository {

    fun addFollowing(username: String, usernameToFollow: String)
}
