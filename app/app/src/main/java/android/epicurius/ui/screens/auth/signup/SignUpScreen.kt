package android.epicurius.ui.screens.auth.signup

import android.annotation.SuppressLint
import android.epicurius.ui.screens.TopBar
import android.epicurius.ui.screens.auth.utils.PasswordTextField
import android.epicurius.ui.screens.utils.TextField
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.MenuAnchorType
import androidx.compose.material3.OutlinedTextField
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
    var username by rememberSaveable { mutableStateOf("") }
    var email by rememberSaveable { mutableStateOf("") }
    var password by rememberSaveable { mutableStateOf("") }
    var confirmPassword by rememberSaveable { mutableStateOf("") }

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
            SelectCountry()

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

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun SelectCountry() {
    val countryCodes = remember {
        Locale.getISOCountries().sorted()
    }

    var expanded by remember { mutableStateOf(false) }
    var selectedCode by remember { mutableStateOf("") }

    ExposedDropdownMenuBox(
        expanded = expanded,
        onExpandedChange = { expanded = !expanded },
        modifier = Modifier
            .padding(5.dp)
            .verticalScroll(rememberScrollState())
    ) {
        OutlinedTextField(
            value = selectedCode,
            onValueChange = { selectedCode = it },
            label = { Text("Country") },
            readOnly = true,
            trailingIcon = {
                ExposedDropdownMenuDefaults.TrailingIcon(expanded)
            },
            modifier = Modifier.menuAnchor(MenuAnchorType.PrimaryNotEditable, enabled = true)
        )

        DropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false }
        ) {
            countryCodes.forEach { code ->
                DropdownMenuItem(
                    text = { Text(code) },
                    onClick = {
                        selectedCode = code
                        expanded = false
                    }
                )
            }
        }
    }
}

@Preview
@Composable
fun SignUpPreview() {
    SignUpScreen()
}