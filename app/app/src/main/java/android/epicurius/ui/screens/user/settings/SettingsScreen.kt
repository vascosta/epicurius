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
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp

@Composable
fun SettingsScreen(onBackButton: () -> Unit = {}) {
    Scaffold(
        topBar = { TopBar(text = "Settings", backButton = true, onBackButton = onBackButton, icon = null) },
        bottomBar = { BottomBar() },
        content = { paddingValues ->
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
                    .background(Color.White)
            ) {
                Spacer(modifier = Modifier.fillMaxHeight(0.02f))

                SettingsButton("Favorites")
                SettingsButton("Change username")
                SettingsButton("Change email")
                SettingsButton("Change password")
                SettingsButton("Change country")
                SettingsButton("Change privacy")
                SettingsButton("Change intolerances")
                SettingsButton("Change diets")

                Spacer(modifier = Modifier.fillMaxHeight(0.8f))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    SettingsButton("Delete account")
                    SettingsButton("Logout")
                }
            }
        }
    )
}



@Preview
@Composable
fun SettingsPreview() {
    SettingsScreen()
}