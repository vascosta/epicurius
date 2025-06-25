package android.epicurius.ui.screens.mealPlanner.search

import android.epicurius.domain.Diet
import android.epicurius.domain.Intolerance
import android.epicurius.domain.mealPlanner.MealTime
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
import android.epicurius.ui.screens.utils.isValidForNumberTextField
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
import java.time.LocalDate

@Composable
fun MealPlannerSearchScreen(
    userInfo: UserInfo,
    date: LocalDate,
    mealTime: MealTime,
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
    onAddRecipeToMealPlanner: (
        date: LocalDate,
        recipeId: Int,
        mealTime: MealTime
    ) -> Unit,
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
                            null,
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
                            date = date,
                            mealTime = mealTime,
                            onAddRecipeToMealPlanner = onAddRecipeToMealPlanner,
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
                        cuisine = cuisine,
                        onCuisineChange = { cuisine = it },
                        mealType = mealType,
                        onMealTypeChange = { mealType = it },
                        ingredients = emptyList(),
                        onIngredientsChange = { /* No ingredients in meal planner */ },
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
fun MealPlannerSearchScreenPreview() {
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
            picture = ""
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
            picture = ""
        )
    )

    MealPlannerSearchScreen(
        userInfo = userInfo,
        date = LocalDate.now(),
        mealTime = MealTime.LUNCH,
        onBackButton = {  },
        onRecipeSearch = { _, _, _,_,_,_, _, _, _, _, _, _, _, _, _ -> recipeList },
        onAddRecipeToMealPlanner = { _, _, _ -> },
        enableButtons = true
    )
}
