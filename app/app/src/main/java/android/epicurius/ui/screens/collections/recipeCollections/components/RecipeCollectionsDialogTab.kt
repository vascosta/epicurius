package android.epicurius.ui.screens.collections.recipeCollections.components

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp

@Composable
fun RecipeCollectionsDialogTab(
    selectedTabIndex: Int,
    onCollectionsToAdd: () -> Unit = {},
    onCollectionsToRemove: () -> Unit = {},
    enabled: Boolean
) {
    val tabs = listOf("Collections to Add", "Collections to Remove")

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
                            0 -> onCollectionsToAdd()
                            1 -> onCollectionsToRemove()
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