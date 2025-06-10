package android.epicurius.ui.screens.auth.signup

import android.annotation.SuppressLint
import android.epicurius.ui.navigation.TopBar
import android.epicurius.ui.screens.auth.components.AuthButton
import android.epicurius.ui.screens.auth.components.PasswordTextField
import android.epicurius.ui.screens.utils.DropdownMenuComponent
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
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import java.util.Locale

@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@Composable
fun SignUpScreen(
    buttonsEnable: Boolean,
    onSignUp: (
        name: String,
        email: String,
        password: String,
        confirmPassword: String,
        country: String
    ) -> Unit,
    onLogin: () -> Unit
) {
    var username by remember { mutableStateOf("") }
    var email by remember{ mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var confirmPassword by remember { mutableStateOf("") }
    var country by remember { mutableStateOf("") }

    Scaffold(
        topBar = {
            TopBar(
                titleText = "SignUp",
                enableButtons = buttonsEnable,
                icon = null
            )
        },
        content = {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Color.Companion.White),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                TextField(
                    value = username,
                    onValueChange = { username = it },
                    enabled = buttonsEnable,
                    label = "Username"
                )
                TextField(
                    value = email,
                    onValueChange = { email = it },
                    enabled = buttonsEnable,
                    label = "Email"
                )
                PasswordTextField(
                    value = password,
                    onValueChange = { password = it },
                    enabled = buttonsEnable,
                    label = "Password"
                )
                PasswordTextField(
                    value = confirmPassword,
                    onValueChange = { confirmPassword = it },
                    enabled = buttonsEnable,
                    label = "Confirm Password",
                )
                val countryCodes = remember { Locale.getISOCountries().sorted() }
                DropdownMenuComponent(
                    options = countryCodes,
                    value = country,
                    onValueChange = { country = it },
                    label = "Country",
                    modifier = Modifier.padding(5.dp),
                    enabled = buttonsEnable
                )
                Row {
                    AuthButton(
                        text = "Login",
                        onClick = { onLogin() },
                        enabled = buttonsEnable
                    )
                    AuthButton(
                        text = "SignUp",
                        onClick = {
                            onSignUp(
                                username,
                                email,
                                password,
                                confirmPassword,
                                country
                            )
                        },
                        enabled = buttonsEnable
                    )
                }
            }
        }
    )
}



@Preview
@Composable
fun SignUpPreview() {
    SignUpScreen(true, {_, _, _, _, _ -> }, {})
}