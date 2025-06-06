package android.epicurius.ui.screens.collections.favourites.folder

import android.epicurius.domain.collection.CollectionProfile
import android.epicurius.ui.navigation.BottomBar
import android.epicurius.ui.navigation.TopBar
import android.epicurius.ui.screens.collections.favourites.folder.components.CollectionProfileBox
import android.epicurius.ui.screens.collections.favourites.folder.components.CreateCollectionDialog
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
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
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
fun FavouritesScreen(
    onBackButton: () -> Unit,
    onCollectionCreate: () -> Unit,
    onCollectionRequest: (Int) -> Unit,
    onCollectionDelete: (Int) -> Unit,
    onFavouritesRefresh: () -> Unit,
    favouritesState: LoadState<List<CollectionProfile>>
) {
    var showCreateCollectionDialog by remember { mutableStateOf(false) }

    Scaffold(
        topBar = { TopBar("Favourites", backButton = true, onBackButton) },
        bottomBar = { BottomBar() },
        floatingActionButton = {
            FloatingActionButton(
                onClick = { showCreateCollectionDialog = true },
            ) {
                Icon(
                    imageVector = Icons.Default.Add,
                    contentDescription = "Create Collection"
                )
            }
        },
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
                            CollectionProfileBox(
                                collection = it,
                                onCollectionRequest = onCollectionRequest,
                                onCollectionDelete = onCollectionDelete
                            )
                            Spacer(modifier = Modifier.height(10.dp))
                        }
                    }

                    if (showCreateCollectionDialog) {
                        CreateCollectionDialog(
                            onDismiss = { showCreateCollectionDialog = false },
                            onCreate = {
                                onCollectionCreate()
                                showCreateCollectionDialog = false
                            }
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
fun FavouritesScreenPreview() {
    val collections = listOf(
        CollectionProfile(1, "Italian Delights"),
        CollectionProfile(2, "Quick Snacks"),
        CollectionProfile(3, "Healthy Meals")
    )

    FavouritesScreen({}, {}, {}, {}, {}, apiSuccess(collections))
}
