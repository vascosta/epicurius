package android.epicurius.ui.screens.mealPlanner.search

import android.epicurius.domain.Diet
import android.epicurius.domain.Intolerance
import android.epicurius.domain.mealPlanner.MealTime
import android.epicurius.domain.user.UserInfo
import android.epicurius.ui.navigation.navigateTo
import android.epicurius.ui.screens.mealPlanner.daily.DailyActivity
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import java.time.LocalDate

class MealPlannerSearchActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            MealPlannerSearchScreen(
                userInfo = UserInfo(
                    name = "Chef",
                    email = "chef@example.com",
                    country = "IT",
                    privacy = false,
                    intolerances = listOf(Intolerance.GLUTEN),
                    diets = listOf(Diet.VEGETARIAN),
                    profilePictureName = null
                ),
                date = LocalDate.now(),
                mealTime = MealTime.BREAKFAST,
                onBackButton = { navigateTo<DailyActivity>() },
                onRecipeSearch = { _, _, _,_,_,_, _, _, _, _, _, _, _, _, _ -> emptyList() },
                onAddRecipeToMealPlanner = { _, _, _ -> },
                enableButtons = true
            )
        }
    }
}
