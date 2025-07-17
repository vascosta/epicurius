package android.epicurius.ui.screens.mealPlanner.daily

import android.epicurius.domain.mealPlanner.DailyMealPlanner
import android.epicurius.domain.mealPlanner.MealPlanner
import android.epicurius.domain.mealPlanner.MealTime
import android.epicurius.domain.recipe.Cuisine
import android.epicurius.domain.recipe.MealType
import android.epicurius.domain.recipe.RecipeInfo
import android.epicurius.ui.navigation.BottomBar
import android.epicurius.ui.navigation.BottomBarState
import android.epicurius.ui.navigation.TopBar
import android.epicurius.ui.screens.mealPlanner.daily.components.CaloriesUpdateDialog
import android.epicurius.ui.screens.mealPlanner.components.MealPlannerComponent
import android.epicurius.ui.screens.theme.Beige
import android.epicurius.ui.screens.theme.DarkPurple
import android.epicurius.ui.screens.utils.LoadState
import android.epicurius.ui.screens.utils.LoadStateRenderer
import android.epicurius.ui.screens.utils.MixedText
import android.epicurius.ui.screens.utils.apiSuccess
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
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

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DailyScreen(
    dailyMealPlannerState: LoadState<DailyMealPlanner>,
    date: LocalDate,
    onBackButton: () -> Unit = {},
    onUpdateDailyCalories: (calories: Int) -> Unit = {},
    onDeleteRecipeFromMealPlanner: (date: LocalDate, mealtime: MealTime) -> Unit = { _, _, -> },
    onAddRecipeToMealPlannerRequest: (date: LocalDate, mealTime: MealTime) -> Unit = { _, _, ->},
    enableButtons: Boolean
) {
    var showUpdateDailyCaloriesDialog by remember { mutableStateOf(false) }

    Scaffold(
        topBar = {
            TopBar(
                titleText = "Daily Meal Planner",
                backButton = true,
                onBackButton = onBackButton,
                enableButtons = enableButtons
            )
        },
        bottomBar = {
            BottomBar(
                buttonsEnable = true,
                state = BottomBarState.PLANNER
            )
        },
        content = { paddingValues ->
            LoadStateRenderer(
                loadState = dailyMealPlannerState,
                content = { dailyMealPlanner ->
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .verticalScroll(rememberScrollState())
                            .padding(paddingValues)
                            .padding(16.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            text = "Meal Planner for $date",
                            modifier = Modifier
                                .fillMaxWidth()
                                .wrapContentSize(Alignment.Center)
                                .padding(bottom = 16.dp),
                            color = DarkPurple,
                            style = MaterialTheme.typography.titleMedium
                        )
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .wrapContentSize(Alignment.Center)
                                .padding(bottom = 16.dp)
                                .clickable(
                                    onClick = { showUpdateDailyCaloriesDialog = true },
                                    enabled = enableButtons && (date == LocalDate.now() ||
                                            date.isAfter(LocalDate.now()))
                                )
                        ) {
                            MixedText(
                                boldString = "Max Calories: ",
                                normalString = dailyMealPlanner.maxCalories?.toString() ?: "Not set"
                            )
                        }
                        MealPlannerComponent(
                            dailyPlanner = dailyMealPlanner,
                            date = date,
                            onAddRecipeToMealPlannerRequest = onAddRecipeToMealPlannerRequest,
                            onDeleteRecipeFromMealPlanner = onDeleteRecipeFromMealPlanner,
                            enableButtons = enableButtons
                        )
                        if (showUpdateDailyCaloriesDialog) {
                            CaloriesUpdateDialog(
                                initialValue = dailyMealPlanner.maxCalories?.toString() ?: "",
                                onDismiss = { showUpdateDailyCaloriesDialog = false },
                                onUpdateCalories = onUpdateDailyCalories,
                                enableButtons = enableButtons
                            )
                        }
                    }
                }
            )
        },
        containerColor = Beige
    )
}

@Preview
@Composable
fun DailyScreenPreview() {
    val mealPlanner = MealPlanner(
        listOf(
            DailyMealPlanner(
                date = LocalDate.now(),
                maxCalories = null,
                meals = mapOf(
                    MealTime.BREAKFAST to RecipeInfo(
                        id = 1,
                        name = "Pancakes",
                        authorUsername = "ChefBear",
                        rating = 4.3,
                        cuisine = Cuisine.AMERICAN,
                        mealType = MealType.BREAKFAST,
                        preparationTime = 15,
                        servings = 2,
                        picture = ""
                    ),
                    MealTime.LUNCH to RecipeInfo(
                        id = 2,
                        name = "Caesar Salad",
                        authorUsername = "ChefBear",
                        rating = 4.3,
                        cuisine = Cuisine.ITALIAN,
                        mealType = MealType.SALAD,
                        preparationTime = 10,
                        servings = 1,
                        picture = ""
                    ),
                )
            ),
            DailyMealPlanner(
                date = LocalDate.now().plusDays(1),
                maxCalories = null,
                meals = mapOf(
                    MealTime.DINNER to RecipeInfo(
                        id = 3,
                        name = "Spaghetti Bolognese",
                        authorUsername = "ChefBear",
                        rating = 4.3,
                        cuisine = Cuisine.ITALIAN,
                        mealType = MealType.MAIN_COURSE,
                        preparationTime = 30,
                        servings = 4,
                        picture = ""
                    )
                )
            ),
            DailyMealPlanner(
                date = LocalDate.now().plusDays(2),
                maxCalories = null,
                meals = mapOf(
                    MealTime.SNACK to RecipeInfo(
                        id = 4,
                        name = "Fruit Smoothie",
                        authorUsername = "ChefBear",
                        rating = 4.3,
                        cuisine = Cuisine.AMERICAN,
                        mealType = MealType.SNACK,
                        preparationTime = 5,
                        servings = 1,
                        picture = ""
                    )
                )
            ),
            DailyMealPlanner(
                date = LocalDate.now().plusDays(3),
                maxCalories = null,
                meals = emptyMap()
            )
        )
    )

    DailyScreen(
        dailyMealPlannerState = apiSuccess(mealPlanner.planner.first()),
        date = LocalDate.now(),
        enableButtons = true
    )
}
