package android.epicurius.ui.screens.collections.favourites.folder

import android.epicurius.domain.collection.CollectionProfile
import android.epicurius.ui.navigation.BottomBar
import android.epicurius.ui.navigation.TopBar
import android.epicurius.ui.screens.collections.favourites.folder.components.CollectionProfileBox
import android.epicurius.ui.screens.collections.favourites.folder.components.CreateCollectionDialog
import android.epicurius.ui.screens.utils.LoadState
import android.epicurius.ui.screens.utils.LoadStateRenderer
import android.epicurius.ui.screens.utils.Loaded
import android.epicurius.ui.screens.utils.apiSuccess
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
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
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp

@Composable
fun FavouritesScreen(
    favouritesState: LoadState<List<CollectionProfile>>,
    onBackButton: () -> Unit,
    onCollectionCreate: (collectionName: String) -> Unit,
    onCollectionRequest: (collectionId: Int) -> Unit,
    onCollectionDelete: (collectionId: Int) -> Unit,
    onFavouritesRefresh: () -> Unit,
    buttonsEnable: Boolean
) {
    var showCreateCollectionDialog by rememberSaveable { mutableStateOf(false) }

    Scaffold(
        topBar = {
            TopBar(
                titleText = "Favourites",
                backButton = true,
                onBackButton = onBackButton,
                enableButtons = buttonsEnable,
                icon = null
            )
        },
        bottomBar = { BottomBar(buttonsEnable = buttonsEnable) },
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
                        if (favouritesState is Loaded) {
                            Box(Modifier.fillMaxWidth()) {
                                Row {
                                    Spacer(Modifier.weight(0.9f))
                                    IconButton(
                                        onClick = { showCreateCollectionDialog = true }
                                    ) {
                                        Icon(
                                            imageVector = Icons.Default.Add,
                                            contentDescription = "Create Collection"
                                        )
                                    }
                                }
                            }
                        }
                        if (favourites.isEmpty()) {
                            Text(
                                text = "You have no collections yet.",
                                modifier = Modifier.padding(16.dp),
                                color = Color.Gray
                            )
                            Text(
                                text = "Create your first collection by clicking the '+' button above.",
                                color = Color(0xFF4E0D8D)
                            )
                        } else {
                            favourites.forEach {
                                CollectionProfileBox(
                                    collection = it,
                                    onCollectionRequest = onCollectionRequest,
                                    onCollectionDelete = onCollectionDelete,
                                    buttonsEnable = buttonsEnable
                                )
                                Spacer(modifier = Modifier.height(10.dp))
                            }
                        }
                    }
                    if (showCreateCollectionDialog) {
                        CreateCollectionDialog(
                            onDismiss = { showCreateCollectionDialog = false },
                            onCollectionCreate = onCollectionCreate,
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
fun FavouritesScreenPreview() {
    val collections = listOf(
        CollectionProfile(1, "Italian Delights"),
        CollectionProfile(2, "Quick Snacks"),
        CollectionProfile(3, "Healthy Meals")
    )
    val emptyCollections = emptyList<CollectionProfile>()

    FavouritesScreen(apiSuccess(emptyCollections), {}, {}, {}, {}, {}, true)

    FavouritesScreen(apiSuccess(collections), {}, {}, {}, {}, {}, true)
}
