package android.epicurius.ui.screens.mealPlanner.daily

import android.epicurius.domain.mealPlanner.DailyMealPlanner
import android.epicurius.domain.mealPlanner.MealTime
import android.epicurius.domain.recipe.Cuisine
import android.epicurius.domain.recipe.MealType
import android.epicurius.domain.recipe.RecipeInfo
import android.epicurius.ui.screens.mealPlanner.calendar.CalendarActivity
import android.epicurius.ui.navigation.navigateTo
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import java.time.LocalDate

class DailyActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            DailyScreen(
                onBackButton = { navigateTo<CalendarActivity>() },
                date = LocalDate.now(),
                dailyMealPlanner = DailyMealPlanner(
                    date = LocalDate.now(),
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
                            picture = ByteArray(0),
                            isInCollection = false
                        )
                    )
                )
            )
        }
    }
}