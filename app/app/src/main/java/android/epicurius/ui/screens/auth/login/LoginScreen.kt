package android.epicurius.ui.screens.auth.login

import android.annotation.SuppressLint
import android.epicurius.ui.navigation.TopBar
import android.epicurius.ui.screens.auth.components.AuthButton
import android.epicurius.ui.screens.auth.components.PasswordTextField
import android.epicurius.ui.screens.utils.TextField
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
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
fun LoginScreen(
    loginEnable: Boolean,
    onSignUp: () -> Unit,
    onLogin: (String, String, String) -> Unit
) {
    var username by rememberSaveable { mutableStateOf("") }
    var email by rememberSaveable { mutableStateOf("") }
    var password by rememberSaveable { mutableStateOf("") }

    Scaffold(
        topBar = { TopBar(text = "Login") }
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
            TextField(value = email, onValueChange = { email = it }, label = "Email")
            PasswordTextField(value = password, onValueChange = { password = it }, label = "Password")

            Row {
                AuthButton(
                    label = "SignUp",
                    onClick = { onSignUp() },
                    enabled = loginEnable
                )
                AuthButton(
                    label = "Login",
                    onClick = { onLogin(username, email, password) },
                    enabled = loginEnable
                )
            }
        }
    }
}

@Preview
@Composable
fun LoginPreview() {
   LoginScreen(true, {}, {_, _, _ -> })
}