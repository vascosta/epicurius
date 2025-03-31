package epicurius.http.utils

object Uris {

    const val PREFIX = "/api"

    object User {
        const val SIGNUP = "/signup"
        const val LOGIN = "/login"
        const val LOGOUT = "/logout"
        const val USER = "/user"
        const val USER_INTOLERANCES = "/user/intolerances"
        const val USER_DIETS = "/user/diets"
        const val USER_RESET_PASSWORD = "/user/resetPassword"
        const val USER_PROFILE = "/user/profile"
        const val USER_PROFILE_PICTURE = "/user/profile/picture"
        const val USERS = "/users"
        const val USER_FOLLOW = "/user/follow"
        const val USER_UNFOLLOW = "/user/unfollow"
        const val USER_FOLLOW_REQUESTS = "/user/follow/requests"
        const val USER_FOLLOWERS = "/user/followers"
        const val USER_FOLLOWING = "/user/following"
    }

    object Fridge {
        const val FRIDGE = "/fridge"
        const val PRODUCTS = "/products"
        const val NEW_PRODUCT = "/fridge/product/{entryNumber}"
        const val OPEN_PRODUCT = "/fridge/open/{entryNumber}"
    }
}
