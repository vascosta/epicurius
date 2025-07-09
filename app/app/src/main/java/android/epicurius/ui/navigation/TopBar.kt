package android.epicurius.ui.navigation

import android.epicurius.ui.screens.theme.Beige
import android.epicurius.ui.screens.theme.DarkGreen
import android.epicurius.ui.screens.theme.Lilac
import android.epicurius.ui.screens.user.profile.UserProfileActivity
import android.epicurius.ui.screens.user.settings.SettingsActivity
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.sharp.ArrowBack
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawWithContent
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TopBar(
    titleText: String,
    backButton: Boolean = false,
    onBackButton: () -> Unit = {},
    enableButtons: Boolean,
    icon: ImageVector? = Icons.Filled.Person
) {
    val context = LocalContext.current

    TopAppBar(
        title = { Text(titleText) },
        modifier = Modifier
            .drawWithContent {
                drawContent()
                drawLine(
                    color = Color(0xFFF1E9DA),
                    start = Offset(0f, size.height),
                    end = Offset(size.width, size.height),
                    strokeWidth = 1.dp.toPx()
                )
            },
        navigationIcon = {
            if (backButton) {
                IconButton(
                    onClick = onBackButton,
                    enabled = enableButtons
                ) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Sharp.ArrowBack,
                        contentDescription = "Go Back"
                    )
                }
            }
        },
        actions = {
            icon?.let {
                IconButton(
                    onClick = {
                        if (it == Icons.Filled.Person) { context.navigateTo<UserProfileActivity>(finishCurrent = true) }
                        else { context.navigateTo<SettingsActivity>(finishCurrent = true) }
                    },
                    enabled = enableButtons
                ) {
                    Icon(
                        imageVector = it,
                        contentDescription = "Navigation",
                        tint = Color.Black
                    )
                }
            }
        },
        colors = TopAppBarDefaults.topAppBarColors(
            containerColor = DarkGreen,
            navigationIconContentColor = Beige,
            titleContentColor = Lilac
        ),
    )
}

@Preview
@Composable
fun TopBarPreview() {
    TopBar("Settings", true, enableButtons = true)
}