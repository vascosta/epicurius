package android.epicurius.ui.screens.favourites.folder

import android.epicurius.domain.collection.CollectionProfile
import android.epicurius.domain.recipe.RecipeInfo
import android.epicurius.ui.screens.BottomBar
import android.epicurius.ui.screens.TopBar
import android.epicurius.ui.screens.favourites.folder.components.CollectionProfileBox
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
fun FavouritesScreen(
    onBackButton: () -> Unit,
    onFavouritesRefresh: () -> Unit = {},
    favouritesState: LoadState<List<CollectionProfile>>
) {
    Scaffold(
        topBar = { TopBar("Favourites", backButton = true, onBackButton) },
        bottomBar = { BottomBar() },
        content = { paddingValues ->
            LoadStateRenderer(
                loadState = favouritesState,
                swipeToRefresh = onFavouritesRefresh,
                content = { favourites ->
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(paddingValues)
                            .padding(10.dp)
                            .background(Color.White)
                            .verticalScroll(rememberScrollState()),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        favourites.forEach {
                            CollectionProfileBox(it)
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
fun FavouritesScreenPreview() {
    val collections = listOf(
        CollectionProfile(1, "Italian Delights"),
        CollectionProfile(2, "Quick Snacks"),
        CollectionProfile(3, "Healthy Meals")
    )

    FavouritesScreen({}, {}, apiSuccess(collections))
}
