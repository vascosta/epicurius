package epicurius.http.utils

import epicurius.http.utils.Uris.Recipe.RECIPE
import org.springframework.web.util.UriTemplate

object Uris {

    const val PREFIX = "/api"

    object User {
        const val SIGNUP = "/signup"
        const val LOGIN = "/login"
        const val LOGOUT = "/logout"

        const val USER = "/user"
        const val USER_PICTURE = "/$USER/picture"
        const val USER_INTOLERANCES = "/$USER/intolerances"
        const val USER_DIETS = "/$USER/diets"
        const val USER_RESET_PASSWORD = "/$USER/password" // PATCH

        const val USERS = "/users" // GET
        const val USER_PROFILE = "/users/{name}" // GET

        const val USER_FOLLOW = "/$USER/follow/{name}" // PATCH and DELETE for unfollow
        const val USER_FOLLOW_REQUEST = "/$USER/follow-requests/{name}" // PATCH

        const val USER_FOLLOW_REQUESTS = "/$USER/follow-requests" // GET
        const val USER_FOLLOWERS = "/$USER/followers" // GET
        const val USER_FOLLOWING = "/$USER/following" // GET

        fun userProfile(name: String) = UriTemplate(USER_PROFILE).expand(name)
    }

    object Fridge {
        const val FRIDGE = "/fridge"
        const val PRODUCT = "/$FRIDGE/product/{entryNumber}"
    }

    object Recipe {
        const val RECIPES = "/recipes"
        const val RECIPE = "/$RECIPES/{id}"
        const val RECIPE_PICTURES = "/$RECIPES/{id}/pictures"

        fun recipe(id: Int) = UriTemplate(RECIPE).expand(id)
    }

    object RateRecipe {
        const val RATE = "/$RECIPE/rate"

        fun rateRecipe(id: Int) = UriTemplate(RATE).expand(id)
    }

    object Feed {
        const val FEED = "/feed"
    }

    object Menu {
        const val MENU = "/menu"
    }

    object Ingredients {
        const val INGREDIENTS = "/ingredients"
        const val INGREDIENTS_SUBSTITUTES = "/$INGREDIENTS/substitutes"
    }

    object MealPlanner {
        const val PLANNER = "/planner"
        const val MEAL_PLANNER = "/$PLANNER/{date}"
    }

    object Collection {
        const val COLLECTIONS = "/collections"
        const val COLLECTION = "/$COLLECTIONS/{id}"
        const val COLLECTION_RECIPES = "$COLLECTION/recipes"
        const val COLLECTION_RECIPE = "$COLLECTION/recipes/{recipeId}"

        fun collection(id: Int) = UriTemplate(COLLECTION).expand(id)
    }
}
