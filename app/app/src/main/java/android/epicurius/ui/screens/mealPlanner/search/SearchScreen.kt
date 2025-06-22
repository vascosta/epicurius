package android.epicurius.ui.screens.mealPlanner.search

import android.epicurius.domain.Diet
import android.epicurius.domain.Intolerance
import android.epicurius.domain.recipe.Cuisine
import android.epicurius.domain.recipe.MealType
import android.epicurius.domain.recipe.RecipeInfo
import android.epicurius.domain.user.UserInfo
import android.epicurius.ui.navigation.BottomBar
import android.epicurius.ui.navigation.TopBar
import android.epicurius.ui.screens.recipe.components.RecipeInfoBox
import android.epicurius.ui.screens.search.components.FiltersDialog
import android.epicurius.ui.screens.search.components.FiltersIcon
import android.epicurius.ui.screens.utils.SearchTextField
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
fun SearchScreen(
    userInfo: UserInfo,
    onBackButton: () -> Unit,
    onRecipeSearch: (
        name: String?,
        mealType: List<MealType>?,
        cuisine: List<Cuisine>?,
        intolerances: List<Intolerance>?,
        diets: List<Diet>?,
        preparationTime: Int?,
        servings: Int?,
        minCalories: Int?,
        maxCalories: Int?,
        minCarbs: Int?,
        maxCarbs: Int?,
        minFat: Int?,
        maxFat: Int?,
        minProtein: Int?,
        maxProtein: Int?
    ) -> List<RecipeInfo>,
    enableButtons: Boolean
) {
    var searchQuery by remember { mutableStateOf("") }

    var showFiltersDialog by remember { mutableStateOf(false) }

    var mealType by remember { mutableStateOf(listOf<String>()) }
    var cuisine by remember { mutableStateOf(listOf<String>()) }
    var intolerances by remember {
        mutableStateOf<List<String>>(userInfo.intolerances.map { it.displayName })
    }
    var diets by remember {
        mutableStateOf<List<String>>(userInfo.diets.map { it.displayName })
    }

    var preparationTime by remember { mutableStateOf("") }
    var serving by remember { mutableStateOf("") }
    var minCalories by remember { mutableStateOf("") }
    var maxCalories by remember { mutableStateOf("") }
    var minCarbs by remember { mutableStateOf("") }
    var maxCarbs by remember { mutableStateOf("") }
    var minFat by remember { mutableStateOf("") }
    var maxFat by remember { mutableStateOf("") }
    var minProtein by remember { mutableStateOf("") }
    var maxProtein by remember { mutableStateOf("") }

    var recipeResults by remember { mutableStateOf<List<RecipeInfo>>(emptyList()) }

    Scaffold(
        topBar = {
            TopBar(
                titleText = "Search Recipes",
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
                        .padding(horizontal = 8.dp),
                    onSearchQueryChange = { searchQuery = it },
                    onIconClick = {
                        recipeResults = onRecipeSearch(
                            searchQuery,
                            mealType.map { MealType.valueOf(it) },
                            cuisine.map { Cuisine.valueOf(it) },
                            intolerances.map { Intolerance.fromDisplayName(it) },
                            diets.map { Diet.fromDisplayName(it) },
                            preparationTime.toIntOrNull(),
                            serving.toIntOrNull(),
                            minCalories.toIntOrNull(),
                            maxCalories.toIntOrNull(),
                            minCarbs.toIntOrNull(),
                            maxCarbs.toIntOrNull(),
                            minFat.toIntOrNull(),
                            maxFat.toIntOrNull(),
                            minProtein.toIntOrNull(),
                            maxProtein.toIntOrNull()
                        )
                    },
                    enableButtons = enableButtons
                )
                Row(modifier = Modifier.fillMaxWidth()) {
                    FiltersIcon(onClick = { showFiltersDialog = true })
                }

                if (recipeResults.isNotEmpty()) {
                    recipeResults.forEach { recipe ->
                        RecipeInfoBox(
                            collectionId = null,
                            recipeInfo = recipe,
                            collectionsStateBundle = null,
                            onAddRecipeToCollections = { _, _, _, _ -> },
                            onRemoveRecipeFromCollections = { _, _, _, _ -> },
                            onRemoveRecipeFromCollection = { _, _ -> },
                            onCollectionsClear = {  },
                            onRecipeRequest = {  },
                            onCollectionsRequest = {  },
                            enableButtons = enableButtons,
                        )
                        Spacer(Modifier.height(10.dp))
                    }
                } else {
                    Text("No recipes found", color = Color.Gray)
                }

                if (showFiltersDialog) {
                    FiltersDialog(
                        onDismiss = { showFiltersDialog = false },
                        onCancel = { showFiltersDialog = false },
                        mealType = mealType,
                        onMealTypeChange = { mealType = it },
                        cuisine = cuisine,
                        onCuisineChange = { cuisine = it },
                        intolerances = intolerances,
                        onIntolerancesChange = { intolerances = it },
                        diets = diets,
                        onDietsChange = { diets = it },
                        preparationTime = preparationTime,
                        onPreparationTimeChange = { preparationTime = it },
                        servings = serving,
                        onServingsChange = { serving = it },
                        minCalories = minCalories,
                        onMinCaloriesChange = { minCalories = it },
                        maxCalories = maxCalories,
                        onMaxCaloriesChange = { maxCalories = it },
                        minCarbs = minCarbs,
                        onMinCarbsChange = { minCarbs = it },
                        maxCarbs = maxCarbs,
                        onMaxCarbsChange = { maxCarbs = it },
                        minFat = minFat,
                        onMinFatChange = { minFat = it },
                        maxFat = maxFat,
                        onMaxFatChange = { maxFat = it },
                        minProtein = minProtein,
                        onMinProteinChange = { minProtein = it },
                        maxProtein = maxProtein,
                        onMaxProteinChange = { maxProtein = it },
                        true
                    )
                }
            }
        },
        containerColor = Color.White
    )
}

@Preview
@Composable
fun SearchScreenPreview() {
    val userInfo = UserInfo(
        name = "Chef",
        email = "chef@example.com",
        country = "IT",
        privacy = false,
        intolerances = listOf(Intolerance.GLUTEN),
        diets = listOf(Diet.VEGETARIAN),
        profilePictureName = null
    )

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
        userInfo = userInfo,
        onBackButton = {  },
        onRecipeSearch = {
            _, _, _,_,_,_, _, _, _, _, _, _, _, _, _ -> recipeList
        },
        enableButtons = true
    )
}
