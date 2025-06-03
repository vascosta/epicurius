package android.epicurius.ui.screens.auth.signup

import android.content.Context
import android.epicurius.services.EpicuriusService
import android.epicurius.services.api.auth.models.input.SignUpInputModel
import android.epicurius.storage.Session
import android.epicurius.ui.screens.auth.AuthViewModel
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch

class SignUpViewModel(
    service: EpicuriusService,
    session: Session,
    context: Context
): AuthViewModel(service, session, context) {

    var signUpEnable by mutableStateOf(true)
        private set

    fun signUp(
        name: String,
        email: String,
        password: String,
        confirmPassword: String,
        country: String,
        navigateTo: () -> Unit
    ) {
        val signUpInfo = validateSignUpInfo(name, email, password, confirmPassword, country)
        disableSignUp()
        viewModelScope.launch {
            val result = request {
                service.authService.signUp(signUpInfo)
            }
            when {
                result.isFailure -> {
                    enableSignUp()
                }
                result.isSuccess -> {
                    val token = result.getTokenOrThrow()
                    saveUserInfo(token)
                    navigateTo()
                }
            }
        }
    }

    fun validateSignUpInfo(
        name: String,
        email: String,
        password: String,
        confirmPassword: String,
        country: String
    ): SignUpInputModel {
        // Add verifications
        return SignUpInputModel(name, email, password, confirmPassword, country)
    }

    fun enableSignUp() {
        signUpEnable = true
    }

    fun disableSignUp() {
        signUpEnable = false
    }
}