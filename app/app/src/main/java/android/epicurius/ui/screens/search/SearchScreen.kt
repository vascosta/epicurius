package android.epicurius.ui.screens.search

import android.Manifest
import android.epicurius.domain.Diet
import android.epicurius.domain.Intolerance
import android.epicurius.domain.recipe.Cuisine
import android.epicurius.domain.recipe.Ingredient
import android.epicurius.domain.recipe.MealType
import android.epicurius.domain.recipe.RecipeInfo
import android.epicurius.domain.user.SearchUser
import android.epicurius.ui.navigation.BottomBar
import android.epicurius.ui.navigation.TopBar
import android.epicurius.ui.screens.recipe.components.RecipeInfoBox
import android.epicurius.ui.screens.search.components.ConfirmIngredientsDialog
import android.epicurius.ui.screens.search.components.FiltersDialog
import android.epicurius.ui.screens.search.components.FiltersIcon
import android.epicurius.ui.screens.search.components.SearchPhotoComponent
import android.epicurius.ui.screens.user.components.UserBox
import android.epicurius.ui.screens.utils.Idle
import android.epicurius.ui.screens.utils.LoadState
import android.epicurius.ui.screens.utils.LoadStateRenderer
import android.epicurius.ui.screens.utils.Loaded
import android.epicurius.ui.screens.utils.SearchTextField
import android.epicurius.ui.screens.utils.TabComponent
import android.epicurius.ui.screens.utils.apiSuccess
import android.epicurius.ui.screens.utils.isValidForNumberTextField
import android.os.Build
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.isGranted
import com.google.accompanist.permissions.rememberPermissionState
import com.google.accompanist.permissions.shouldShowRationale

