package android.epicurius.ui.screens.auth.login

import android.content.Context
import android.epicurius.services.EpicuriusService
import android.epicurius.services.api.auth.models.input.LoginInputModel
import android.epicurius.storage.Session
import android.epicurius.ui.screens.auth.AuthViewModel
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch

class LoginViewModel(
    service: EpicuriusService,
    session: Session,
    context: Context
): AuthViewModel(service, session, context) {

    var loginEnable by mutableStateOf(true)
        private set

    fun login(
        name: String?,
        email: String?,
        password: String,
        navigateTo: () -> Unit
    ) {
        val loginInfo = validateLoginInfo(name, email, password)
        disableLogin()
        viewModelScope.launch {
            val result = request {
                service.authService.login(loginInfo)
            }
            when {
                result.isFailure -> {
                    enableLogin()
                }
                result.isSuccess -> {
                    val token = result.getTokenOrThrow()
                    saveUserInfo(token)
                    navigateTo()
                }
            }
        }
    }

    fun validateLoginInfo(name: String?, email: String?, password: String): LoginInputModel {
        TODO("Add verifications")
        return LoginInputModel(name, email, password)
    }

    fun enableLogin() {
        loginEnable = true
    }

    fun disableLogin() {
        loginEnable = false
    }
}