package android.epicurius.ui.screens.mealPlanner.components

import android.epicurius.domain.mealPlanner.MealTime
import android.epicurius.domain.recipe.RecipeInfo
import android.epicurius.ui.screens.recipe.components.RecipeInfoSimpleBox
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
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

@Composable
fun DailyMealPlannerBox(mealTime: MealTime, recipe: RecipeInfo?) {
    Box(modifier = Modifier.padding(10.dp).background(Color.White)) {
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
                RecipeInfoSimpleBox(recipe)
                IconButton(
                    onClick = {  },
                    modifier = Modifier
                        .size(24.dp)
                        .align(Alignment.End)
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
                    TextButton(
                        onClick = { }
                    ) {
                        Text(
                            text = "Add Recipe",
                            color = Color(0xFF4E0D8D),
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }
        }
    }
}
