package android.epicurius.ui.screens.user.preferences.skipable

import android.epicurius.R
import android.epicurius.ui.navigation.TopBar
import android.epicurius.ui.screens.theme.DarkGreen
import android.epicurius.ui.screens.theme.DarkPurple
import android.epicurius.ui.screens.theme.LightGreen
import android.epicurius.ui.screens.theme.Lilac
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
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
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

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
            Box(modifier = Modifier.fillMaxWidth()) {
                Image(
                    painter = painterResource(id = R.drawable.background_know_more),
                    contentDescription = null,
                    modifier = Modifier.fillMaxSize(),
                    contentScale = ContentScale.Crop
                )
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(paddingValues)
                        .padding(16.dp),
                    verticalArrangement = Arrangement.Center,
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = "We would like to know more about your preferences for a better experience.",
                        color = DarkGreen,
                        fontSize = 30.sp,
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
                            onClick = onSkip,
                            enabled = enableButtons
                        ) { Text("Skip") }
                        Spacer(modifier = Modifier.size(16.dp))
                        TextButton(
                            onClick = onNext,
                            enabled = enableButtons
                        ) { Text("Next") }
                    }
                }
            }
        }
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
