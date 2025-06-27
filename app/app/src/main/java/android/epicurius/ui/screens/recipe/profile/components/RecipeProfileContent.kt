package android.epicurius.ui.screens.recipe.profile.components

import android.Manifest
import android.epicurius.R
import android.epicurius.domain.Diet
import android.epicurius.domain.Intolerance
import android.epicurius.domain.collection.CollectionProfile
import android.epicurius.domain.recipe.Cuisine
import android.epicurius.domain.recipe.Ingredient
import android.epicurius.domain.recipe.Instructions
import android.epicurius.domain.recipe.MealType
import android.epicurius.domain.recipe.Recipe
import android.epicurius.ui.screens.collections.recipeCollections.components.RecipeCollectionsDialog
import android.epicurius.ui.screens.collections.recipeCollections.components.RecipeCollectionsStateBundle
import android.epicurius.ui.screens.utils.HorizontalPagerIndicator
import android.epicurius.ui.screens.utils.LoadState
import android.epicurius.ui.screens.utils.LoadStateRenderer
import android.epicurius.ui.screens.utils.Loaded
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
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
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
import epicurius.domain.collection.CollectionType

@OptIn(ExperimentalPermissionsApi::class)
@Composable
fun RecipeProfileContent(
    recipeState: LoadState<Recipe>,
    usernameState: LoadState<String>,
    userRecipeRatingState: LoadState<Int?>,
    recipeCollectionsStateBundle: RecipeCollectionsStateBundle,
    onEditRecipe: (
        name: String?,
        description: String?,
        servings: Int?,
        preparationTime: Int?,
        cuisine: Cuisine?,
        mealType: MealType?,
        intolerances: Set<Intolerance>?,
        diets: Set<Diet>?,
        ingredients: List<Ingredient>?,
        calories: Int?,
        protein: Int?,
        fat: Int?,
        carbs: Int?,
        instructions: Instructions?
    ) -> Unit = { _, _, _, _, _, _, _, _, _, _, _, _, _, _ -> },
    onEditRecipePictures: (picturesBytes: List<ByteArray>) -> Unit = {},
    onEditUserRating: (rating: Int) -> Unit = {},
    onDeleteUserRecipeRating: (rating: Int) -> Unit = {},
    onDeleteRecipe: () -> Unit,
    onMakeRecipe: () -> Unit,
    onAddRecipeToCollections: (
        recipeId: Int,
        collectionsToAdd: List<CollectionProfile>
    ) -> Unit = { _, _ -> },
    onRemoveRecipeFromCollections: (
        recipeId: Int,
        collectionsToRemove: List<CollectionProfile>
    ) -> Unit = { _, _ -> },
    onRecipeCollectionsClear: () -> Unit = {},
    onUserProfileRequest: (name: String) -> Unit = {},
    onRecipeCollectionsRequest: (recipeId: Int, collectionType: CollectionType) -> Unit = { _, _ -> },
    enableButtons: Boolean,
    paddingValues: PaddingValues
) {
    val context = LocalContext.current

    var showEditRecipeDialog by remember { mutableStateOf(false) }
    var showEditRatingDialog by remember { mutableStateOf(false) }
    var showCollectionsDialog by remember { mutableStateOf(false) }
    var showConfirmDeleteRecipeDialog by remember { mutableStateOf(false) }

    var isAuthor by remember { mutableStateOf(false) }

    var recipePicturesSize by remember { mutableIntStateOf(0) }
    var recipePicturesBytes by remember { mutableStateOf(emptyList<ByteArray>()) }

    var pagerState = rememberPagerState(pageCount = { recipePicturesSize })

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
                onEditRecipePictures(recipePicturesBytes)
            }
        } else Toast.makeText(context, "No image selected", Toast.LENGTH_SHORT).show()
    }

    val galleryPermissionState = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
        rememberPermissionState(Manifest.permission.READ_MEDIA_IMAGES)
    } else {
        rememberPermissionState(Manifest.permission.READ_EXTERNAL_STORAGE)
    }

    LaunchedEffect(recipeState) {
        if (recipeState is Loaded) {
            val recipe = recipeState.value.getValueOrThrow()
            recipePicturesSize = recipe.picturesBytes.size
            recipePicturesBytes = recipe.picturesBytes
        }
    }
    LaunchedEffect(usernameState) {
        if (usernameState is Loaded && recipeState is Loaded)
            isAuthor = usernameState.value.getValueOrThrow() == recipeState.value.getValueOrThrow().authorUsername
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
            LoadStateRenderer(
                loadState = recipeState,
                content = { recipe ->
                    Row {
                        Text("${recipe.rating}/5")
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
                        onShowCollectionsDialog = { showCollectionsDialog = true },
                        enableButtons = enableButtons
                    )
                    RecipeProfileImages(
                        images = recipePicturesBytes,
                        pagerState = pagerState,
                        onImageClick = {
                            if (galleryPermissionState.status.isGranted) {
                                imagePickerLauncher.launch(
                                    PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly)
                                )
                            } else galleryPermissionState.launchPermissionRequest()
                        },
                        enabled = enableButtons && isAuthor
                    )
                    HorizontalPagerIndicator(recipe.picturesBytes.size, pagerState)
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 10.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        if (isAuthor) {
                            TextButton(
                                onClick = { showConfirmDeleteRecipeDialog = true }
                            ) { Text("Delete recipe", color = Color.Red) }
                        } else {
                            LoadStateRenderer(
                                loadState = userRecipeRatingState,
                                content = { userRating ->
                                    Box(
                                        modifier =
                                            Modifier
                                                .clickable(
                                                    enabled = enableButtons && userRating != null,
                                                    onClick = { showEditRatingDialog = true }
                                    )) {
                                        Row {
                                            Text("Your Rating: ", fontWeight = FontWeight.Bold)
                                            Text("$userRating")
                                        }
                                        if (showEditRatingDialog && userRating != null) {
                                            EditUserRatingDialog(
                                                previousRating = userRating,
                                                onEditUserRating = onEditUserRating,
                                                onDeleteUserRecipeRating = onDeleteUserRecipeRating,
                                                onDismissRequest = { showEditRatingDialog = false },
                                                enableButtons = enableButtons
                                            )
                                        }
                                    }
                                }
                            )
                        }
                        Box(contentAlignment = Alignment.CenterEnd) {
                            Button(
                                onClick = { onUserProfileRequest(recipe.authorUsername) },
                                enabled = enableButtons
                            ) { MixedText("by ", recipe.authorUsername) }
                        }
                    }
                    RecipeDescription(recipe.description)
                    RecipeInfoComponent(
                        isAuthor = isAuthor,
                        recipe = recipe,
                        onEditRecipe = { showEditRecipeDialog = true }
                    )
                    Row {
                        Button(
                            onClick = { onMakeRecipe() },
                            modifier = Modifier.padding(top = 5.dp, end = 10.dp),
                            enabled = enableButtons
                        ) { Text("Make it!") }
                    }
                    if (showEditRecipeDialog) {
                        EditRecipeDialog(
                            recipe = recipe,
                            onEditRecipe = onEditRecipe,
                            onDismissRequest = { showEditRecipeDialog = false },
                            enableButtons = enableButtons
                        )
                    }
                    if (showConfirmDeleteRecipeDialog) {
                        ConfirmDeleteRecipeDialog(
                            onConfirmDeleteRecipe = onDeleteRecipe,
                            onDismissRequest = { showConfirmDeleteRecipeDialog = false },
                            enableButtons = enableButtons
                        )
                    }
                    if (showCollectionsDialog) {
                        RecipeCollectionsDialog(
                            recipeId = recipe.id,
                            recipeCollectionsStateBundle = recipeCollectionsStateBundle,
                            onAddRecipeToCollections = onAddRecipeToCollections,
                            onRemoveRecipeFromCollections = onRemoveRecipeFromCollections,
                            onRecipeCollectionsRequest = { recipeId ->
                                val collectionType = if (isAuthor) CollectionType.KITCHEN_BOOK
                                else CollectionType.FAVOURITE
                                onRecipeCollectionsRequest(recipeId, collectionType)
                            },
                            onDismissRequest = {
                                showCollectionsDialog = false
                                onRecipeCollectionsClear()
                            },
                            enableButtons = enableButtons
                        )
                    }
                }
            )
        }
    }
}