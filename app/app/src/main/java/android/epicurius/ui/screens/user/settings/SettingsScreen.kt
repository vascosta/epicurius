package android.epicurius.ui.screens.user.settings

import android.epicurius.ui.navigation.BottomBar
import android.epicurius.ui.navigation.TopBar
import android.epicurius.ui.screens.user.settings.components.SettingsButton
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview

@Composable
fun SettingsScreen(
    onBackButton: () -> Unit,
    onFavouritesRequest: () -> Unit,
    onLogout: () -> Unit,
    buttonsEnable: Boolean
) {
    Scaffold(
        topBar = { TopBar(
            titleText = "Settings",
            backButton = true,
            onBackButton = onBackButton,
            buttonsEnable = buttonsEnable,
            icon = null
        ) },
        bottomBar = { BottomBar(buttonsEnable = buttonsEnable) },
        content = { paddingValues ->
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
                    .background(Color.White)
            ) {
                Spacer(modifier = Modifier.fillMaxHeight(0.02f))

                SettingsButton(
                    onClick = onFavouritesRequest,
                    enabled = buttonsEnable,
                    text = "Favorites"
                )
                SettingsButton(
                    onClick = {},
                    enabled = buttonsEnable,
                    text = "Change username"
                )
                SettingsButton(
                    onClick = {},
                    enabled = buttonsEnable,
                    text = "Change email"
                )
                SettingsButton(
                    onClick = {},
                    enabled = buttonsEnable,
                    text = "Change password"
                )
                SettingsButton(
                    onClick = {},
                    enabled = buttonsEnable,
                    text = "Change country"
                )
                SettingsButton(
                    onClick = {},
                    enabled = buttonsEnable,
                    text = "Change privacy"
                )
                SettingsButton(
                    onClick = {},
                    enabled = buttonsEnable,
                    text = "Change intolerances"
                )
                SettingsButton(
                    onClick = {},
                    enabled = buttonsEnable,
                    text = "Change diets"
                )

                Spacer(modifier = Modifier.fillMaxHeight(0.8f))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    SettingsButton(
                        onClick = {},
                        enabled = buttonsEnable,
                        text = "Delete account"
                    )
                    SettingsButton(
                        onClick = onLogout,
                        enabled = buttonsEnable,
                        text = "Logout"
                    )
                }
            }
        }
    )
}



@Preview
@Composable
fun SettingsPreview() {
    SettingsScreen({}, {}, {}, true)
}