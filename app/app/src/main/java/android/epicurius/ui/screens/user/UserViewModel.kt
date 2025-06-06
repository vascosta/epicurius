package android.epicurius.ui.screens.user

import android.content.Context
import android.epicurius.domain.user.EMAIL_LENGTH_MSG
import android.epicurius.domain.user.MAX_EMAIL_LENGTH
import android.epicurius.domain.user.MAX_PASSWORD_LENGTH
import android.epicurius.domain.user.MAX_USERNAME_LENGTH
import android.epicurius.domain.user.MIN_EMAIL_LENGTH
import android.epicurius.domain.user.MIN_PASSWORD_LENGTH
import android.epicurius.domain.user.MIN_USERNAME_LENGTH
import android.epicurius.domain.user.PASSWORD_LENGTH_MSG
import android.epicurius.domain.user.USERNAME_LENGTH_MSG
import android.epicurius.domain.user.UserProfile
import android.epicurius.domain.user.VALID_EMAIL_MSG
import android.epicurius.domain.user.VALID_PASSWORD_MSG
import android.epicurius.domain.user.VALID_USERNAME_MSG
import android.epicurius.domain.user.emailRegex
import android.epicurius.domain.user.passwordRegex
import android.epicurius.domain.user.usernameRegex
import android.epicurius.services.EpicuriusService
import android.epicurius.storage.Session
import android.epicurius.ui.EpicuriusViewModel
import android.epicurius.ui.screens.utils.LoadState
import android.epicurius.ui.screens.utils.idle
import kotlinx.coroutines.flow.MutableStateFlow

open class UserViewModel(
    service: EpicuriusService,
    session: Session,
    context: Context
): EpicuriusViewModel(service, session, context) {

    internal suspend fun saveUserInfo(token: String) {
        val result = request {
            service.userService.getUserInfo(token)
        }
        when {
            result.isSuccess -> {
                session.save(context, token, result.getValueOrThrow().userInfo, null)
            }
        }
    }

    fun validateName(name: String): Boolean {
        if (!name.isBlank() && !name.matches(usernameRegex)) {
            showToast(VALID_USERNAME_MSG)
            return false
        }

        if (name.length !in MIN_USERNAME_LENGTH..MAX_USERNAME_LENGTH) {
            showToast(USERNAME_LENGTH_MSG)
            return false
        }

        return true
    }

    fun validateEmail(email: String): Boolean {
        if (!email.isBlank() && !email.matches(emailRegex)) {
            showToast(VALID_EMAIL_MSG)
            return false
        }
        if (email.length !in MIN_EMAIL_LENGTH..MAX_EMAIL_LENGTH) {
            showToast(EMAIL_LENGTH_MSG)
            return false
        }
        return true
    }

    fun validatePassword(password: String): Boolean {
        if (!password.isBlank() && !password.matches(passwordRegex)) {
            showToast(VALID_PASSWORD_MSG)
            return false
        }
        if (password.length !in MIN_PASSWORD_LENGTH..MAX_PASSWORD_LENGTH) {
            showToast(PASSWORD_LENGTH_MSG)
            return false
        }
        return true
    }
}