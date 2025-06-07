package android.epicurius.ui.screens.collections.favourites.list

import android.epicurius.domain.recipe.Cuisine
import android.epicurius.domain.recipe.MealType
import android.epicurius.domain.recipe.RecipeInfo
import android.epicurius.ui.navigation.BottomBar
import android.epicurius.ui.navigation.TopBar
import android.epicurius.ui.screens.collections.favourites.folder.components.DeleteCollectionDialog
import android.epicurius.ui.screens.collections.favourites.folder.components.EditCollectionDialog
import android.epicurius.ui.screens.collections.favourites.folder.components.getFavouritesListName
import android.epicurius.ui.screens.recipe.components.RecipeInfoBox
import android.epicurius.ui.screens.utils.LoadState
import android.epicurius.ui.screens.utils.LoadStateRenderer
import android.epicurius.ui.screens.utils.apiSuccess
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp

@Composable
fun FavouritesListScreen(
    onBackButton: () -> Unit = {},
    onEditCollection: (String) -> Unit = {},
    onDeleteCollection: (Int) -> Unit = {},
    onRecipeRequest: (Int) -> Unit = {},
    onRecipeDelete: () -> Unit = {},
    onFavouritesRefresh: () -> Unit = {},
    favouritesListNameState: LoadState<String>,
    recipesState: LoadState<List<RecipeInfo>>,
) {
    val favouritesListName = getFavouritesListName(favouritesListNameState)

    var showEditCollectionDialog by remember { mutableStateOf(false) }
    var showDeleteCollectionDialog by remember { mutableStateOf(false) }

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
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            TextButton(
                                onClick = { showEditCollectionDialog = true }
                            ) { Text("Edit Collection") }
                            TextButton(
                                onClick = { showDeleteCollectionDialog = true }
                            ) { Text("Delete Collection", color = Color.Red) }
                        }
                        
                        recipes.forEach {
                            Row {
                                RecipeInfoBox(
                                    recipeInfo = it,
                                    onRecipeRequest,
                                    isFavourite = true,
                                    onFavouriteStarClick = onRecipeDelete
                                )
                                Spacer(modifier = Modifier.height(10.dp))
                            }
                        }
                    }

                    if (showEditCollectionDialog) {
                        EditCollectionDialog(
                            onDismiss = { showEditCollectionDialog = false },
                            onEditCollection = onEditCollection,
                            collectionName = favouritesListName
                        )
                    }

                    if (showDeleteCollectionDialog) {
                        DeleteCollectionDialog(
                            collectionName = favouritesListName,
                            collectionId = 1,
                            onDelete = {
                                onDeleteCollection(1)
                                showDeleteCollectionDialog = false
                            },
                            onDismissRequest = { showDeleteCollectionDialog = false }
                        )
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
                rating = 4.3,
                cuisine = Cuisine.ITALIAN,
                mealType = MealType.MAIN_COURSE,
                preparationTime = 35,
                servings = 4,
                picture = "".toByteArray()
            )
        ))
    )
}
