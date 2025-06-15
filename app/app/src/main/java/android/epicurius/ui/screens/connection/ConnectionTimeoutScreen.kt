package android.epicurius.ui.screens.connection

import android.epicurius.ui.navigation.BottomBar
import android.epicurius.ui.navigation.TopBar
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun ConnectionTimeoutScreen() {
    Scaffold(
        topBar = {
            TopBar(
                titleText = "Epicurius",
                enableButtons = false,
                icon = null
            )
        },
        bottomBar = { BottomBar(buttonsEnable = false) },
        content = { paddingValues ->
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
                    .padding(16.dp)
                    .background(Color.White)
                    .verticalScroll(rememberScrollState()),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                Text(
                    text = "Server is Offline",
                    modifier = Modifier.padding(bottom = 16.dp),
                    color = Color.Black,
                    fontSize = 24.sp,
                )

                Text(
                    text = "In the meantime try to think about new recipes to share!",
                    modifier = Modifier.padding(bottom = 8.dp),
                    color = Color.Gray,
                    fontSize = 16.sp,
                    textAlign = TextAlign.Center
                )
            }
        },
        containerColor = Color.White
    )
}

@Preview
@Composable
fun ConnectionTimeoutPreview() {
    ConnectionTimeoutScreen()
}
