package android.epicurius.ui.screens.auth.signup

import android.content.Context
import android.epicurius.services.EpicuriusService
import android.epicurius.services.api.auth.models.input.SignUpInputModel
import android.epicurius.storage.Session
import android.epicurius.ui.screens.user.UserViewModel
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch

class SignUpViewModel(
    service: EpicuriusService,
    session: Session,
    context: Context
): UserViewModel(service, session, context) {

    fun signUp(
        name: String,
        email: String,
        password: String,
        confirmPassword: String,
        country: String,
        navigateTo: () -> Unit
    ) {
        disableButtons()
        if (!validateSignUpInfo(name, email, password, confirmPassword)) {
            enableButtons()
            return
        }
        val signUpInfo = SignUpInputModel(name, email, password, country)
        viewModelScope.launch {
            handleSignUp(signUpInfo, navigateTo)
        }
    }

    private suspend fun handleSignUp(signUpInfo: SignUpInputModel, navigateTo: () -> Unit) {
        val result = request {
            service.authService.signUp(signUpInfo)
        }
        when {
            result.isFailure -> {
                enableButtons()
            }
            result.isSuccess -> {
                val token = result.getTokenOrThrow()
                saveUserInfo(token)
                navigateTo()
            }
        }
    }

    private fun validateSignUpInfo(
        name: String,
        email: String,
        password: String,
        confirmPassword: String
    ): Boolean =
        when {
            password != confirmPassword -> {
                showToast("passwords must be equal")
                false
            }
            !validateName(name) || !validateEmail(email) || !validatePassword(password) -> false
            else -> true
        }
}