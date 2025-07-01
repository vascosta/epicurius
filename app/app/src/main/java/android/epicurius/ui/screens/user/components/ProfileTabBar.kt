package android.epicurius.ui.screens.user.components

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp

@Composable
fun ProfileTabBar(
    selectedTabIndex: Int,
    onRecipesClick: () -> Unit,
    onKitchenBookClick: () -> Unit,
    enabled: Boolean
) {
    val tabs = listOf("Recipes", "Kitchen Book")

    Column {
        TabRow(
            selectedTabIndex = selectedTabIndex,
            containerColor = Color.White,
            contentColor = Color.Black
        ) {
            tabs.forEachIndexed { index, name ->
                Tab(
                    selected = selectedTabIndex == index,
                    onClick = {
                        when (index) {
                            0 -> onRecipesClick()
                            1 -> onKitchenBookClick()
                        }
                    },
                    modifier = Modifier.padding(15.dp),
                    enabled = enabled
                ) {
                    Text(name)
                    Spacer(modifier = Modifier.fillMaxHeight(0.05f))
                }
            }
        }
    }
}