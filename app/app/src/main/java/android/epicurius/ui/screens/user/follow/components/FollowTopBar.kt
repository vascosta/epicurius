package android.epicurius.ui.screens.user.follow.components

import android.epicurius.ui.screens.utils.TabComponent
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.sharp.ArrowBack
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FollowTopBar(
    selectedTabIndex: Int,
    followersCount: Int,
    followingCount: Int,
    onTabSelected: (tabIndex: Int) -> Unit = {},
    onBackButton: () -> Unit = {},
    enabled: Boolean
) {
    val tabs = listOf("$followersCount Followers", "$followingCount Following")

    TopAppBar(
        title = {
            Row(
                modifier = Modifier
                    .height(56.dp)
                    .fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                TabComponent(tabs, selectedTabIndex, onTabSelected, enabled)
            }
        },
        navigationIcon = {
            IconButton(
                onClick = { onBackButton()},
                enabled = enabled
            ) {
                Icon(
                    imageVector = Icons.AutoMirrored.Sharp.ArrowBack,
                    contentDescription = "Go Back"
                )
            }
        },
        colors = TopAppBarDefaults.topAppBarColors(
            containerColor = Color.White,
            titleContentColor = Color.Black
        )
    )
}

@Preview
@Composable
fun FollowTopBarPreview() {
    FollowTopBar(100, 200, 0, enabled = true)
}