package android.epicurius.ui.screens.auth.login

import android.epicurius.ui.EpicuriusActivity
import android.epicurius.ui.screens.auth.resetPassword.ResetPasswordActivity
import android.epicurius.ui.screens.auth.signup.SignUpActivity
import android.epicurius.ui.screens.feed.FeedActivity
import android.epicurius.ui.navigation.navigateTo
import android.os.Bundle
import androidx.activity.OnBackPressedCallback
import androidx.activity.compose.setContent
import androidx.compose.material3.MaterialTheme

class LoginActivity : EpicuriusActivity() {
    override val viewModel: LoginViewModel by getViewModel<LoginViewModel>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        onBackPressedDispatcher.addCallback(this, object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                finishAffinity()
            }
        })
        setContent {
            MaterialTheme {
                LoginScreen(
                    onSignUp = { navigateTo<SignUpActivity>(useStack = true) },
                    onLogin = { name: String?, email: String?, password: String ->
                        viewModel.login(name, email, password)
                        { navigateTo<FeedActivity>(finishCurrent = true) }
                    },
                    onForgotPassword = { navigateTo<ResetPasswordActivity>(useStack = true) },
                    enableButtons = viewModel.enableButtons
                )
            }
        }
    }
}