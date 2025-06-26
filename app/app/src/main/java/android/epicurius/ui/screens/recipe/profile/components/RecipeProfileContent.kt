package android.epicurius.ui.screens.recipe.profile.components

import android.Manifest
import android.epicurius.R
import android.epicurius.domain.collection.CollectionProfile
import android.epicurius.domain.recipe.Recipe
import android.epicurius.ui.screens.collections.recipeCollections.components.RecipeCollectionsDialog
import android.epicurius.ui.screens.utils.LoadState
import android.epicurius.ui.screens.utils.MixedText
import android.os.Build
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
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
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.isGranted
import com.google.accompanist.permissions.rememberPermissionState

@OptIn(ExperimentalPermissionsApi::class)
@Composable
fun RecipeProfileContent(
    recipe: Recipe,
    rating: Double,
    images: List<Int>,
    isAuthor: Boolean,
    userRating: Int = 0,
    collectionId: Int?,
    collectionsState: LoadState<List<CollectionProfile>>?,
    onEditRating: (Int) -> Unit,
    onEditRecipe: () -> Unit,
    onEditRecipeImages: (List<ByteArray>) -> Unit,
    onMakeIt: () -> Unit,
    onDeleteRecipe: (Int) -> Unit,
    onAddRecipeToCollection: (Int, Int) -> Unit,
    onRemoveRecipeFromCollection: (Int, Int) -> Unit,
    onCollectionsRequest: (Int, Boolean) -> Unit,
    enableButtons: Boolean,
    paddingValues: PaddingValues
) {
    var showEditRecipeDialog by remember { mutableStateOf(false) }
    var showEditRatingDialog by remember { mutableStateOf(false) }
    var showCollectionsDialog by remember { mutableStateOf(false) }
    var enableStarIcon by remember { mutableStateOf(recipe.isInCollection) }
    var confirmRecipeDelete by remember { mutableStateOf(false) }

    val pagerState = rememberPagerState(pageCount = { images.size })
    var recipePicturesBytes by remember { mutableStateOf(recipe.picturesBytes.toList())}
    val context = LocalContext.current

    val imagePickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.PickVisualMedia()
    ) { uri ->
        if (uri != null) {
            val inputStream = context.contentResolver.openInputStream(uri)
            val bytes = inputStream?.readBytes()
            inputStream?.close()
            if (bytes != null) {
                val currentPage = pagerState.currentPage
                recipePicturesBytes = recipePicturesBytes.toMutableList().also {
                    it[currentPage] = bytes
                }
                onEditRecipeImages(recipePicturesBytes)
            }
        } else {
            Toast.makeText(context, "No image selected", Toast.LENGTH_SHORT).show()
        }
    }

    val galleryPermissionState = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
        rememberPermissionState(Manifest.permission.READ_MEDIA_IMAGES)
    } else {
        rememberPermissionState(Manifest.permission.READ_EXTERNAL_STORAGE)
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(paddingValues)
            .padding(16.dp)
            .background(Color.White),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(start = 10.dp, end = 10.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row {
                Text("$rating/5")
                Spacer(Modifier.size(5.dp))
                Image(
                    painter = painterResource(id = R.drawable.star),
                    contentDescription = "Favorites",
                    modifier = Modifier
                        .padding(top = (0.5).dp)
                        .size(15.dp),
                    contentScale = ContentScale.Fit
                )
            }
            FavouritesIcon(
                recipe = recipe,
                collectionId = collectionId,
                onShowCollectionDialog = { showCollectionsDialog = true },
                onRemoveRecipeFromCollection = onRemoveRecipeFromCollection,
                enableStarIcon = enableStarIcon,
                enableButtons = enableButtons
            )
        }

        RecipeProfileImages(
            images = recipePicturesBytes,
            pagerState = pagerState,
            onImageClick = {
                if (galleryPermissionState.status.isGranted) {
                    imagePickerLauncher.launch(
                        PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly)
                    )
                } else {
                    galleryPermissionState.launchPermissionRequest()
                }
            },
            enabled = enableButtons && isAuthor
        )
        HorizontalPagerIndicator(images.size, pagerState)

        Row(
            modifier = Modifier.fillMaxWidth().padding(horizontal = 10.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            if (isAuthor) {
                TextButton(
                    onClick = { confirmRecipeDelete = true }
                ) { Text("Delete recipe", color = Color.Red) }
            } else {
                Box(Modifier.clickable { showEditRatingDialog = true }) {
                    Row {
                        Text("Your Rating: ", fontWeight = FontWeight.Bold)
                        Text("$userRating")
                    }
                }
            }
            Box(contentAlignment = Alignment.CenterEnd) {
                MixedText("by ", recipe.authorUsername)
            }
        }

        RecipeDescription(recipe.description)
        RecipeInfoComponent(
            recipe = recipe,
            isAuthor = isAuthor,
            onEditButton = { showEditRecipeDialog = true }
        )

        Row {
            Button(
                onClick = { onMakeIt() },
                modifier = Modifier.padding(top = 5.dp, end = 10.dp),
            ) { Text("Make it!") }
        }

        if (showEditRecipeDialog) {
            EditRecipeDialog(
                recipe = recipe,
                onDismissRequest = { showEditRecipeDialog = false },
                onEditRecipe = {
                    onEditRecipe()
                    showEditRecipeDialog = false
                },
                enableButtons
            )
        }
        if (showEditRatingDialog) {
            EditRatingDialog(
                previousRating = userRating,
                onDismissRequest = { showEditRatingDialog = false },
                onEditRating = {
                    onEditRating(it)
                    showEditRatingDialog = false
                }
            )
        }
        if (confirmRecipeDelete) {
            ConfirmDeleteRecipeDialog(
                recipeId = recipe.id,
                onDismissRequest = { confirmRecipeDelete = false },
                onConfirmDelete = {
                    onDeleteRecipe(it)
                    confirmRecipeDelete = false
                }
            )
        }
        if (showCollectionsDialog) {
            RecipeCollectionsDialog(
                recipeId = recipe.id,
                onDismissRequest = { showCollectionsDialog = false },
                onRecipeCollectionsRequest = {},
                enableButtons = enableButtons
            )
        }
    }
}