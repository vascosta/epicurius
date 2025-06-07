package android.epicurius.ui.screens.auth.signup

import android.epicurius.ui.EpicuriusActivity
import android.epicurius.ui.screens.auth.login.LoginActivity
import android.epicurius.ui.screens.feed.FeedActivity
import android.epicurius.ui.screens.utils.navigateTo
import android.os.Bundle
import androidx.activity.OnBackPressedCallback
import androidx.activity.compose.setContent
import androidx.compose.material3.MaterialTheme

class SignUpActivity : EpicuriusActivity() {
    val viewmodel: SignUpViewModel by getViewModel<SignUpViewModel>()

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
                    signUpEnable = viewmodel.signUpEnable,
                    onSignUp = { name, email, password, confirmPassword, country ->
                        viewmodel.signUp(name, email, password, confirmPassword, country) {
                            navigateTo<FeedActivity>() // TODO: change to activity to change intolerances and diets
                        }
                    },
                    onLogin = { navigateTo<LoginActivity>() },
                )
            }
        }
    }
}