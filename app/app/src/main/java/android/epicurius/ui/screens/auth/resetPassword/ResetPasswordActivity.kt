package android.epicurius.ui.screens.auth.resetPassword

import android.epicurius.ui.EpicuriusActivity
import android.epicurius.ui.screens.auth.login.LoginActivity
import android.epicurius.ui.navigation.navigateTo
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent

class ResetPasswordActivity : EpicuriusActivity() {
    override val viewModel: ResetPasswordViewModel by getViewModel<ResetPasswordViewModel>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            ResetPasswordScreen(
                onBackButton = { navigateTo<LoginActivity>(true) },
                onResetPassword = { email: String, password: String, confirmPassword: String ->
                    viewModel.resetPassword(email, password, confirmPassword)
                },
                buttonsEnable = viewModel.buttonsEnable
            )
        }
    }
}