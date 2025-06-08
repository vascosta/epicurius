package android.epicurius.ui.screens.auth.resetPassword

import android.epicurius.ui.screens.auth.login.LoginActivity
import android.epicurius.ui.screens.utils.navigateTo
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent

class ResetPasswordActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            ResetPasswordScreen(
                onBackButton = { navigateTo<LoginActivity>() },
                onResetPassword = { email, newPassword, confirmPassword ->
                }
            )
        }
    }
}