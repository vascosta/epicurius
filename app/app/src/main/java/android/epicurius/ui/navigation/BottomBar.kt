package android.epicurius.ui.navigation

import android.content.Intent
import android.epicurius.R
import android.epicurius.ui.screens.feed.FeedActivity
import android.epicurius.ui.screens.fridge.FridgeActivity
import android.epicurius.ui.screens.mealPlanner.calendar.CalendarActivity
import android.epicurius.ui.screens.recipe.createRecipe.CreateRecipeActivity
import android.epicurius.ui.screens.search.general.SearchActivity
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.material3.NavigationBar
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawWithContent
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp

@Composable
fun BottomBar(onFeedRefresh: () -> Unit = {}) {
    val context = LocalContext.current
    val currentActivityClass = context::class.java
    NavigationBar(containerColor = Color.White) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .height(79.dp)
                .drawWithContent {
                    drawContent()
                    drawLine(
                        color = Color.Black,
                        start = Offset(0f, 0f),
                        end = Offset(size.width, 0f),
                        strokeWidth = 1.dp.toPx()
                    )
                },
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            BottomBarButton(
                onClick = {
                    if (currentActivityClass != FeedActivity::class.java) {
                        context.startActivity(Intent(context, FeedActivity::class.java))
                    }
                    else { onFeedRefresh }
                },
                enabled = true,
                imageId = R.drawable.home,
                description = "Home"
            )
            BottomBarButton(
                onClick = { context.startActivity(Intent(context, CreateRecipeActivity::class.java)) },
                enabled = currentActivityClass != CreateRecipeActivity::class.java,
                imageId = R.drawable.pencil,
                description = "Pencil"
            )
            BottomBarButton(
                onClick = { context.startActivity(Intent(context, SearchActivity::class.java)) },
                enabled = currentActivityClass != SearchActivity::class.java,
                imageId = R.drawable.magnifier,
                description = "Magnifier",
                imageSize = 41
            )
            BottomBarButton(
                onClick = { context.startActivity(Intent(context, CalendarActivity::class.java)) },
                enabled = currentActivityClass != CalendarActivity::class.java,
                imageId = R.drawable.plate,
                description = "Plate",
                imageSize = 45
            )
            BottomBarButton(
                onClick = { context.startActivity(Intent(context, FridgeActivity::class.java)) },
                enabled = currentActivityClass != FridgeActivity::class.java,
                imageId = R.drawable.fridge,
                description = "Fridge",
                imageSize = 40
            )
        }
    }
}

@Preview
@Composable
fun BottomBarPreview() {
    BottomBar()
}