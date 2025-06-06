package android.epicurius

import android.epicurius.ui.MainScreen
import android.epicurius.ui.screens.auth.login.LoginActivity
import android.epicurius.ui.screens.auth.signup.SignUpActivity
import android.epicurius.ui.screens.dailyMenu.DailyMenuActivity
import android.epicurius.ui.screens.collections.favourites.folder.FavouritesActivity
import android.epicurius.ui.screens.collections.favourites.list.FavouritesListActivity
import android.epicurius.ui.screens.feed.FeedActivity
import android.epicurius.ui.screens.fridge.FridgeActivity
import android.epicurius.ui.screens.mealPlanner.calendar.CalendarActivity
import android.epicurius.ui.screens.recipe.createRecipe.CreateRecipeActivity
import android.epicurius.ui.screens.recipe.ingredients.ConfirmIngredientsActivity
import android.epicurius.ui.screens.recipe.profile.RecipeProfileActivity
import android.epicurius.ui.screens.search.general.SearchActivity
import android.epicurius.ui.screens.user.follow.FollowActivity
import android.epicurius.ui.screens.user.profile.UserProfileActivity
import android.epicurius.ui.screens.user.settings.SettingsActivity
import android.epicurius.ui.screens.utils.navigateTo
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.material3.MaterialTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent{
            MaterialTheme {
                MainScreen(
                    onSignUp = { navigateTo<SignUpActivity>() },
                    onLogin = { navigateTo<LoginActivity>() },
                    onUserProfile = { navigateTo<UserProfileActivity>() },
                    onUserSettings = { navigateTo<SettingsActivity>() },
                    onUserFollows = { navigateTo<FollowActivity>() },
                    onCreateRecipe = { navigateTo<CreateRecipeActivity>() },
                    onConfirmIngredients = { navigateTo<ConfirmIngredientsActivity>() },
                    onRecipeProfile = { navigateTo<RecipeProfileActivity>() },
                    onSearch = { navigateTo<SearchActivity>() },
                    onFeed = { navigateTo<FeedActivity>() },
                    onDailyMenu = { navigateTo<DailyMenuActivity>() },
                    onFavouritesFolders = { navigateTo<FavouritesActivity>() },
                    onFavouritesRecipes = { navigateTo<FavouritesListActivity>() },
                    onCalendar = { navigateTo<CalendarActivity>() },
                    onFridge = { navigateTo<FridgeActivity>() },
                )
            }
        }
    }
}