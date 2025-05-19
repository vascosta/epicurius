package android.epicurius.ui.screens.auth.login

import android.annotation.SuppressLint
import android.epicurius.ui.screens.TopBar
import android.epicurius.ui.utils.TextField
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp

@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@Composable
fun LoginScreen() {
    Scaffold(
        topBar = { TopBar("LogIn") }
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp)
                .background(Color.Companion.White),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            TextField(value = "", onValueChange = {}, label = "Username")
            TextField(value = "", onValueChange = {}, label = "Email")
            TextField(value = "", onValueChange = {}, label = "Password")

            Row {
                Button(
                    onClick = {},
                    modifier = Modifier.padding(10.dp)
                ) { Text("SignUp") }

                Button(
                    onClick = {},
                    modifier = Modifier.padding(10.dp)
                ) { Text("Login") }
            }
        }
    }
}

@Preview
@Composable
fun LoginPreview() {
   LoginScreen()
}