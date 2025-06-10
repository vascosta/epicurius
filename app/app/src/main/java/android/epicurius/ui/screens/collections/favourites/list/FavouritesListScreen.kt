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
import android.epicurius.ui.screens.utils.Loaded
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
import androidx.compose.runtime.LaunchedEffect
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
    collectionId: Int,
    favouritesListNameState: LoadState<String>,
    recipesState: LoadState<List<RecipeInfo>>,
    onBackButton: () -> Unit,
    onCollectionEdit: (collectionId: Int, collectionName: String) -> Unit,
    onCollectionDelete: (collectionId: Int) -> Unit,
    onRecipeDelete: (collectionId: Int, recipeId: Int) -> Unit,
    onRecipeRequest: (recipeId: Int) -> Unit,
    onFavouriteCollectionRefresh: () -> Unit,
    buttonsEnable: Boolean
) {
    var favouritesListName = getFavouritesListName(favouritesListNameState)

    var showEditCollectionDialog by remember { mutableStateOf(false) }
    var showDeleteCollectionDialog by remember { mutableStateOf(false) }

    LaunchedEffect(favouritesListNameState) {
        if (favouritesListNameState is Loaded) {
            showEditCollectionDialog = false
        }
    }
    Scaffold(
        topBar = {
            TopBar(
                titleText = favouritesListName,
                backButton = true,
                onBackButton = onBackButton,
                enableButtons = buttonsEnable,
                icon = null
            )
                 },
        bottomBar = { BottomBar(buttonsEnable = buttonsEnable) },
        content = { paddingValues ->
            LoadStateRenderer(
                loadState = recipesState,
                swipeToRefresh = onFavouriteCollectionRefresh,
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
                                onClick = { showEditCollectionDialog = true },
                                enabled = buttonsEnable
                            ) { Text("Edit Collection") }
                            TextButton(
                                onClick = { showDeleteCollectionDialog = true },
                                enabled = buttonsEnable
                            ) { Text("Delete Collection", color = Color.Red) }
                        }

                        if (recipes.isEmpty()) {
                            Text(
                                text = "You have no recipes in this collection.",
                                modifier = Modifier.padding(16.dp),
                                color = Color.Gray
                            )
                        } else {
                            recipes.forEach {
                                Row {
                                    RecipeInfoBox(
                                        collectionId = collectionId,
                                        recipeInfo = it,
                                        collectionsStateBundle = null,
                                        onAddRecipeToCollections = {_, _, _, _ ->},
                                        onRemoveRecipeFromCollections = {_, _, _, _ ->},
                                        onRemoveRecipeFromCollection = onRecipeDelete,
                                        onRecipeRequest = onRecipeRequest,
                                        onCollectionsRequest = {},
                                        onCollectionsClear = {},
                                        enableButtons = buttonsEnable
                                    )
                                    Spacer(modifier = Modifier.height(10.dp))
                                }
                            }
                        }
                    }
                    if (showEditCollectionDialog && buttonsEnable) {
                        EditCollectionDialog(
                            collectionName = favouritesListName,
                            collectionId = collectionId,
                            onDismiss = { showEditCollectionDialog = false },
                            onEditCollection = onCollectionEdit,
                            buttonsEnable = buttonsEnable
                        )
                    }
                    if (showDeleteCollectionDialog && buttonsEnable) {
                        DeleteCollectionDialog(
                            collectionName = favouritesListName,
                            collectionId = collectionId,
                            onCollectionDelete = onCollectionDelete,
                            onDismissRequest = { showDeleteCollectionDialog = false },
                            buttonsEnable = buttonsEnable
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
        collectionId = 1,
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
                picture = "",
                isInCollection = true
            )
        )),
        favouritesListNameState = apiSuccess("My Favourite Recipes"),
        onBackButton = {},
        onCollectionEdit = {_, _ ->},
        onCollectionDelete = {},
        onRecipeDelete = {_, _ ->},
        onRecipeRequest = {},
        onFavouriteCollectionRefresh = {},
        buttonsEnable = true
    )
}
