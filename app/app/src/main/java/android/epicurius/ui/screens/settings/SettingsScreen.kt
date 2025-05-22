package android.epicurius.ui.screens.settings

import android.epicurius.ui.screens.BottomBar
import android.epicurius.ui.screens.TopBar
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
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp

@Composable
fun SettingsScreens() {
    Scaffold(
        topBar = { TopBar(text = "Settings", backButton = true, icon = null) },
        bottomBar = { BottomBar() },
        content = { paddingValues ->
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
                    .background(Color.White)
            ) {
                Spacer(modifier = Modifier.fillMaxHeight(0.02f))

                SettingsButton("Change username")
                SettingsButton("Change email")
                SettingsButton("Change password")
                SettingsButton("Change privacy")
                SettingsButton("Change intolerances")
                SettingsButton("Change diets")

                Spacer(modifier = Modifier.fillMaxHeight(0.9f))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    SettingsButton("Delete account")
                    SettingsButton("Logout")
                }
            }
        }
    )
}

@Composable
private fun SettingsButton(text: String) {
    TextButton(
        onClick = {},
        modifier = Modifier.padding(start = 15.dp, end = 15.dp)
    ) { Text(text) }
}

@Preview
@Composable
fun SettingsPreview() {
    SettingsScreens()
}