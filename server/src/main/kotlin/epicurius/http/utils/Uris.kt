package epicurius.http.utils

object Uris {

    const val PREFIX = "/api"

    object User {
        const val SIGNUP = "/signup"
        const val LOGIN = "/login"
        const val LOGOUT = "/logout"
        const val USER = "/user"
        const val FOLLOW = "/follow/{usernameToFollow}"
        const val FOLLOWERS = "/followers"
        const val FOLLOWING = "/following"
        const val FOLLOWING_REQUESTS = "/following/requests"
        const val UNFOLLOW = "/unfollow/{usernameToUnfollow}"
        const val RESET_PASSWORD = "/reset"
        const val UPDATE_INTOLERANCES = "/intolerances/add"
        const val GET_INTOLERANCES = "/intolerances/get"
        const val UPDATE_DIET = "/diet/add"
        const val GET_DIET = "/diet/get"
    }
}