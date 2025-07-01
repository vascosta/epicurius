package android.epicurius.ui.screens.mealPlanner.weekly

import android.epicurius.domain.mealPlanner.DailyMealPlanner
import android.epicurius.domain.mealPlanner.MealPlanner
import android.epicurius.domain.mealPlanner.MealTime
import android.epicurius.domain.recipe.Cuisine
import android.epicurius.domain.recipe.MealType
import android.epicurius.domain.recipe.RecipeInfo
import android.epicurius.ui.navigation.BottomBar
import android.epicurius.ui.navigation.TopBar
import android.epicurius.domain.mealPlanner.utils.getWeek
import android.epicurius.ui.navigation.BottomBarState
import android.epicurius.ui.screens.mealPlanner.components.MealPlannerComponent
import android.epicurius.ui.screens.mealPlanner.components.WeekCalendarRow
import android.epicurius.ui.screens.mealPlanner.daily.components.CaloriesUpdateDialog
import android.epicurius.ui.screens.utils.MixedText
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
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
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import java.time.LocalDate

@Composable
fun WeeklyScreen(
    week: List<LocalDate>,
    mealPlanner: MealPlanner,
    onBackButton: () -> Unit,
    onCaloriesUpdate: (Int) -> Unit,
    onAddRecipe: () -> Unit,
    onDeleteRecipe: (date: LocalDate, mealtime: MealTime) -> Unit
) {
    var selectedDay by remember { mutableStateOf(LocalDate.now()) }
    val selectMealPlanner = mealPlanner.planner.find { daily -> daily.date == selectedDay }

    var showDialog by remember { mutableStateOf(false) }

    Scaffold(
        topBar = {
            TopBar(
                titleText = "Weekly Meal Planner",
                backButton = true,
                onBackButton = onBackButton,
                enableButtons = true
            )
        },
        bottomBar = {
            BottomBar(
                buttonsEnable = true,
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
                WeekCalendarRow()

                Row(Modifier.fillMaxWidth()) {
                    week.forEach { date ->
                        Text(
                            text = date.dayOfMonth.toString(),
                            modifier = Modifier
                                .weight(1f)
                                .clip(RoundedCornerShape(8.dp))
                                .border(1.dp, Color.LightGray)
                                .padding(16.dp)
                                .clickable { selectedDay = date },
                            textAlign = TextAlign.Center,
                            fontWeight = FontWeight.Normal,
                            color = if (date == selectedDay) Color(0xFFAC88DC) else Color.Black
                        )
                    }
                }

                val dailyCalories = selectMealPlanner?.maxCalories
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .wrapContentSize(Alignment.Center)
                        .padding(top = 16.dp)
                        .clickable(
                            onClick = { showDialog = true },
                            enabled = selectedDay == LocalDate.now() ||
                                    selectedDay.isAfter(LocalDate.now())
                        )
                ) {
                    MixedText(
                        boldString = "Max Calories: ",
                        normalString = dailyCalories?.toString() ?: "Not set"
                    )
                }

                if (showDialog) {
                    CaloriesUpdateDialog(
                        initialValue = dailyCalories?.toString() ?: "",
                        onDismiss = { showDialog = false },
                        onConfirm = {
                            onCaloriesUpdate(it)
                            showDialog = false
                        }
                    )
                }

                Spacer(Modifier.height(20.dp))
                MealPlannerComponent(
                    dailyPlanner = selectMealPlanner,
                    date = selectedDay,
                    onAddRecipe = onAddRecipe,
                    onDeleteRecipe = onDeleteRecipe
                )
            }
        },
        containerColor = Color.White
    )
}

@Preview
@Composable
fun WeeklyScreenPreview() {
    val week = getWeek(LocalDate.now())
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
            )
        )
    )
    WeeklyScreen(
        week = week,
        mealPlanner = mealPlanner,
        onBackButton = {},
        onCaloriesUpdate = {},
        onAddRecipe = {},
        onDeleteRecipe = { _, _ -> }
    )
}
