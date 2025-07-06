package android.epicurius.ui.screens.mealPlanner.components

import android.epicurius.domain.mealPlanner.MealTime
import android.epicurius.domain.recipe.RecipeInfo
import android.epicurius.ui.screens.recipe.components.RecipeInfoBox
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import java.time.LocalDate

@Composable
fun DailyMealPlannerBox(
    mealTime: MealTime,
    recipe: RecipeInfo?,
    dailyMealPlannerDate: LocalDate,
    onDeleteRecipeFromMealPlanner: (date: LocalDate, mealtime: MealTime) -> Unit = { _, _, -> },
    onAddRecipeToMealPlannerRequest: (date: LocalDate, mealTime: MealTime) -> Unit = {_, _, -> },
    enableButtons: Boolean
) {
    Box(
        modifier = Modifier
            .padding(horizontal = 5.dp)
            .background(Color.White)
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text(
                text = mealTime.displayName,
                modifier = Modifier.padding(bottom = 10.dp),
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
                textAlign = TextAlign.Center,
                color = Color.Black
            )
            if (recipe != null) {
                RecipeInfoBox(
                    recipeInfo = recipe,
                    enableButtons = enableButtons,
                )
                IconButton(
                    onClick = { onDeleteRecipeFromMealPlanner(dailyMealPlannerDate, mealTime) },
                    modifier = Modifier
                        .size(24.dp)
                        .align(Alignment.End),
                    enabled = enableButtons
                ) {
                    Icon(
                        imageVector = Icons.Default.Delete,
                        contentDescription = null,
                        tint = Color(0xFF4E0D8D),
                        modifier = Modifier.size(16.dp)
                    )
                }
            } else {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(
                        text = "${mealTime.displayName} not planned",
                        color = Color.Gray
                    )
                    if (dailyMealPlannerDate == LocalDate.now() ||
                        dailyMealPlannerDate.isAfter(LocalDate.now())
                    ) {
                        TextButton(
                            onClick = { onAddRecipeToMealPlannerRequest(dailyMealPlannerDate, mealTime) },
                            enabled = enableButtons
                        ) {
                            Text(
                                text = "Add Recipe",
                                color = Color(0xFF4E0D8D),
                                fontWeight = FontWeight.Bold
                            )
                        }
                    } else {
                        Spacer(Modifier.height(15.dp))
                    }
                }
            }
        }
    }
}
