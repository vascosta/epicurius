package android.epicurius.ui.screens.auth.resetPassword

import android.content.Context
import android.epicurius.domain.user.validateEmail
import android.epicurius.domain.user.validatePassword
import android.epicurius.services.EpicuriusService
import android.epicurius.services.api.user.models.input.ResetPasswordInputModel
import android.epicurius.storage.Session
import android.epicurius.ui.screens.user.UserViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch

class ResetPasswordViewModel(
    service: EpicuriusService,
    session: Session,
    context: Context
): UserViewModel(service, session, context) {

    fun resetPassword(
        email: String,
        password: String,
        confirmPassword: String
    ) {
        disableButtons()
        if (!validateResetPasswordInfo(email, password, confirmPassword)) {
            enableButtons()
            return
        }
        val resetPasswordInfo = ResetPasswordInputModel(email, password)
        viewModelScope.launch { handleResetPassword(resetPasswordInfo) }
    }

    private suspend fun handleResetPassword(resetPasswordInfo: ResetPasswordInputModel) {
        val result = request {
            service.userService.resetUserPassword(resetPasswordInfo)
        }
        when {
            result.isFailure -> enableButtons()
            result.isSuccess -> {
                showToast("password was reset successfully")
                onSessionExpired(false)
            }
        }
    }

    private fun validateResetPasswordInfo(
        email: String,
        password: String,
        confirmPassword: String
    ): Boolean =
        when {
            password != confirmPassword -> {
                showToast("passwords must be equal")
                false
            }
            !validateEmail(email, ::showToast)
                    || !validatePassword(password, ::showToast)
                    || !validatePassword(confirmPassword, ::showToast) -> false
            else -> true
        }
}