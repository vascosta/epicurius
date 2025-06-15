package android.epicurius.ui.screens.auth.signup

import android.epicurius.ui.EpicuriusActivity
import android.epicurius.ui.screens.auth.login.LoginActivity
import android.epicurius.ui.screens.feed.FeedActivity
import android.epicurius.ui.navigation.navigateTo
import android.epicurius.ui.screens.user.preferences.skipable.KnowMoreActivity
import android.os.Bundle
import androidx.activity.OnBackPressedCallback
import androidx.activity.compose.setContent
import androidx.compose.material3.MaterialTheme

class SignUpActivity : EpicuriusActivity() {
    override val viewModel: SignUpViewModel by getViewModel<SignUpViewModel>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        onBackPressedDispatcher.addCallback(this, object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                finishAffinity()
            }
        })
        setContent {
            MaterialTheme {
                SignUpScreen(
                    buttonsEnable = viewModel.enableButtons,
                    onSignUp = {
                        name: String,
                        email: String,
                        password: String,
                        confirmPassword: String,
                        country: String ->
                            viewModel.signUp(name, email, password, confirmPassword, country) {
                                navigateTo<KnowMoreActivity>(finishCurrent = true)
                        }
                    },
                    onLogin = { navigateTo<LoginActivity>() },
                )
            }
        }
    }
}