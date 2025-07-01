package android.epicurius.ui.screens.collections.favourites

import android.epicurius.domain.collection.CollectionProfile
import android.epicurius.ui.navigation.BottomBar
import android.epicurius.ui.navigation.TopBar
import android.epicurius.ui.screens.collections.components.CollectionProfileBox
import android.epicurius.ui.screens.collections.components.CreateCollectionDialog
import android.epicurius.ui.screens.utils.LoadState
import android.epicurius.ui.screens.utils.LoadStateRenderer
import android.epicurius.ui.screens.utils.Loaded
import android.epicurius.ui.screens.utils.apiSuccess
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
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
    favouritesState: LoadState<List<CollectionProfile>>,
    onBackButton: () -> Unit = {},
    onCollectionCreate: (collectionName: String) -> Unit = {},
    onCollectionDelete: (collectionId: Int) -> Unit = {},
    onCollectionRequest: (collectionId: Int, isCollectionOwner: Boolean) -> Unit = { _, _ -> },
    onLoadMoreFavourites: () -> Unit = {},
    enableButtons: Boolean
) {
    var showCreateCollectionDialog by remember { mutableStateOf(false) }

    Scaffold(
        topBar = {
            TopBar(
                titleText = "Favourites",
                backButton = true,
                onBackButton = onBackButton,
                enableButtons = enableButtons && favouritesState is Loaded,
                icon = null
            )
        },
        bottomBar = { BottomBar(buttonsEnable = enableButtons && favouritesState is Loaded) },
        content = { paddingValues ->
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
                    .padding(16.dp)
                    .background(Color.White)
                    .verticalScroll(rememberScrollState()),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Row {
                    Spacer(Modifier.fillMaxWidth().weight(0.9f))
                    IconButton(
                        onClick = { showCreateCollectionDialog = true },
                        enabled = enableButtons
                    ) {
                        Icon(
                            imageVector = Icons.Default.Add,
                            contentDescription = "Create Collection"
                        )
                    }
                }
                LoadStateRenderer(
                    loadState = favouritesState,
                    content = { favourites ->
                        if (favourites.isNotEmpty()) {
                            favourites.forEach {
                                CollectionProfileBox(
                                    isCollectionOwner = true,
                                    collection = it,
                                    onCollectionDelete = onCollectionDelete,
                                    onCollectionRequest = onCollectionRequest,
                                    enableButtons = enableButtons
                                )
                                Spacer(modifier = Modifier.height(10.dp))
                            }
                            Button(
                                onClick = { onLoadMoreFavourites() },
                                modifier = Modifier.fillMaxWidth().padding(10.dp),
                                enabled = enableButtons
                            ) { Text("Load More") }
                        }
                        else if (favouritesState is Loaded) {
                            Text(
                                text = "You have no collections yet.",
                                modifier = Modifier.padding(16.dp),
                                color = Color.Gray
                            )
                            Text(
                                text = "Create your first collection by clicking the '+' button above.",
                                color = Color(0xFF4E0D8D)
                            )
                        }
                    }
                )
                if (showCreateCollectionDialog) {
                    CreateCollectionDialog(
                        onCollectionCreate = onCollectionCreate,
                        onDismiss = { showCreateCollectionDialog = false },
                        enableButtons = enableButtons
                    )
                }
            }
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
    val emptyCollections = emptyList<CollectionProfile>()

    FavouritesScreen(apiSuccess(emptyCollections), enableButtons = true)

    FavouritesScreen(apiSuccess(collections), enableButtons = true)
}
