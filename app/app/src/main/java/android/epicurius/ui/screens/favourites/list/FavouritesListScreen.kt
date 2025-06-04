package android.epicurius.ui.screens.favourites.list

import android.epicurius.domain.recipe.Cuisine
import android.epicurius.domain.recipe.MealType
import android.epicurius.domain.recipe.RecipeInfo
import android.epicurius.ui.screens.BottomBar
import android.epicurius.ui.screens.TopBar
import android.epicurius.ui.screens.favourites.folder.components.getFavouritesListName
import android.epicurius.ui.screens.recipe.components.RecipeInfoBox
import android.epicurius.ui.screens.utils.LoadState
import android.epicurius.ui.screens.utils.LoadStateRenderer
import android.epicurius.ui.screens.utils.apiSuccess
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp

@Composable
fun FavouritesListScreen(
    onBackButton: () -> Unit = {},
    onFavouritesRefresh: () -> Unit = {},
    favouritesListNameState: LoadState<String>,
    recipesState: LoadState<List<RecipeInfo>>
) {
    val favouritesListName = getFavouritesListName(favouritesListNameState)
    Scaffold(
        topBar = { TopBar(text = favouritesListName, backButton = true, onBackButton) },
        bottomBar = { BottomBar() },
        content = { paddingValues ->
            LoadStateRenderer(
                loadState = recipesState,
                swipeToRefresh = onFavouritesRefresh,
                content = { recipes ->
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(paddingValues)
                            .padding(10.dp)
                            .background(Color.White)
                            .verticalScroll(rememberScrollState()),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        recipes.forEach {
                            RecipeInfoBox(recipeInfo = it)
                            Spacer(modifier = Modifier.height(10.dp))
                        }
                    }
                }
            )
        },
        containerColor = Color.White
    )
}

@Preview
@Composable
fun FavouritesListScreenPreview() {
    FavouritesListScreen(
        onBackButton = {},
        onFavouritesRefresh = {},
        favouritesListNameState = apiSuccess("My Favourite Recipes"),
        recipesState = apiSuccess(listOf(
            RecipeInfo(
                id = 1,
                name = "Spaghetti Carbonara",
                authorUsername = "ChefBear",
                cuisine = Cuisine.ITALIAN,
                mealType = MealType.MAIN_COURSE,
                preparationTime = 35,
                servings = 4,
                picture = "".toByteArray()
            )
        ))
    )
}