@OptIn(ExperimentalPermissionsApi::class)
@Composable
fun SearchScreen(
    recipesResultState: LoadState<List<RecipeInfo>>,
    usersResultState: LoadState<List<SearchUser>>,
    onBackButton: () -> Unit,
    onSearchRecipes: (
        name: String?,
        cuisine: List<Cuisine>?,
        mealType: List<MealType>?,
        ingredients: List<Ingredient>?,
        intolerances: List<Intolerance>?,
        diets: List<Diet>?,
        servings: Int?,
        minCalories: Int?,
        maxCalories: Int?,
        minCarbs: Int?,
        maxCarbs: Int?,
        minFat: Int?,
        maxFat: Int?,
        minProtein: Int?,
        maxProtein: Int?,
        minTime: Int?,
        maxTime: Int?
    ) -> Unit,
    onSearchUsers: (name: String) -> Unit,
    onSearchRecipesClear: () -> Unit,
    onSearchUsersClear: () -> Unit,
    onCamera: () -> Unit,
    onIdentifyIngredientsInPicture: (ByteArray) -> Unit,
    onConfirm: (List<String>) -> Unit,
    onUserProfileRequest: (name: String) -> Unit,
    onRecipeProfileRequest: (recipeId: Int) -> Unit,
    enableButtons: Boolean
) {
    val context = LocalContext.current

    val tabs = listOf("Recipe", "Users")
    var selectedTabIndex by remember { mutableIntStateOf(0) }

    var showFiltersDialog by remember { mutableStateOf(false) }

    var searchQuery by remember { mutableStateOf("") }
    var searchRecipesQuery by remember { mutableStateOf("") }
    var searchUsersQuery by remember { mutableStateOf("") }

    var cuisine by remember { mutableStateOf(listOf<String>()) }
    var mealType by remember { mutableStateOf(listOf<String>()) }
    var intolerances by remember { mutableStateOf(listOf<String>()) }
    var diets by remember { mutableStateOf(listOf<String>()) }
    var serving by remember { mutableStateOf("") }
    var minCalories by remember { mutableStateOf("") }
    var maxCalories by remember { mutableStateOf("") }
    var minCarbs by remember { mutableStateOf("") }
    var maxCarbs by remember { mutableStateOf("") }
    var minFat by remember { mutableStateOf("") }
    var maxFat by remember { mutableStateOf("") }
    var minProtein by remember { mutableStateOf("") }
    var maxProtein by remember { mutableStateOf("") }
    var minTime by remember { mutableStateOf("") }
    var maxTime by remember { mutableStateOf("") }

    var selectedImageBytes by remember { mutableStateOf<ByteArray?>(null) }

    val imagePickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.PickVisualMedia()
    ) { uri ->
        if (uri != null) {
            val inputStream = context.contentResolver.openInputStream(uri)
            val bytes = inputStream?.readBytes()
            inputStream?.close()
            if (bytes != null) {
                selectedImageBytes = bytes
                onIdentifyIngredientsInPicture(bytes)
            }
        } else {
            Toast.makeText(context, "No image selected", Toast.LENGTH_SHORT).show()
        }
    }

    var showGalleryAccessDialog by remember { mutableStateOf(false) }
    val galleryPermissionState =
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU)
            rememberPermissionState(Manifest.permission.READ_MEDIA_IMAGES)
        else
            rememberPermissionState(Manifest.permission.READ_EXTERNAL_STORAGE)

    var showConfirmIngredientsDialog by remember { mutableStateOf(false) }

    Scaffold(
        topBar = {
            TopBar(
                titleText = "Search",
                backButton = true,
                onBackButton = onBackButton,
                enableButtons = enableButtons
            )
        },
        bottomBar = { BottomBar(buttonsEnable = enableButtons) },
        content = { paddingValues ->
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
                    .padding(16.dp)
                    .verticalScroll(rememberScrollState()),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                SearchTextField(
                    text = searchQuery,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(8.dp),
                    onSearchQueryChange = { searchQuery = it },
                    onIconClick = {
                        if (selectedTabIndex == 0) {
                            val cuisineList = cuisine.map { Cuisine.valueOf(it) }
                            val mealTypeList = mealType.map { MealType.valueOf(it) }
                            // ingredients
                            val intolerancesList = intolerances.map { Intolerance.fromDisplayName(it) }
                            val dietsList = diets.map { Diet.fromDisplayName(it) }

                            onSearchRecipesClear()
                            onSearchRecipes(
                                searchQuery,
                                if (cuisineList.isEmpty()) null else cuisineList,
                                if (mealTypeList.isEmpty()) null else mealTypeList,
                                null, // change to ingredients
                                if (intolerancesList.isEmpty()) null else intolerancesList,
                                if (dietsList.isEmpty()) null else dietsList,
                                serving.toIntOrNull(),
                                minCalories.toIntOrNull(),
                                maxCalories.toIntOrNull(),
                                minCarbs.toIntOrNull(),
                                maxCarbs.toIntOrNull(),
                                minFat.toIntOrNull(),
                                maxFat.toIntOrNull(),
                                minProtein.toIntOrNull(),
                                maxProtein.toIntOrNull(),
                                minTime.toIntOrNull(),
                                maxTime.toIntOrNull()
                            )
                            searchRecipesQuery = searchQuery
                        }
                        else {
                            onSearchUsersClear()
                            onSearchUsers(searchQuery)
                            searchUsersQuery = searchQuery
                        }
                    },
                    enableButtons = enableButtons
                )

                TabComponent(
                    tabs = tabs,
                    selectedTabIndex = selectedTabIndex,
                    onTabSelected = { selectedTabIndex = it }
                )

                if (selectedTabIndex == 0) {
                    Button(
                        onClick = {
                            onSearchRecipesClear()
                            // clear filters and ingredients
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(10.dp),
                        enabled = enableButtons
                    ) { Text("Clear") }
                    if (recipesResultState is Idle) {
                        Row(modifier = Modifier.fillMaxWidth()) {
                            FiltersIcon(onClick = { showFiltersDialog = true })
                            Button(
                                onClick = {
                                    onSearchRecipesClear()
                                    // clear filters and ingredients
                                },
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(10.dp),
                                enabled = enableButtons
                            ) { Text("Clear") }
                        }
                        Spacer(modifier = Modifier.height(100.dp))
                        SearchPhotoComponent(
                            onCamera,
                            onUpload = {
                                when {
                                    galleryPermissionState.status.isGranted -> {
                                        imagePickerLauncher.launch(
                                            PickVisualMediaRequest(
                                                ActivityResultContracts.PickVisualMedia.ImageOnly
                                            )
                                        )
                                        showConfirmIngredientsDialog = true
                                    }
                                    galleryPermissionState.status.shouldShowRationale -> {
                                        showGalleryAccessDialog = true
                                    }
                                    else -> showGalleryAccessDialog = true
                                }
                            },
                            modifier = Modifier.fillMaxWidth()
                        )
                    }
                    LoadStateRenderer(
                        loadState = recipesResultState,
                        content = { recipesResult ->
                            Button(
                                onClick = {
                                    onSearchRecipesClear()
                                    // clear filters and ingredients
                                },
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(10.dp),
                                enabled = enableButtons
                            ) { Text("Clear") }
                            if (recipesResult.isNotEmpty()) {
                                recipesResult.forEach { recipe ->
                                    RecipeInfoBox(
                                        collectionId = null,
                                        recipeInfo = recipe,
                                        collectionsStateBundle = null,
                                        onAddRecipeToCollections = { _, _, _, _ -> },
                                        onRemoveRecipeFromCollections = { _, _, _, _ -> },
                                        onRemoveRecipeFromCollection = { _, _ -> },
                                        onCollectionsClear = {},
                                        onRecipeRequest = onRecipeProfileRequest,
                                        onCollectionsRequest = {},
                                        enableButtons = enableButtons
                                    )
                                    Spacer(Modifier.height(10.dp))
                                }
                                Button(
                                    onClick = {
                                        val cuisineList = cuisine.map { Cuisine.valueOf(it) }
                                        val mealTypeList = mealType.map { MealType.valueOf(it) }
                                        // ingredients
                                        val intolerancesList = intolerances.map { Intolerance.fromDisplayName(it) }
                                        val dietsList = diets.map { Diet.fromDisplayName(it) }

                                        onSearchRecipes(
                                            searchRecipesQuery,
                                            if (cuisineList.isEmpty()) null else cuisineList,
                                            if (mealTypeList.isEmpty()) null else mealTypeList,
                                            null, // change to ingredients
                                            if (intolerancesList.isEmpty()) null else intolerancesList,
                                            if (dietsList.isEmpty()) null else dietsList,
                                            serving.toIntOrNull(),
                                            minCalories.toIntOrNull(),
                                            maxCalories.toIntOrNull(),
                                            minCarbs.toIntOrNull(),
                                            maxCarbs.toIntOrNull(),
                                            minFat.toIntOrNull(),
                                            maxFat.toIntOrNull(),
                                            minProtein.toIntOrNull(),
                                            maxProtein.toIntOrNull(),
                                            minTime.toIntOrNull(),
                                            maxTime.toIntOrNull()
                                        )
                                    },
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(10.dp),
                                    enabled = enableButtons
                                ) { Text("Load More") }
                            }
                            else if (recipesResultState is Loaded) {
                                Text(
                                    text = "No recipes found.",
                                    modifier = Modifier.padding(10.dp),
                                    textAlign = TextAlign.Center,
                                    color = Color.Gray
                                )
                            }
                        }
                    )
                } else {
                    LoadStateRenderer(
                        loadState = usersResultState,
                        content = { usersResult ->
                            if (usersResult.isNotEmpty()) {
                                usersResult.forEach { user ->
                                    UserBox(
                                        user,
                                        onUserProfileRequest = onUserProfileRequest,
                                        enableButtons = enableButtons
                                    )
                                }
                                Button(
                                    onClick = { onSearchUsers(searchUsersQuery) },
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(10.dp),
                                    enabled = enableButtons
                                ) { Text("Load More") }
                            }
                            else if (usersResultState is Loaded) {
                                Text(
                                    text = "No users found.",
                                    modifier = Modifier.padding(10.dp),
                                    textAlign = TextAlign.Center,
                                    color = Color.Gray
                                )
                            }
                        }
                    )
                }
                if (showFiltersDialog) {
                    FiltersDialog(
                        onDismiss = { showFiltersDialog = false },
                        onCancel = {
                            showFiltersDialog = false
                            mealType = emptyList()
                            // clear filters
                        },
                        cuisine = cuisine,
                        onCuisineChange = { cuisine = it },
                        mealType = mealType,
                        onMealTypeChange = { mealType = it },
                        intolerances = intolerances,
                        onIntolerancesChange = { intolerances = it },
                        diets = diets,
                        onDietsChange = { diets = it },
                        servings = serving,
                        onServingsChange = { serving = isValidForNumberTextField(it) },
                        minCalories = minCalories,
                        onMinCaloriesChange = { minCalories = isValidForNumberTextField(it) },
                        maxCalories = maxCalories,
                        onMaxCaloriesChange = { maxCalories = isValidForNumberTextField(it) },
                        minCarbs = minCarbs,
                        onMinCarbsChange = { minCarbs = isValidForNumberTextField(it) },
                        maxCarbs = maxCarbs,
                        onMaxCarbsChange = { maxCarbs = isValidForNumberTextField(it) },
                        minFat = minFat,
                        onMinFatChange = { minFat = isValidForNumberTextField(it) },
                        maxFat = maxFat,
                        onMaxFatChange = { maxFat = isValidForNumberTextField(it) },
                        minProtein = minProtein,
                        onMinProteinChange = { minProtein = isValidForNumberTextField(it) },
                        maxProtein = maxProtein,
                        onMaxProteinChange = { maxProtein = isValidForNumberTextField(it) },
                        minTime = minTime,
                        onMinTimeChange = { minTime = isValidForNumberTextField(it) },
                        maxTime = maxTime,
                        onMaxTimeChange = { maxTime = isValidForNumberTextField(it) },
                        enableButtons
                    )
                }
                if (showGalleryAccessDialog) {
                    LaunchedEffect(Unit) {
                        galleryPermissionState.launchPermissionRequest()
                    }
                }
                if (showConfirmIngredientsDialog && selectedImageBytes != null) {
                    ConfirmIngredientsDialog(
                        ingredients = listOf(),
                        onConfirm = onConfirm,
                        onDismiss = { showConfirmIngredientsDialog = false }
                    )
                }
            }
        },
        containerColor = Color.White
    )
}

