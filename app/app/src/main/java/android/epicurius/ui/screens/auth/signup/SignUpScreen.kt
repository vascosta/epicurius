package android.epicurius.ui.screens.auth.signup

import android.epicurius.R
import android.epicurius.ui.navigation.TopBar
import android.epicurius.ui.screens.auth.components.AuthButton
import android.epicurius.ui.screens.auth.components.PasswordTextField
import android.epicurius.ui.screens.utils.dropdownMenu.DropdownMenuComponent
import android.epicurius.ui.screens.utils.TextField
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
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
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import java.util.Locale

@Composable
fun SignUpScreen(
    onSignUp: (
        name: String,
        email: String,
        password: String,
        confirmPassword: String,
        country: String
    ) -> Unit = { _, _, _, _, _ -> },
    onLogin: () -> Unit = {},
    enableButtons: Boolean
) {
    var name by remember { mutableStateOf("") }
    var email by remember{ mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var confirmPassword by remember { mutableStateOf("") }
    var country by remember { mutableStateOf("") }

    Scaffold(
        topBar = {
            TopBar(
                titleText = "SignUp",
                enableButtons = enableButtons,
                icon = null
            )
        },
        content = { paddingValues ->
            Box(modifier = Modifier.fillMaxSize()) {
                Image(
                    painter = painterResource(id = R.drawable.background_signup),
                    contentDescription = null,
                    modifier = Modifier.fillMaxSize(),
                    contentScale = ContentScale.Crop
                )
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(paddingValues)
                        .padding(40.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
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
                    PasswordTextField(
                        value = confirmPassword,
                        onValueChange = { confirmPassword = it },
                        enabled = enableButtons,
                        label = "Confirm Password",
                    )
                    val countryCodes = remember { Locale.getISOCountries().sorted() }
                    DropdownMenuComponent(
                        options = countryCodes,
                        value = country,
                        onValueChange = { country = it },
                        label = "Country",
                        enabled = enableButtons
                    )
                    Row {
                        AuthButton(
                            text = "Login",
                            onClick = onLogin,
                            enabled = enableButtons
                        )
                        AuthButton(
                            text = "SignUp",
                            onClick = {
                                onSignUp(
                                    name,
                                    email,
                                    password,
                                    confirmPassword,
                                    country
                                )
                            },
                            enabled = enableButtons
                        )
                    }
                }
            }
        },
    )
}



@Preview
@Composable
fun SignUpPreview() {
    SignUpScreen(enableButtons = true)
}