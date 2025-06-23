package android.epicurius.ui.screens.collections.list

import android.epicurius.domain.recipe.Cuisine
import android.epicurius.domain.recipe.MealType
import android.epicurius.domain.recipe.RecipeInfo
import android.epicurius.ui.navigation.BottomBar
import android.epicurius.ui.navigation.TopBar
import android.epicurius.ui.screens.collections.components.DeleteCollectionDialog
import android.epicurius.ui.screens.collections.components.EditCollectionDialog
import android.epicurius.ui.screens.collections.favourites.components.getCollectionsListName
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
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp

@Composable
fun CollectionsListScreen(
    isAuthor: Boolean,
    collectionId: Int,
    collectionListNameState: LoadState<String>,
    recipesState: LoadState<List<RecipeInfo>>,
    onBackButton: () -> Unit,
    onCollectionEdit: (collectionId: Int, collectionName: String) -> Unit,
    onCollectionDelete: (collectionId: Int) -> Unit,
    onRecipeDelete: (collectionId: Int, recipeId: Int) -> Unit,
    onRecipeRequest: (recipeId: Int) -> Unit,
    onFavouriteCollectionRefresh: () -> Unit,
    enableButtons: Boolean
) {
    var collectionListName = getCollectionsListName(collectionListNameState)

    var showEditCollectionDialog by rememberSaveable { mutableStateOf(false) }
    var showDeleteCollectionDialog by rememberSaveable { mutableStateOf(false) }

    LaunchedEffect(collectionListNameState) {
        if (collectionListNameState is Loaded) {
            showEditCollectionDialog = false
        }
    }
    Scaffold(
        topBar = {
            TopBar(
                titleText = collectionListName,
                backButton = true,
                onBackButton = onBackButton,
                enableButtons = enableButtons && collectionListNameState is Loaded && recipesState is Loaded,
                icon = null
            )
        },
        bottomBar = { BottomBar(
            buttonsEnable = enableButtons && collectionListNameState is Loaded && recipesState is Loaded
        ) },
        content = { paddingValues ->
            LoadStateRenderer(
                loadState = recipesState,
                content = { recipes ->
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(paddingValues)
                            .padding(16.dp)
                            .background(Color.White)
                            .verticalScroll(rememberScrollState()),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        if (isAuthor) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                TextButton(
                                    onClick = { showEditCollectionDialog = true },
                                    enabled = enableButtons
                                ) { Text("Edit Collection") }
                                TextButton(
                                    onClick = { showDeleteCollectionDialog = true },
                                    enabled = enableButtons
                                ) { Text("Delete Collection", color = Color.Red) }
                            }
                        }

                        if (recipes.isEmpty()) {
                            val txt =
                                if (isAuthor) { "You have no recipes in this collection." }
                                else { "This collection has no recipes." }
                            Text(
                                text = txt,
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
                                        enableButtons = enableButtons
                                    )
                                    Spacer(modifier = Modifier.height(10.dp))
                                }
                            }
                        }
                    }
                    if (showEditCollectionDialog) {
                        EditCollectionDialog(
                            collectionName = collectionListName,
                            collectionId = collectionId,
                            onDismiss = {
                                if (enableButtons) {
                                    showEditCollectionDialog = false
                                }
                            },
                            onEditCollection = onCollectionEdit,
                            enableButtons = enableButtons
                        )
                    }
                    if (showDeleteCollectionDialog) {
                        DeleteCollectionDialog(
                            collectionName = collectionListName,
                            collectionId = collectionId,
                            onCollectionDelete = onCollectionDelete,
                            onDismissRequest = {
                                if (enableButtons) {
                                    showDeleteCollectionDialog = false
                                }
                            },
                            enableButtons = enableButtons
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
    CollectionsListScreen(
        isAuthor = false,
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
        collectionListNameState = apiSuccess("My Favourite Recipes"),
        onBackButton = {},
        onCollectionEdit = {_, _ ->},
        onCollectionDelete = {},
        onRecipeDelete = {_, _ ->},
        onRecipeRequest = {},
        onFavouriteCollectionRefresh = {},
        enableButtons = true
    )
}
