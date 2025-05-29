package android.epicurius.services.http.api

import android.epicurius.services.http.HttpService
import android.epicurius.services.http.api.auth.AuthService
import android.epicurius.services.http.api.collection.CollectionService
import android.epicurius.services.http.api.dailyMenu.DailyMenuService
import android.epicurius.services.http.api.fridge.FridgeService
import android.epicurius.services.http.api.ingredients.IngredientsService
import android.epicurius.services.http.api.mealPlanner.MealPlannerService
import android.epicurius.services.http.api.recipe.RecipeService
import android.epicurius.services.http.api.user.UserService

class EpicuriusService(val http: HttpService) {
    val authService = AuthService(http)
    val collectionService = CollectionService(http)
    val dailyMenuService = DailyMenuService(http)
    val fridgeService = FridgeService(http)
    val ingredientsService = IngredientsService(http)
    val mealPlannerService = MealPlannerService(http)
    val recipeService = RecipeService(http)
    val userService = UserService(http)
}