package android.epicurius.ui.screens.recipe.profile

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
import android.epicurius.ui.navigation.BottomBar
import android.epicurius.ui.navigation.TopBar
import android.epicurius.ui.screens.collections.list.components.CollectionsListDialog
import android.epicurius.ui.screens.recipe.profile.components.ConfirmDeleteRecipeDialog
import android.epicurius.ui.screens.recipe.profile.components.EditRatingDialog
import android.epicurius.ui.screens.recipe.profile.components.EditRecipeDialog
import android.epicurius.ui.screens.recipe.profile.components.HorizontalPagerIndicator
import android.epicurius.ui.screens.recipe.profile.components.RecipeProfileImages
import android.epicurius.ui.screens.recipe.profile.utils.generateTestImageByteArray
import android.epicurius.ui.screens.utils.LoadState
import android.epicurius.ui.screens.utils.LoadingSpinner
import android.epicurius.ui.screens.utils.MixedText
import android.epicurius.ui.screens.utils.apiSuccess
import android.os.Build
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.IconButton
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
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.isGranted
import com.google.accompanist.permissions.rememberPermissionState
import java.time.LocalDate
import java.util.Base64

@OptIn(ExperimentalPermissionsApi::class)
@Composable
fun RecipeProfileScreen(
    recipe: Recipe,
    rating: Double,
    images: List<Int>,
    isAuthor: Boolean,
    userRating: Int = 0,
    collectionId: Int?,
    collectionsState: LoadState<List<CollectionProfile>>?,
    onBackButton: () -> Unit,
    onEditRating: (Int) -> Unit,
    onEditRecipe: () -> Unit,
    onEditRecipeImages: (List<ByteArray>) -> Unit,
    onMakeIt: () -> Unit,
    onDeleteRecipe: (Int) -> Unit,
    onAddRecipeToCollection: (Int, Int) -> Unit,
    onRemoveRecipeFromCollection: (Int, Int) -> Unit,
    onCollectionsRequest: (Int, Boolean) -> Unit,
    enableButtons: Boolean,
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
        rememberPermissionState(android.Manifest.permission.READ_MEDIA_IMAGES)
    } else {
        rememberPermissionState(android.Manifest.permission.READ_EXTERNAL_STORAGE)
    }

    Scaffold(
        topBar = {
            TopBar(
                titleText = recipe.name,
                backButton = true,
                onBackButton = onBackButton,
                enableButtons = true
            )
        },
        bottomBar = { BottomBar(buttonsEnable = true) },
        content = { paddingValues ->
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
                    Row{
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

                    IconButton(
                        onClick = {
                            if (collectionId != null) {
                                onRemoveRecipeFromCollection(collectionId, recipe.id)
                            }
                            else {
                                showCollectionsDialog = true
                            }
                        },
                        modifier = Modifier.size(24.dp),
                        enabled = enableButtons
                    ) {
                        if (enableButtons) {
                            val painter = if (enableStarIcon) {
                                painterResource(R.drawable.star)
                            } else {
                                painterResource(R.drawable.white_star)
                            }
                            Image(
                                painter = painter,
                                contentDescription = "Favorites",
                                modifier = Modifier.size(20.dp),
                                contentScale = ContentScale.Fit
                            )
                        }
                        else {
                            LoadingSpinner(Modifier.size(30.dp))
                        }

                        if (showCollectionsDialog) {
                            CollectionsListDialog(
                                recipeId = recipe.id,
                                collectionsStateBundle = null,
                                onDismissRequest = { showCollectionsDialog = false },
                                onAddRecipeToCollections = {_, _, _, _ ->},
                                onRemoveRecipeFromCollections = {_, _, _, _ ->},
                                onCollectionsRequest = {},
                                enableButtons = enableButtons
                            )
                        }
                    }
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

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    horizontalArrangement = Arrangement.Center,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = recipe.description,
                        fontStyle = FontStyle.Italic,
                        textAlign = TextAlign.Center
                    )
                }

                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 16.dp, start = 10.dp, end = 10.dp)
                        .border(1.dp, Color.Black, RoundedCornerShape(16.dp)),
                    contentAlignment = Alignment.CenterStart
                ) {

                    if (isAuthor) {
                        Button(
                            onClick = { showEditRecipeDialog = true },
                            modifier = Modifier
                                .align(Alignment.TopEnd)
                                .padding(top = 5.dp, end = 10.dp)
                        ) {
                            Text("Edit")
                        }
                    }

                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp)
                    ) {
                        MixedText("Servings: ", "${recipe.servings} px")
                        MixedText("Preparation Time: ", "${recipe.preparationTime} min")
                        MixedText("Meal Type: ", recipe.mealType.displayName)
                        MixedText("Cuisine: ", recipe.cuisine.displayName)
                        MixedText("Intolerances: ", recipe.intolerances.joinToString(", ") { it.displayName })
                        MixedText("Diets: ", recipe.diets.joinToString(", ") { it.displayName })
                        MixedText("Calories: ", recipe.calories?.toString() ?: "N/A")
                        MixedText("Protein: ", recipe.protein?.toString() ?: "N/A")
                        MixedText("Fat: ", recipe.fat?.toString() ?: "N/A")
                        MixedText("Carbs: ", recipe.carbs?.toString() ?: "N/A")

                        val ingredients = recipe.ingredients.joinToString("\n") {
                            val formattedQuantity = if (it.quantity % 1.0 == 0.0) {
                                it.quantity.toInt()
                            } else {
                                it.quantity
                            }
                            val formattedUnit = it.unit.displayName
                            "$formattedQuantity$formattedUnit ${it.name}"
                        }
                        Text("Ingredients:", fontWeight = FontWeight.Bold)
                        Text(text = ingredients, modifier = Modifier.padding(start = 10.dp))

                        val instructions = recipe.instructions.steps.entries.joinToString("\n") { "${it.key}: ${it.value}" }
                        Text("Instructions:", fontWeight = FontWeight.Bold)
                        Text(text = instructions, modifier = Modifier.padding(start = 10.dp))
                    }
                }

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
            }
        },
        containerColor = Color.White
    )
}

@Preview
@Composable
fun RecipeProfilePreview(){
    val testImages = listOf(
        Base64.getEncoder().encodeToString(generateTestImageByteArray(R.drawable.test_tomato)),
        Base64.getEncoder().encodeToString(generateTestImageByteArray(R.drawable.test_tomato)),
        Base64.getEncoder().encodeToString(generateTestImageByteArray(R.drawable.test_tomato))
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
        pictures = testImages,
        isInCollection = true
    )
    val rating = 4.0
    val collections = listOf(
        CollectionProfile(id = 1, name = "Favorites"),
        CollectionProfile(id = 2, name = "Breakfast Recipes")
    )
    RecipeProfileScreen(
        recipe = recipe,
        rating = rating,
        images = listOf(R.drawable.home, R.drawable.star, R.drawable.pencil),
        isAuthor = true,
        userRating = 4,
        collectionId = null,
        collectionsState = apiSuccess(collections),
        {},
        {},
        {},
        {},
        {},
        {},
        { _, _ -> },
        { _, _ -> },
        { _, _ -> },
        enableButtons = true,
    )
}