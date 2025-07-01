package android.epicurius.ui.navigation

import android.content.Intent
import android.epicurius.R
import android.epicurius.ui.screens.feed.FeedActivity
import android.epicurius.ui.screens.fridge.FridgeActivity
import android.epicurius.ui.screens.mealPlanner.calendar.CalendarActivity
import android.epicurius.ui.screens.recipe.createRecipe.CreateRecipeActivity
import android.epicurius.ui.screens.search.SearchActivity
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.material3.NavigationBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawWithContent
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp

enum class BottomBarState {
    FEED,
    RECIPE,
    SEARCH,
    PLANNER,
    FRIDGE
}

@Composable
fun BottomBar(
    buttonsEnable: Boolean,
    state: BottomBarState? = null
) {
    val context = LocalContext.current
    val currentActivityClass = context::class.java

    var selectedItem by remember {
        mutableStateOf(
            if (state != null) {
                when (state) {
                    BottomBarState.FEED -> FeedActivity::class.java
                    BottomBarState.RECIPE -> CreateRecipeActivity::class.java
                    BottomBarState.SEARCH -> SearchActivity::class.java
                    BottomBarState.PLANNER -> CalendarActivity::class.java
                    BottomBarState.FRIDGE -> FridgeActivity::class.java
                }
            } else {
                currentActivityClass
            }
        )
    }

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
                    if (currentActivityClass != FeedActivity::class.java)
                        context.startActivity(Intent(context, FeedActivity::class.java))
                        selectedItem = FeedActivity::class.java
                },
                enabled = buttonsEnable,
                imageId = R.drawable.home,
                description = "Home",
                isSelected = selectedItem == FeedActivity::class.java
            )
            BottomBarButton(
                onClick = {
                    context.startActivity(Intent(context, CreateRecipeActivity::class.java))
                    selectedItem = CreateRecipeActivity::class.java
                },
                enabled = buttonsEnable && currentActivityClass != CreateRecipeActivity::class.java,
                imageId = R.drawable.pencil,
                description = "Pencil",
                isSelected = selectedItem == CreateRecipeActivity::class.java,
            )
            BottomBarButton(
                onClick = {
                    context.startActivity(Intent(context, SearchActivity::class.java))
                    selectedItem = SearchActivity::class.java
                },
                enabled = buttonsEnable && currentActivityClass != SearchActivity::class.java,
                imageId = R.drawable.magnifier,
                description = "Magnifier",
                imageSize = 41,
                isSelected = selectedItem == SearchActivity::class.java
            )
            BottomBarButton(
                onClick = {
                    context.startActivity(Intent(context, CalendarActivity::class.java))
                    selectedItem = CalendarActivity::class.java
                },
                enabled = buttonsEnable && currentActivityClass != CalendarActivity::class.java,
                imageId = R.drawable.plate,
                description = "Plate",
                imageSize = 45,
                isSelected = selectedItem == CalendarActivity::class.java
            )
            BottomBarButton(
                onClick = {
                    context.startActivity(Intent(context, FridgeActivity::class.java))
                    selectedItem = FridgeActivity::class.java
                },
                enabled = buttonsEnable && currentActivityClass != FridgeActivity::class.java,
                imageId = R.drawable.fridge,
                description = "Fridge",
                imageSize = 40,
                isSelected = selectedItem == FridgeActivity::class.java
            )
        }
    }
}

@Preview
@Composable
fun BottomBarPreview() {
    BottomBar(buttonsEnable = true)
}