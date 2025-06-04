package android.epicurius.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp

@Composable
fun MainScreen(
    onSignUp: () -> Unit = {},
    onLogin: () -> Unit = {},
    onUserProfile: () -> Unit = {},
    onUserSettings: () -> Unit = {},
    onUserFollows: () -> Unit = {},
    onCreateRecipe: () -> Unit = {},
    onConfirmIngredients: () -> Unit = {},
    onRecipeProfile: () -> Unit = {},
    onSearch: () -> Unit = {},
    onFeed: () -> Unit = {},
    onDailyMenu: () -> Unit = {},
    onFavouritesFolders: () -> Unit = {},
    onFavouritesRecipes: () -> Unit = {},
    onCalendar: () -> Unit = {},
    onFridge: () -> Unit = {}
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
            .background(Color.White)
            .verticalScroll(rememberScrollState()),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Button(onClick = { onSignUp() }) {
            Text("Sign Up")
        }
        Button(onClick = { onLogin() }) {
            Text("Login")
        }
        Button(onClick = { onUserProfile() }) {
            Text("User Profile")
        }
        Button(onClick = { onUserSettings() }) {
            Text("User Settings")
        }
        Button(onClick = { onUserFollows() }) {
            Text("User Follows")
        }
        Button(onClick = { onCreateRecipe() }) {
            Text("Create recipe")
        }
        Button(onClick = { onConfirmIngredients() }) {
            Text("Confirm Ingredients")
        }
        Button(onClick = { onRecipeProfile() }) {
            Text("Recipe Profile")
        }
        Button(onClick = { onSearch() }) {
            Text("Search")
        }
        Button(onClick = { onFeed() }) {
            Text("Feed")
        }
        Button(onClick = { onDailyMenu() }) {
            Text("Daily Menu")
        }
        Button(onClick = { onFavouritesFolders() }) {
            Text("Favourites Folders")
        }
        Button(onClick = { onFavouritesRecipes() }) {
            Text("Favourites Recipes")
        }
        Button(onClick = { onCalendar() }) {
            Text("Calendar")
        }
        Button(onClick = { onFridge() }) {
            Text("Fridge")
        }
    }
}

@Preview
@Composable
fun MainScreenPreview() {
    MainScreen()
}
