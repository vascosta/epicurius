package epicurius.http.utils

object Uris {

    const val PREFIX = "/api"

    object User {
        const val SIGNUP = "/signup"
        const val LOGIN = "/login"
        const val LOGOUT = "/logout"
        const val USER = "/user"
        const val FOLLOW = "user/follow/{usernameToFollow}"
        const val FOLLOWERS = "user/followers"
        const val FOLLOWING = "user/following"
        const val FOLLOWING_REQUESTS = "user/following/requests"
        const val UNFOLLOW = "user/unfollow/{usernameToUnfollow}"
        const val RESET_PASSWORD = "/reset"
        const val INTOLERANCES = "/intolerances"
        const val DIET = "/diet"
        const val USER_PROFILE = "/user/profile"
    }
}