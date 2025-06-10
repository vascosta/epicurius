package android.epicurius.ui.screens.auth.resetPassword

import android.epicurius.ui.navigation.TopBar
import android.epicurius.ui.screens.auth.components.PasswordTextField
import android.epicurius.ui.screens.utils.LoadingSpinner
import android.epicurius.ui.screens.utils.TextField
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
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

@Composable
fun ResetPasswordScreen(
    onBackButton: () -> Unit,
    onResetPassword: (email: String, password: String, confirmPassword: String) -> Unit,
    buttonsEnable: Boolean
) {
    var email by rememberSaveable { mutableStateOf("") }
    var newPassword by rememberSaveable { mutableStateOf("") }
    var confirmPassword by rememberSaveable { mutableStateOf("") }

    Scaffold(
        topBar = {
            TopBar(
                titleText = "Reset Password",
                backButton = true,
                onBackButton = onBackButton,
                buttonsEnable,
                icon = null
            )
         },
        content = { paddingValues ->
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
                    .padding(16.dp)
                    .background(Color.White),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                TextField(
                    value = email,
                    onValueChange = { email = it },
                    enabled = buttonsEnable,
                    label = "Email"
                )
                PasswordTextField(
                    value = newPassword,
                    onValueChange = { newPassword = it },
                    enabled = buttonsEnable,
                    label = "New Password"
                )
                PasswordTextField(
                    value = confirmPassword,
                    onValueChange = { confirmPassword = it },
                    enabled = buttonsEnable,
                    label = "Confirm New Password"
                )
                Spacer(Modifier.size(10.dp))
                Button(
                    onClick = { onResetPassword(email, newPassword, confirmPassword) },
                    enabled = buttonsEnable
                ) {
                    if (buttonsEnable) { Text("Reset Password") }
                    else { LoadingSpinner(Modifier.size(30.dp)) }
                }
            }
        },
        containerColor = Color.White
    )
}

@Preview
@Composable
fun ResetPasswordScreenPreview() {
    ResetPasswordScreen({}, { _, _, _ -> }, true)
}