private fun clearFilters(
    cuisine: List<Cuisine>?,
    mealType: List<MealType>?,
    ingredients: List<Ingredient>?,
    intolerances: List<Intolerance>?,
    diets: List<Diet>?,
    servings: Int?,
    minCalories: Int?,
    maxCalories: Int?,
    minCarbs: Int?,
    maxCarbs: Int?,
    minFat: Int?,
    maxFat: Int?,
    minProtein: Int?,
    maxProtein: Int?,
    minTime: Int?,
    maxTime: Int?
) {

}

@Preview
@Composable
fun SearchScreenPreview() {
    val recipeList = listOf(
        RecipeInfo(
            id = 1,
            name = "Spaghetti Carbonara",
            authorUsername = "ChefBear",
            rating = 4.5,
            cuisine = Cuisine.ITALIAN,
            mealType = MealType.MAIN_COURSE,
            preparationTime = 30,
            servings = 4,
            picture = "",
            isInCollection = true
        ),
        RecipeInfo(
            id = 2,
            name = "Caesar Salad",
            authorUsername = "ChefBear",
            rating = 4.3,
            cuisine = Cuisine.ITALIAN,
            mealType = MealType.SALAD,
            preparationTime = 15,
            servings = 2,
            picture = "",
            isInCollection = false
        )
    )

    SearchScreen(
        recipesResultState = apiSuccess(recipeList),
        usersResultState = apiSuccess(emptyList()),
        onBackButton = {},
        onSearchRecipes = { _, _, _, _, _, _, _, _, _, _, _, _, _, _, _, _, _ -> },
        onSearchUsers = {},
        onSearchRecipesClear = {},
        onSearchUsersClear = {},
        onCamera = {},
        onIdentifyIngredientsInPicture = {},
        onConfirm = { _ -> },
        onUserProfileRequest = {},
        onRecipeProfileRequest = {},
        enableButtons = true
    )
}
