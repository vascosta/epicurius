package android.epicurius.ui.screens.search.components

import android.Manifest
import android.epicurius.domain.Diet
import android.epicurius.domain.Intolerance
import android.epicurius.domain.collection.CollectionProfile
import android.epicurius.domain.recipe.Cuisine
import android.epicurius.domain.recipe.MealType
import android.epicurius.domain.recipe.RecipeInfo
import android.epicurius.domain.user.SearchUser
import android.epicurius.domain.user.UserInfo
import android.epicurius.ui.navigation.BottomBar
import android.epicurius.ui.navigation.TopBar
import android.epicurius.ui.screens.collections.recipeCollections.components.RecipeCollectionsStateBundle
import android.epicurius.ui.screens.recipe.components.RecipeInfoBox
import android.epicurius.ui.screens.theme.Beige
import android.epicurius.ui.screens.user.components.UserBox
import android.epicurius.ui.screens.utils.Idle
import android.epicurius.ui.screens.utils.LoadState
import android.epicurius.ui.screens.utils.LoadStateRenderer
import android.epicurius.ui.screens.utils.Loaded
import android.epicurius.ui.screens.utils.SearchTextField
import android.epicurius.ui.screens.utils.TabComponent
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.isGranted
import com.google.accompanist.permissions.rememberPermissionState
import kotlin.text.uppercase

