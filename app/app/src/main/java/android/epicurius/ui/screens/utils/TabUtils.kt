package android.epicurius.ui.screens.utils

import android.epicurius.ui.screens.theme.DarkPurple
import android.epicurius.ui.screens.theme.Lilac
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun TabComponent(
    tabs: List<String>,
    selectedTabIndex: Int,
    onTabSelected: (Int) -> Unit,
    colorTittleTab: Color = DarkPurple,
    enabled: Boolean
) {
    Column {
        TabRow(
            selectedTabIndex = selectedTabIndex,
            containerColor = Color.Transparent,
            contentColor = colorTittleTab,
        ) {
            tabs.forEachIndexed { index, name ->
                Tab(
                    selected = selectedTabIndex == index,
                    onClick = { onTabSelected(index) },
                    modifier = Modifier.padding(15.dp),
                    enabled = enabled
                ) {
                    Text(text = name, fontSize = 15.sp)
                }
            }
        }
    }
}
