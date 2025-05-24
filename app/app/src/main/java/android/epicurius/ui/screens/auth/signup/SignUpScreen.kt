package android.epicurius.ui.screens.auth.signup

import android.annotation.SuppressLint
import android.epicurius.ui.screens.TopBar
import android.epicurius.ui.screens.auth.utils.PasswordTextField
import android.epicurius.ui.screens.utils.DropdownMenuComponent
import android.epicurius.ui.screens.utils.TextField
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
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import java.util.Locale

@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@Composable
fun SignUpScreen() {
    var username by remember { mutableStateOf("") }
    var email by remember{ mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var confirmPassword by remember { mutableStateOf("") }
    var country by remember { mutableStateOf("") }

    Scaffold(
        topBar = { TopBar(text = "SignUp", icon = null) }
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(Color.Companion.White),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            TextField(value = username, onValueChange = { username = it }, label = "Username")
            TextField(value = email, onValueChange = { email = it }, label = "Email")
            PasswordTextField(value = password, onValueChange = { password = it }, label = "Password")
            PasswordTextField(
                value = confirmPassword,
                onValueChange = { confirmPassword = it },
                label = "Confirm Password"
            )

            val countryCodes = remember { Locale.getISOCountries().sorted() }
            DropdownMenuComponent(
                options = countryCodes,
                value = country,
                onValueChange = { country = it },
                label = "Country",
                modifier = Modifier.padding(5.dp)
            )

            Row {
                SignUpButton("LogIn")
                SignUpButton("SignUp")
            }
        }
    }
}

@Composable
private fun SignUpButton(label: String) {
    Button(
        onClick = {},
        modifier = Modifier.padding(10.dp)
    ) { Text(label) }
}

@Preview
@Composable
fun SignUpPreview() {
    SignUpScreen()
}