package epicurius.http.utils

import org.springframework.web.util.UriTemplate
import java.time.LocalDate

object Uris {

    const val PREFIX = "/api"

    object User {
        const val SIGNUP = "/signup"
        const val LOGIN = "/login"
        const val LOGOUT = "/logout"

        const val USER = "/user"
        const val USER_PICTURE = "$USER/picture"
        const val USER_INTOLERANCES = "$USER/intolerances"
        const val USER_DIETS = "$USER/diets"
        const val USER_RESET_PASSWORD = "$USER/password"
        const val USER_FEED = "$USER/feed"

        const val USERS = "/users"
        const val USER_PROFILE = "$USERS/{name}"

        const val USER_FOLLOW = "$USER/follow/{name}"
        const val USER_FOLLOW_REQUEST = "$USER/follow-requests/{name}"

        const val USER_FOLLOW_REQUESTS = "$USER/follow-requests"
        const val USER_FOLLOWERS = "$USER/followers"
        const val USER_FOLLOWING = "$USER/following"

        fun userProfile(name: String) = UriTemplate(USER_PROFILE).expand(name)
    }

    object Fridge {
        const val FRIDGE = "/fridge"
        const val PRODUCT = "$FRIDGE/product/{entryNumber}"

        fun product(entryNumber: Int) = UriTemplate(PRODUCT).expand(entryNumber)
    }

    object Recipe {
        const val RECIPES = "/recipes"
        const val RECIPE = "$RECIPES/{id}"
        const val RECIPE_RATE = "$RECIPE/rate"
        const val RECIPE_USER_RATE = "$RECIPE_RATE/self"
        const val RECIPE_PICTURES = "$RECIPES/{id}/pictures"

        fun recipe(id: Int) = UriTemplate(RECIPE).expand(id)
        fun rateRecipe(id: Int) = UriTemplate(RECIPE_RATE).expand(id)
    }

    object Menu {
        const val MENU = "/menu"
    }

    object Ingredients {
        const val INGREDIENTS = "/ingredients"
        const val INGREDIENTS_SUBSTITUTES = "$INGREDIENTS/substitutes"
    }

    object MealPlanner {
        const val PLANNER = "/planner"
        const val MEAL_PLANNER = "$PLANNER/{date}"
        const val CALORIES = "$MEAL_PLANNER/calories"
        const val CLEAN_MEAL_TIME = "$MEAL_PLANNER/{mealTime}"

        fun mealPlanner(date: LocalDate) = UriTemplate(MEAL_PLANNER).expand(date)
    }

    object Collection {
        const val COLLECTIONS = "/collections"
        const val COLLECTION = "$COLLECTIONS/{id}"
        const val COLLECTION_RECIPES = "$COLLECTION/recipes"
        const val COLLECTION_RECIPE = "$COLLECTION/recipes/{recipeId}"

        fun collection(id: Int) = UriTemplate(COLLECTION).expand(id)
    }
}
