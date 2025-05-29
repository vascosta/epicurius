package android.epicurius.services

import android.epicurius.services.api.auth.AuthService
import android.epicurius.services.api.collection.CollectionService
import android.epicurius.services.api.dailyMenu.DailyMenuService
import android.epicurius.services.api.fridge.FridgeService
import android.epicurius.services.api.ingredients.IngredientsService
import android.epicurius.services.api.mealPlanner.MealPlannerService
import android.epicurius.services.api.recipe.RecipeService
import android.epicurius.services.api.user.UserService
import android.epicurius.services.http.HttpService

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