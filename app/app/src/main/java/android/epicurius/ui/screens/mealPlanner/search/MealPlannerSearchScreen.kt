package android.epicurius.ui.screens.mealPlanner.search

import android.epicurius.domain.Diet
import android.epicurius.domain.Intolerance
import android.epicurius.domain.mealPlanner.MealTime
import android.epicurius.domain.recipe.Cuisine
import android.epicurius.domain.recipe.MealType
import android.epicurius.domain.recipe.RecipeInfo
import android.epicurius.domain.user.UserInfo
import android.epicurius.ui.navigation.BottomBar
import android.epicurius.ui.navigation.BottomBarState
import android.epicurius.ui.navigation.TopBar
import android.epicurius.ui.screens.recipe.components.RecipeInfoBox
import android.epicurius.ui.screens.search.components.FiltersDialog
import android.epicurius.ui.screens.search.components.FiltersIcon
import android.epicurius.ui.screens.search.components.clearFilters
import android.epicurius.ui.screens.utils.Idle
import android.epicurius.ui.screens.utils.LoadState
import android.epicurius.ui.screens.utils.LoadStateRenderer
import android.epicurius.ui.screens.utils.Loaded
import android.epicurius.ui.screens.utils.SearchTextField
import android.epicurius.ui.screens.utils.apiSuccess
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
import androidx.compose.material3.Button
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import java.time.LocalDate

@Composable
fun MealPlannerSearchScreen(
    userInfoState: LoadState<UserInfo>,
    date: LocalDate,
    mealTime: MealTime,
    recipesResultState: LoadState<List<RecipeInfo>>,
    onBackButton: () -> Unit = {},
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
    onAddRecipeToMealPlanner: (
        date: LocalDate,
        recipeId: Int,
        mealTime: MealTime
    ) -> Unit = { _, _, _ -> },
    onSearchRecipesClear: () -> Unit = {},
    enableButtons: Boolean
) {
    var showFiltersDialog by remember { mutableStateOf(false) }

    var searchQuery by remember { mutableStateOf("") }
    var searchRecipesQuery by remember { mutableStateOf("") }

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

    LaunchedEffect(userInfoState) {
        if (userInfoState is Loaded) {
            val userInfo = userInfoState.value.getValueOrThrow()
            intolerances = userInfo.intolerances.map { it.displayName }
            diets = userInfo.diets.map { it.displayName }
        }
    }
    Scaffold(
        topBar = {
            TopBar(
                titleText = "Search Recipes",
                backButton = true,
                onBackButton = onBackButton,
                enableButtons = enableButtons
            )
        },
        bottomBar = {
            BottomBar(
                buttonsEnable = enableButtons,
                state = BottomBarState.PLANNER
            )
        },
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
                            searchQuery,
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
                    },
                    enableButtons = enableButtons
                )
                Row(modifier = Modifier.fillMaxWidth()) {
                    if (recipesResultState is Idle) {
                        FiltersIcon(onClick = { showFiltersDialog = true }, enableButtons = enableButtons)
                        TextButton(
                            onClick = {
                                onSearchRecipesClear()
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
                }
                LoadStateRenderer(
                    loadState = recipesResultState,
                    content = { recipesResult ->
                        TextButton(
                            onClick = {
                                onSearchRecipesClear()
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
                                    date = date,
                                    mealTime = mealTime,
                                    onAddRecipeToMealPlanner = onAddRecipeToMealPlanner,
                                    enableButtons = enableButtons,
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
                                        searchRecipesQuery,
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
                if (showFiltersDialog) {
                    FiltersDialog(
                        onDismiss = { showFiltersDialog = false },
                        onCancel = { showFiltersDialog = false },
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
        userInfoState = apiSuccess(userInfo),
        date = LocalDate.now(),
        mealTime = MealTime.LUNCH,
        recipesResultState = apiSuccess(recipeList),
        enableButtons = true
    )
}
