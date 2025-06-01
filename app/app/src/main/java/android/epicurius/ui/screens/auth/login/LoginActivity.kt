package android.epicurius.ui.screens.auth.login

import android.epicurius.MainActivity
import android.epicurius.ui.screens.auth.signup.SignUpActivity
import android.epicurius.ui.screens.utils.navigateTo
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.material3.MaterialTheme

class LoginActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            MaterialTheme {
                LoginScreen(
                    onBackButton = { navigateTo<MainActivity>() },
                    onSignUp = { navigateTo<SignUpActivity>() },
                )
            }
        }
    }
}