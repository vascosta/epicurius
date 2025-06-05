package android.epicurius.ui.screens.mealPlanner.daily

import android.epicurius.domain.mealPlanner.DailyMealPlanner
import android.epicurius.domain.mealPlanner.MealPlanner
import android.epicurius.domain.mealPlanner.MealTime
import android.epicurius.domain.recipe.Cuisine
import android.epicurius.domain.recipe.MealType
import android.epicurius.domain.recipe.RecipeInfo
import android.epicurius.ui.navigation.BottomBar
import android.epicurius.ui.navigation.TopBar
import android.epicurius.ui.screens.mealPlanner.daily.components.CaloriesUpdateDialog
import android.epicurius.ui.screens.mealPlanner.components.MealPlannerComponent
import android.epicurius.ui.screens.utils.MixedText
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
    onBackButton: () -> Unit = {},
    onCaloriesUpdate: (Int) -> Unit = {},
    date: LocalDate,
    dailyMealPlanner: DailyMealPlanner?
) {
    var showDialog by remember { mutableStateOf(false) }

    Scaffold(
        topBar = { TopBar("Daily Meal Planner", backButton = true, onBackButton) },
        bottomBar = { BottomBar() },
        content = { paddingValues ->
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
                    color = Color(0xFFAC88DC),
                    style = MaterialTheme.typography.titleMedium
                )

                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .wrapContentSize(Alignment.Center)
                        .padding(bottom = 16.dp)
                        .clickable { showDialog = true }
                ) {
                    MixedText(
                        boldString = "Max Calories: ",
                        normalString = dailyMealPlanner?.maxCalories?.toString() ?: "Not set"
                    )
                }

                CaloriesUpdateDialog(
                    visible = showDialog,
                    initialValue = dailyMealPlanner?.maxCalories?.toString() ?: "",
                    onDismiss = { showDialog = false },
                    onConfirm = {
                        onCaloriesUpdate(it)
                        showDialog = false
                    }
                )

                MealPlannerComponent(dailyMealPlanner)
            }
        },
        containerColor = Color.White
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
                        picture = ByteArray(0)
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
                        picture = ByteArray(0)
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
                        picture = ByteArray(0)
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
                        picture = ByteArray(0)
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
        onBackButton = {},
        date = LocalDate.now(),
        dailyMealPlanner = mealPlanner.planner.first()
    )
}
