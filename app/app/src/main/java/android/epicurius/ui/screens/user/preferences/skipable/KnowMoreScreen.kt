package android.epicurius.ui.screens.user.preferences.skipable

import android.epicurius.ui.navigation.TopBar
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp

@Composable
fun KnowMoreScreen(
    onSkip: () -> Unit,
    onNext: () -> Unit,
    enableButtons: Boolean
) {
    Scaffold(
        topBar = {
            TopBar(
                titleText = "About you",
                enableButtons = enableButtons,
                icon = null
            )
        },
        content = { paddingValues ->
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
                    .padding(16.dp)
                    .background(Color.White),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = "We would like to know more about your preferences for a better experience.",
                    textAlign = TextAlign.Center,
                    style = MaterialTheme.typography.headlineSmall
                )
                Spacer(modifier = Modifier.size(16.dp))
                Text(
                    text = "You can skip this step and set them later.",
                    textAlign = TextAlign.Center,
                    style = MaterialTheme.typography.bodyMedium
                )
                Spacer(modifier = Modifier.size(50.dp))
                Row {
                    TextButton(
                        onClick = { onSkip() },
                        enabled = enableButtons
                    ) { Text("Skip") }
                    Spacer(modifier = Modifier.size(16.dp))
                    TextButton(
                        onClick = { onNext() },
                        enabled = enableButtons
                    ) { Text("Next") }
                }
            }
        },
        containerColor = Color.White
    )
}

@Preview
@Composable
fun KnowMoreScreenPreview() {
    KnowMoreScreen(
        onSkip = {},
        onNext = {},
        enableButtons = true
    )
}
