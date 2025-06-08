package android.epicurius.ui

import android.content.Context
import android.epicurius.R
import android.epicurius.domain.exceptions.AuthenticatedUserNotFound
import android.epicurius.domain.exceptions.InvalidResponseException
import android.epicurius.domain.exceptions.InvalidToken
import android.epicurius.domain.exceptions.MissingUserToken
import android.epicurius.domain.exceptions.UserNotLoggedInException
import android.epicurius.services.EpicuriusService
import android.epicurius.services.http.utils.APIResult
import android.epicurius.services.http.media.Problem
import android.epicurius.storage.Session
import android.epicurius.ui.screens.auth.login.LoginActivity
import android.epicurius.ui.navigation.navigateTo
import android.epicurius.ui.screens.auth.resetPassword.ResetPasswordActivity
import android.epicurius.ui.screens.auth.signup.SignUpActivity
import android.widget.Toast
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch
import java.lang.ref.WeakReference
import java.net.ConnectException
import java.net.SocketTimeoutException
import java.net.UnknownHostException
import kotlin.coroutines.cancellation.CancellationException

open class EpicuriusViewModel(
    val service: EpicuriusService,
    val session: Session,
    context: Context
): ViewModel() {

    private val contextRef: WeakReference<Context> = WeakReference(context)
    val context get() = contextRef.get() ?: error("Activity Context is null")

    var isLoggedIn by mutableStateOf(false)
        protected set

    var buttonsEnable by mutableStateOf(true)
        protected set

    init {
        viewModelScope.launch {
            isLoggedIn = session.isLoggedIn()
            if (
                !isLoggedIn &&
                context::class.java != LoginActivity::class.java &&
                context::class.java != SignUpActivity::class.java &&
                context::class.java != ResetPasswordActivity::class.java
                ) {
                onSessionExpired()
            }
        }
    }

    inline fun <reified T> request(
        showError: Boolean = true,
        handler: () -> APIResult<T>
    ): APIResult<T> {
        return try {
            val result = handler()
            if (result.isFailure) {
                val problem = result.getProblemOrThrow()
                if (unauthorizedMessages.contains(problem.detail)) {
                    onSessionExpired()
                } else if (showError) {
                    showToast(problem.detail)
                }
            }
            result
        } catch (e: Exception) {
            if (e is UserNotLoggedInException) {
                onSessionExpired()
            }
            else {
                val message = when (e) {
                    is ConnectException,
                    is SocketTimeoutException,
                    is UnknownHostException,
                    is InvalidResponseException -> context.getString(R.string.could_not_connect_to_server_msg)
                    is CancellationException -> null
                    else -> context.getString(R.string.something_went_wrong_msg)
                }
                if (message != null) showToast(message)
            }
            APIResult.failure(Problem(detail = e.message ?: context.getString(R.string.something_went_wrong_msg)))
        }
    }

    fun showToast(message: String) {
        Toast.makeText(context, message, Toast.LENGTH_LONG).show()
    }

    fun onSessionExpired(showSessionExpiredMessage: Boolean = true) {
        if (showSessionExpiredMessage) {
            showToast(context.getString(R.string.session_expired_msg))
        }
        viewModelScope.launch { session.delete(context) }
        context.navigateTo<LoginActivity>()
    }

    fun enableButtons() {
        buttonsEnable = true
    }

    fun disableButtons() {
        buttonsEnable = false
    }

    companion object {
        val unauthorizedMessages = listOf(
            MissingUserToken().message,
            AuthenticatedUserNotFound().message,
            InvalidToken().message
        )
    }
}