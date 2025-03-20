package epicurius.http.utils

object Uris {

    const val PREFIX = "/api"

    object User {
        const val SIGNUP = "/signup"
        const val LOGIN = "/login"
        const val LOGOUT = "/logout"
        const val FOLLOW = "/follow/{usernameToFollow}"
        const val UNFOLLOW = "/unfollow/{usernameToUnfollow}"
        const val RESET_PASSWORD = "/reset"
        const val ADD_INTOLERANCES = "/intolerances/add"
        const val GET_INTOLERANCES = "/intolerances/get"
    }
}