@OptIn(ExperimentalPermissionsApi::class)
@Composable
fun SearchScreenContent(
    ingredientsList: Set<String>,
    recipesResultState: LoadState<List<RecipeInfo>>,
    usersResultState: LoadState<List<SearchUser>>,
    ingredientsState: LoadState<List<String>>,
    userInfoState: LoadState<UserInfo>,
    recipeCollectionsStateBundle: RecipeCollectionsStateBundle,
    onSearchRecipes: (
        name: String?,
        cuisine: List<Cuisine>?,
        mealType: List<MealType>?,
        ingredients: Set<String>?,
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
        maxTime: Int?,
        showAuthorRecipes: Boolean
    ) -> Unit = { _, _, _, _, _, _, _, _, _, _, _, _, _, _, _, _, _, _ -> },
    onSearchUsers: (name: String) -> Unit = {},
    onCamera: () -> Unit = {},
    onIdentifyIngredientsInPicture: (pictureBytes: ByteArray) -> Unit = {},
    onConfirmIngredients: (List<String>) -> Unit = {},
    onAddRecipeToCollections: (
        recipeId: Int,
        collectionsToAdd: List<CollectionProfile>
    ) -> Unit = { _, _ -> },
    onRemoveRecipeFromCollections: (
        recipeId: Int,
        collectionsToRemove: List<CollectionProfile>
    ) -> Unit = { _, _ -> },
    onSearchRecipesClear: () -> Unit = {},
    onSearchUsersClear: () -> Unit = {},
    onIngredientsClear: () -> Unit = {},
    onRecipeCollectionsClear: () -> Unit = {},
    onRecipeProfileRequest: (recipeId: Int) -> Unit = {},
    onUserProfileRequest: (name: String) -> Unit = {},
    onRecipeCollectionsRequest: (recipeId: Int, isRecipeAuthor: Boolean) -> Unit = { _, _ -> },
    enableButtons: Boolean
) {
    val context = LocalContext.current

    val tabs = listOf("Recipe", "Users")
    var selectedTabIndex by remember { mutableIntStateOf(0) }

    var showConfirmIngredientsDialog by remember { mutableStateOf(false) }
    var showFiltersDialog by remember { mutableStateOf(false) }

    var searchQuery by remember { mutableStateOf("") }
    var searchRecipesQuery by remember { mutableStateOf("") }
    var searchUsersQuery by remember { mutableStateOf("") }

    var cuisine by remember { mutableStateOf(listOf<String>()) }
    var mealType by remember { mutableStateOf(listOf<String>()) }
    var ingredients by remember { mutableStateOf(setOf<String>()) }
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
    var showAuthorRecipes by remember { mutableStateOf(false) }

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
                showConfirmIngredientsDialog = true
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

    LaunchedEffect(ingredientsList) {
        ingredients += ingredientsList
    }
    LaunchedEffect(userInfoState) {
        if (userInfoState is Loaded) {
            val userInfo = userInfoState.value.getValueOrThrow()
            intolerances = userInfo.intolerances.map { it.displayName }
            diets = userInfo.diets.map { it.displayName }
        }
    }
    Scaffold(
        topBar = { TopBar(titleText = "Search", enableButtons = enableButtons) },
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
                            val cuisineList = cuisine.map { Cuisine.valueOf(it.uppercase().replace(Regex("[\\s-]"), "_")) }
                            val mealTypeList = mealType.map { MealType.valueOf(it.uppercase().replace(Regex("[\\s-]"), "_")) }
                            val intolerancesList =
                                intolerances.map {
                                    Intolerance.valueOf(
                                        it.uppercase().replace(Regex("[\\s-]"), "_")
                                    )
                                }
                            val dietsList = diets.map { Diet.valueOf(
                                it.uppercase().replace(Regex("[\\s-]"), "_")
                            ) }

                            onSearchRecipesClear()
                            onSearchRecipes(
                                if (searchQuery.isEmpty()) null else searchQuery,
                                if (cuisineList.isEmpty()) null else cuisineList,
                                if (mealTypeList.isEmpty()) null else mealTypeList,
                                if (ingredients.isEmpty()) null else ingredients.map { it.replace(" ", "-") }.toSet(),
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
                                maxTime.toIntOrNull(),
                                showAuthorRecipes
                            )
                            searchRecipesQuery = searchQuery
                        } else {
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
                    onTabSelected = { selectedTabIndex = it },
                    enabled = enableButtons
                )

                if (selectedTabIndex == 0) {
                    if (recipesResultState is Idle) {
                        Row(modifier = Modifier.fillMaxWidth()) {
                            FiltersIcon(onClick = { showFiltersDialog = true }, enableButtons = enableButtons)
                            TextButton(
                                onClick = {
                                    onSearchRecipesClear()
                                    onIngredientsClear()
                                    clearFilters(
                                        onCuisineChange = { cuisine = it },
                                        onMealTypeChange = { mealType = it },
                                        onIngredientsChange = { ingredients = it },
                                        onIntolerancesChange = { intolerances = it },
                                        onDietsChange = { diets = it },
                                        onServingsChange = { serving = it },
                                        onMinCaloriesChange = { minCalories = it },
                                        onMaxCaloriesChange = { maxCalories = it },
                                        onMinCarbsChange = { minCarbs = it },
                                        onMaxCarbsChange = { maxCarbs = it },
                                        onMinFatChange = { minFat = it },
                                        onMaxFatChange = { maxFat = it },
                                        onMinProteinChange = { minProtein = it },
                                        onMaxProteinChange = { maxProtein = it },
                                        onMinTimeChange = { minTime = it },
                                        onMaxTimeChange = { maxTime = it },
                                        onShowAuthorRecipesChange = { showAuthorRecipes = it }
                                    )
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
                                    }

                                    else -> showGalleryAccessDialog = true
                                }
                            },
                            modifier = Modifier.fillMaxWidth(),
                            enableButtons = enableButtons
                        )
                    }
                    LoadStateRenderer(
                        loadState = recipesResultState,
                        content = { recipesResult ->
                            TextButton(
                                onClick = {
                                    onSearchRecipesClear()
                                    onIngredientsClear()
                                    clearFilters(
                                        onCuisineChange = { cuisine = it },
                                        onMealTypeChange = { mealType = it },
                                        onIngredientsChange = { ingredients = it },
                                        onIntolerancesChange = { intolerances = it },
                                        onDietsChange = { diets = it },
                                        onServingsChange = { serving = it },
                                        onMinCaloriesChange = { minCalories = it },
                                        onMaxCaloriesChange = { maxCalories = it },
                                        onMinCarbsChange = { minCarbs = it },
                                        onMaxCarbsChange = { maxCarbs = it },
                                        onMinFatChange = { minFat = it },
                                        onMaxFatChange = { maxFat = it },
                                        onMinProteinChange = { minProtein = it },
                                        onMaxProteinChange = { maxProtein = it },
                                        onMinTimeChange = { minTime = it },
                                        onMaxTimeChange = { maxTime = it },
                                        onShowAuthorRecipesChange = { showAuthorRecipes = it }
                                    )
                                },
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(10.dp),
                                enabled = enableButtons
                            ) { Text("Clear") }
                            if (recipesResult.isNotEmpty()) {
                                recipesResult.forEach { recipe ->
                                    RecipeInfoBox(
                                        recipeInfo = recipe,
                                        recipeCollectionsStateBundle = recipeCollectionsStateBundle,
                                        onAddRecipeToCollections = onAddRecipeToCollections,
                                        onRemoveRecipeFromCollections = onRemoveRecipeFromCollections,
                                        onRecipeCollectionsClear = onRecipeCollectionsClear,
                                        onRecipeCollectionsRequest = { recipeId ->
                                            onRecipeCollectionsRequest(recipeId, showAuthorRecipes)
                                        },
                                        onRecipeRequest = onRecipeProfileRequest,
                                        enableButtons = enableButtons
                                    )
                                    Spacer(Modifier.height(10.dp))
                                }
                                Button(
                                    onClick = {
                                        val cuisineList = cuisine.map { Cuisine.valueOf(it.uppercase().replace(Regex("[\\s-]"), "_")) }
                                        val mealTypeList = mealType.map { MealType.valueOf(it.uppercase().replace(Regex("[\\s-]"), "_")) }
                                        val intolerancesList =
                                            intolerances.map {
                                                Intolerance.valueOf(
                                                    it.uppercase().replace(Regex("[\\s-]"), "_")
                                                )
                                            }
                                        val dietsList = diets.map { Diet.valueOf(
                                            it.uppercase().replace(Regex("[\\s-]"), "_")
                                        ) }

                                        onSearchRecipes(
                                            if (searchRecipesQuery.isEmpty()) null else searchRecipesQuery,
                                            if (cuisineList.isEmpty()) null else cuisineList,
                                            if (mealTypeList.isEmpty()) null else mealTypeList,
                                            if (ingredients.isEmpty()) null else ingredients.map { it.replace(" ", "-") }.toSet(),
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
                                            maxTime.toIntOrNull(),
                                            showAuthorRecipes
                                        )
                                    },
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(10.dp),
                                    enabled = enableButtons
                                ) { Text("Load More") }
                            } else if (recipesResultState is Loaded) {
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
                            } else if (usersResultState is Loaded) {
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
                            onIngredientsClear()
                            clearFilters(
                                onCuisineChange = { cuisine = it },
                                onMealTypeChange = { mealType = it },
                                onIngredientsChange = { ingredients = ingredientsList },
                                onIntolerancesChange = { intolerances = it },
                                onDietsChange = { diets = it },
                                onServingsChange = { serving = it },
                                onMinCaloriesChange = { minCalories = it },
                                onMaxCaloriesChange = { maxCalories = it },
                                onMinCarbsChange = { minCarbs = it },
                                onMaxCarbsChange = { maxCarbs = it },
                                onMinFatChange = { minFat = it },
                                onMaxFatChange = { maxFat = it },
                                onMinProteinChange = { minProtein = it },
                                onMaxProteinChange = { maxProtein = it },
                                onMinTimeChange = { minTime = it },
                                onMaxTimeChange = { maxTime = it },
                                onShowAuthorRecipesChange = { showAuthorRecipes = it }
                            )
                        },
                        cuisine = cuisine,
                        onCuisineChange = { cuisine = it },
                        mealType = mealType,
                        onMealTypeChange = { mealType = it },
                        ingredients = ingredients,
                        onIngredientsChange = { ingredients = it },
                        intolerances = intolerances,
                        onIntolerancesChange = { intolerances = it },
                        diets = diets,
                        onDietsChange = { diets = it },
                        servings = serving,
                        onServingsChange = { if (isValidForNumberTextField(it)) serving = it },
                        minCalories = minCalories,
                        onMinCaloriesChange = { if (isValidForNumberTextField(it)) minCalories = it },
                        maxCalories = maxCalories,
                        onMaxCaloriesChange = { if (isValidForNumberTextField(it)) maxCalories = it },
                        minCarbs = minCarbs,
                        onMinCarbsChange = { if (isValidForNumberTextField(it)) minCarbs = it },
                        maxCarbs = maxCarbs,
                        onMaxCarbsChange = { if (isValidForNumberTextField(it)) maxCarbs = it },
                        minFat = minFat,
                        onMinFatChange = { if (isValidForNumberTextField(it)) minFat = it },
                        maxFat = maxFat,
                        onMaxFatChange = { if (isValidForNumberTextField(it)) maxFat = it },
                        minProtein = minProtein,
                        onMinProteinChange = { if (isValidForNumberTextField(it)) minProtein = it },
                        maxProtein = maxProtein,
                        onMaxProteinChange = { if (isValidForNumberTextField(it)) maxProtein = it },
                        minTime = minTime,
                        onMinTimeChange = { if (isValidForNumberTextField(it)) minTime = it },
                        maxTime = maxTime,
                        onMaxTimeChange = { if (isValidForNumberTextField(it)) maxTime = it },
                        showAuthorRecipes = showAuthorRecipes,
                        onShowAuthorRecipesChange = { showAuthorRecipes = it },
                        enableButtons
                    )
                }
                if (showGalleryAccessDialog) {
                    LaunchedEffect(Unit) {
                        galleryPermissionState.launchPermissionRequest()
                    }
                }
                if (showConfirmIngredientsDialog) {
                    ConfirmIngredientsDialog(
                        ingredientsState = ingredientsState,
                        onIngredientsClear = onIngredientsClear,
                        onConfirmIngredients = onConfirmIngredients,
                        onCloseDialog = { showConfirmIngredientsDialog = false },
                        enableButtons = enableButtons
                    )
                }
            }
        },
        containerColor = Beige
    )
}
