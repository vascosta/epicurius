package android.epicurius.ui.screens.recipe.profile.components

import android.Manifest
import android.epicurius.R
import android.epicurius.domain.Diet
import android.epicurius.domain.Intolerance
import android.epicurius.domain.collection.CollectionProfile
import android.epicurius.domain.recipe.Cuisine
import android.epicurius.domain.recipe.Ingredient
import android.epicurius.domain.recipe.IngredientUnit
import android.epicurius.domain.recipe.Instructions
import android.epicurius.domain.recipe.MealType
import android.epicurius.domain.recipe.Recipe
import android.epicurius.ui.screens.collections.recipeCollections.components.RecipeCollectionsDialog
import android.epicurius.ui.screens.collections.recipeCollections.components.RecipeCollectionsStateBundle
import android.epicurius.ui.screens.theme.DarkPurple
import android.epicurius.ui.screens.theme.Lilac
import android.epicurius.ui.screens.utils.HorizontalPagerIndicator
import android.epicurius.ui.screens.utils.LoadState
import android.epicurius.ui.screens.utils.LoadStateRenderer
import android.epicurius.ui.screens.utils.Loaded
import android.epicurius.ui.screens.utils.MixedText
import android.epicurius.ui.screens.utils.apiSuccess
import android.epicurius.ui.screens.utils.generateTestImageByteArray
import android.os.Build
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
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.isGranted
import com.google.accompanist.permissions.rememberPermissionState
import epicurius.domain.collection.CollectionType
import java.time.LocalDate
import java.util.Base64

@OptIn(ExperimentalPermissionsApi::class)
@Composable
fun RecipeProfileContent(
    recipe: Recipe,
    usernameState: LoadState<String>,
    userRecipeRatingState: LoadState<Int?>,
    recipeCollectionsStateBundle: RecipeCollectionsStateBundle,
    ingredientsResultState: LoadState<List<String>>,
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
    onDeleteUserRecipeRating: (recipeId: Int) -> Unit = {},
    onDeleteRecipe: () -> Unit = {},
    onMakeRecipe: () -> Unit = {},
    onAddRecipeToCollections: (
        recipeId: Int,
        collectionsToAdd: List<CollectionProfile>
    ) -> Unit = { _, _ -> },
    onRemoveRecipeFromCollections: (
        recipeId: Int,
        collectionsToRemove: List<CollectionProfile>
    ) -> Unit = { _, _ -> },
    onSearchIngredients: (partialName: String) -> Unit = {},
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

    var recipePicturesListSize by remember { mutableIntStateOf(recipe.pictures.size) }
    var recipePicturesBytes by remember { mutableStateOf(recipe.picturesBytes) }

    var pagerState = rememberPagerState(pageCount = { recipePicturesListSize })

    val imagePickerLauncherForEdit =
        rememberImagePickerLauncher(context) { bytes ->
            val currentPage = pagerState.currentPage
            recipePicturesBytes = recipePicturesBytes.toMutableList().also {
                it[currentPage] = bytes
            }
            onEditRecipePictures(recipePicturesBytes)
        }

    val imagePickerLauncherForAdd =
        rememberImagePickerLauncher(context) { bytes ->
            val currentPage = pagerState.currentPage
            recipePicturesBytes = recipePicturesBytes.toMutableList().also {
                it.add(currentPage, bytes)
                if (recipePicturesListSize < 3) recipePicturesListSize += 1
            }
            onEditRecipePictures(recipePicturesBytes)
    }

    val galleryPermissionState = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
        rememberPermissionState(Manifest.permission.READ_MEDIA_IMAGES)
    } else {
        rememberPermissionState(Manifest.permission.READ_EXTERNAL_STORAGE)
    }

    LaunchedEffect(usernameState) {
        if (usernameState is Loaded) {
            isAuthor = usernameState.value.getValueOrThrow() == recipe.authorUsername
            recipePicturesListSize =
                if (isAuthor) {
                    if (recipe.pictures.size < 3) recipe.pictures.size + 1
                    else recipe.pictures.size
                } else recipe.pictures.size
        }
    }
    LoadStateRenderer(
        loadState = userRecipeRatingState,
        content = { userRating ->
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .verticalScroll(rememberScrollState())
                    .padding(paddingValues)
                    .padding(16.dp),
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
                }
                RecipeProfileImages(
                    images = recipePicturesBytes,
                    pagerState = pagerState,
                    isAuthor = isAuthor,
                    onImageClick = {
                        if (galleryPermissionState.status.isGranted) {
                            imagePickerLauncherForEdit.launch(
                                PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly)
                            )
                        } else galleryPermissionState.launchPermissionRequest()
                    },
                    onAddImage = {
                        if (galleryPermissionState.status.isGranted) {
                            imagePickerLauncherForAdd.launch(
                                PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly)
                            )
                        } else galleryPermissionState.launchPermissionRequest()
                    },
                    onRemoveImage = { index ->
                        if (recipePicturesBytes.size > 1) {
                            recipePicturesBytes = recipePicturesBytes.toMutableList().also {
                                it.removeAt(index)
                            }
                            if (recipePicturesBytes.size == 1) {
                                recipePicturesListSize -= 1
                            }
                            onEditRecipePictures(recipePicturesBytes)
                        }
                    },
                    enableButtons = enableButtons && isAuthor
                )
                HorizontalPagerIndicator(
                    size = recipePicturesListSize,
                    pagerState = pagerState,
                )
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
                        Box(
                            modifier =
                                Modifier
                                    .clickable(
                                        enabled = enableButtons && userRating != null,
                                        onClick = { showEditRatingDialog = true }
                                    )) {
                            Row {
                                Text(
                                    text = "Your Rating: ",
                                    color = DarkPurple,
                                    fontWeight = FontWeight.Bold
                                )
                                Text("${userRating ?: "not yet rated"}")
                            }
                            if (showEditRatingDialog && userRating != null) {
                                EditUserRatingDialog(
                                    previousRating = userRating,
                                    onEditUserRating = onEditUserRating,
                                    onDeleteUserRecipeRating = { onDeleteUserRecipeRating(recipe.id) },
                                    onDismissRequest = { showEditRatingDialog = false },
                                    enableButtons = enableButtons
                                )
                            }
                        }
                    }
                    Box(contentAlignment = Alignment.CenterEnd) {
                        TextButton(
                            onClick = { onUserProfileRequest(recipe.authorUsername) },
                            enabled = enableButtons
                        ) {
                            MixedText(
                                boldString = "by ",
                                normalString = recipe.authorUsername,
                                color = DarkPurple
                            )
                        }
                    }
                }
                RecipeDescription(recipe.description)
                RecipeInfoComponent(
                    isAuthor = isAuthor,
                    recipe = recipe,
                    onEditRecipe = { showEditRecipeDialog = true },
                    enableButtons = enableButtons
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
                        ingredientsResultState = ingredientsResultState,
                        onEditRecipe = onEditRecipe,
                        onDismissRequest = { showEditRecipeDialog = false },
                        onSearchIngredients = onSearchIngredients,
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
        }
    )
}

