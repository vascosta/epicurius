import epicurius.domain.user.SocialUser

interface UserFirestoreRepository {

    fun createUserFollowersAndFollowing(username: String, privacy: Boolean)

    fun getFollowers(username: String): List<SocialUser>
    fun getFollowing(username: String): List<SocialUser>
    fun getFollowRequests(username: String): List<SocialUser>

    fun addFollowing(username: String, usernameToFollow: String)

    fun removeFollowing(username: String, usernameToUnfollow: String)
}
