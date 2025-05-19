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
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp

@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@Composable
fun LoginScreen() {
    var username by rememberSaveable { mutableStateOf("") }
    var email by rememberSaveable { mutableStateOf("") }
    var password by rememberSaveable { mutableStateOf("") }

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
            TextField(value = username, onValueChange = { username = it }, label = "Username")
            TextField(value = email, onValueChange = {email = it}, label = "Email")
            TextField(value = password, onValueChange = {password = it}, label = "Password")

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