@Preview
@Composable
fun RecipeProfileContentPreview(){
    val testImages = listOf(
        Base64.getEncoder().encodeToString(generateTestImageByteArray(R.drawable.test_tomato)),
    )

    val recipe = Recipe(
        id = 1,
        name = "Panquecas Americanas",
        authorUsername = "MestreAndre",
        rating = 4.3,
        date = LocalDate.of(2025, 5, 19),
        description = "Deliciosas panquecas fofinhas perfeitas para o pequeno-almoço.",
        servings = 4,
        preparationTime = 20,
        cuisine = Cuisine.AMERICAN,
        mealType = MealType.BREAKFAST,
        intolerances = listOf(Intolerance.GLUTEN),
        diets = listOf(Diet.VEGETARIAN),
        ingredients = listOf(
            Ingredient("Farinha de trigo", 200.0, IngredientUnit.G),
            Ingredient("Leite", 300.0, IngredientUnit.ML),
            Ingredient("Ovo", 2.0, IngredientUnit.X),
            Ingredient("Açúcar", 50.0, IngredientUnit.G),
            Ingredient("Fermento em pó", 10.0, IngredientUnit.G),
            Ingredient("Sal", 1.0, IngredientUnit.TSP),
            Ingredient("Manteiga", 30.0, IngredientUnit.G)
        ),
        calories = 350,
        protein = 8,
        fat = 10,
        carbs = 55,
        instructions = Instructions(
            steps = mapOf(
                "1" to "Numa taça, mistura a farinha, o açúcar, o fermento e o sal.",
                "2" to "Adiciona o leite, os ovos e a manteiga derretida. Mistura até ficar homogéneo.",
                "3" to "Aquece uma frigideira antiaderente e coloca uma concha da massa.",
                "4" to "Cozinha até formar bolhas na superfície e vira a panqueca. Cozinha o outro lado.",
                "5" to "Serve quente com xarope de ácer ou frutas."
            )
        ),
        pictures = testImages
    )
    val rating = 4
    RecipeProfileContent(
        recipe = recipe,
        usernameState = apiSuccess("MestreAndre"),
        userRecipeRatingState = apiSuccess(rating),
        recipeCollectionsStateBundle = RecipeCollectionsStateBundle(
            collectionsToAddRecipeState = apiSuccess(emptyList()),
            collectionsToRemoveRecipeState = apiSuccess(emptyList())
        ),
        ingredientsResultState = apiSuccess(emptyList()),
        enableButtons = true,
        paddingValues = PaddingValues()
    )
}