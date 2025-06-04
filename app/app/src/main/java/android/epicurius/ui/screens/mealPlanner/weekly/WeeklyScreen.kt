package android.epicurius.ui.screens.mealPlanner.weekly

import android.epicurius.domain.mealPlanner.DailyMealPlanner
import android.epicurius.domain.mealPlanner.MealPlanner
import android.epicurius.domain.mealPlanner.MealTime
import android.epicurius.domain.recipe.Cuisine
import android.epicurius.domain.recipe.MealType
import android.epicurius.domain.recipe.RecipeInfo
import android.epicurius.ui.screens.BottomBar
import android.epicurius.ui.screens.TopBar
import android.epicurius.domain.mealPlanner.utils.getWeek
import android.epicurius.ui.screens.mealPlanner.components.MealPlannerComponent
import android.epicurius.ui.screens.mealPlanner.components.WeekCalendarRow
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
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
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import java.time.LocalDate

@Composable
fun WeeklyScreen(
    onBackButton: () -> Unit = {},
    week: List<LocalDate>,
    mealPlanner: MealPlanner
) {
    var selectedDay by remember { mutableStateOf(LocalDate.now()) }
    val selectMealPlanner = mealPlanner.planner.find { daily -> daily.date == selectedDay }

    Scaffold(
        topBar = { TopBar("Weekly Meal Planner", backButton = true, onBackButton) },
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
                            color = if (date == selectedDay) Color(0xFF4E0D8D) else Color.Black
                        )
                    }
                }

                Spacer(Modifier.height(20.dp))
                MealPlannerComponent(selectMealPlanner)
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
                        cuisine = Cuisine.AMERICAN,
                        mealType = MealType.SNACK,
                        preparationTime = 5,
                        servings = 1,
                        picture = ByteArray(0)
                    )
                )
            )
        )
    )
    WeeklyScreen(week = week, mealPlanner = mealPlanner)
}
