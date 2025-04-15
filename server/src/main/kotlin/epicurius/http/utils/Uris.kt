package epicurius.http.utils

import org.springframework.web.util.UriTemplate

object Uris {

    const val PREFIX = "/api"

    object User {
        const val SIGNUP = "/signup"
        const val LOGIN = "/login"
        const val LOGOUT = "/logout"

        const val USER = "/user"
        const val USER_PICTURE = "/user/picture"
        const val USER_INTOLERANCES = "/user/intolerances"
        const val USER_DIETS = "/user/diets"
        const val USER_RESET_PASSWORD = "/user/password" // PATCH

        const val USERS = "/users" // GET
        const val USER_PROFILE = "/users/{username}" // GET

        const val USER_FOLLOW = "/user/follow/{username}" // PATCH and DELETE for unfollow
        const val USER_FOLLOW_REQUEST = "/user/follow-requests/{username}" // PATCH

        const val USER_FOLLOW_REQUESTS = "/user/follow-requests" // GET
        const val USER_FOLLOWERS = "/user/followers" // GET
        const val USER_FOLLOWING = "/user/following" // GET

        fun userProfile(username: String) = UriTemplate(USER_PROFILE).expand(username)
    }

    object Fridge {
        const val FRIDGE = "/fridge"
        const val PRODUCTS = "/products"
        const val PRODUCT = "/fridge/product/{entryNumber}"
        const val OPEN_PRODUCT = "/fridge/open/{entryNumber}"
    }

    object Recipe {
        const val RECIPES = "/recipes"
        const val RECIPE = "/recipes/{id}"

        fun recipe(id: Int) = UriTemplate(RECIPE).expand(id)
    }

    object MealPlanner {
        const val PLANNER = "/planner"
        const val MEAL_PLANNER = "/planner/{date}"
    }
}
