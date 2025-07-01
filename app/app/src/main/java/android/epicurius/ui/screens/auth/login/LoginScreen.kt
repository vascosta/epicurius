package android.epicurius.ui.screens.auth.login

import android.epicurius.R
import android.epicurius.ui.navigation.TopBar
import android.epicurius.ui.screens.auth.components.AuthButton
import android.epicurius.ui.screens.auth.components.PasswordTextField
import android.epicurius.ui.screens.theme.Beige
import android.epicurius.ui.screens.utils.TextField
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp

@Composable
fun LoginScreen(
    onSignUp: () -> Unit = {},
    onLogin: (name: String?, email: String?, password: String) -> Unit = { _, _, _ -> },
    onForgotPassword: () -> Unit = {},
    enableButtons: Boolean,
) {
    var name by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }

    Scaffold(
        topBar = {
            TopBar(
                titleText = "Login",
                enableButtons = enableButtons,
                icon = null
            )
        },
        content = { paddingValues ->
            Box(modifier = Modifier.fillMaxSize()) {
                Image(
                    painter = painterResource(id = R.drawable.background_login),
                    contentDescription = null,
                    modifier = Modifier.fillMaxSize(),
                    contentScale = ContentScale.Crop
                )
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(paddingValues)
                        .padding(40.dp),
                    verticalArrangement = Arrangement.Center,
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    TextField(
                        value = name,
                        onValueChange = { name = it },
                        enabled = enableButtons,
                        label = "Name"
                    )
                    TextField(
                        value = email,
                        onValueChange = { email = it },
                        enabled = enableButtons,
                        label = "Email"
                    )
                    PasswordTextField(
                        value = password,
                        onValueChange = { password = it },
                        enabled = enableButtons,
                        label = "Password"
                    )
                    Row {
                        AuthButton(
                            onClick = onSignUp,
                            enabled = enableButtons,
                            text = "SignUp"
                        )
                        AuthButton(
                            onClick = {
                                onLogin(
                                    if (name.isBlank()) null else name,
                                    if (email.isBlank()) null else email,
                                    password
                                )
                            },
                            enabled = enableButtons,
                            text = "Login",
                        )
                    }
                    TextButton(
                        onClick = onForgotPassword,
                        enabled = enableButtons
                    ) {
                        Text(
                            text = "Forgot your password?",
                            color = Beige
                        )
                    }
                }
            }
        },
    )
}

@Preview
@Composable
fun LoginPreview() {
   LoginScreen(enableButtons = true)
}