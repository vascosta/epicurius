package android.epicurius.ui

import android.content.Context
import android.epicurius.R
import android.epicurius.domain.exceptions.InvalidResponseException
import android.epicurius.domain.exceptions.UserNotLoggedInException
import android.epicurius.services.EpicuriusService
import android.epicurius.services.http.utils.APIResult
import android.epicurius.services.http.media.Problem
import android.epicurius.storage.Session
import android.epicurius.ui.screens.auth.login.LoginActivity
import android.epicurius.ui.screens.utils.navigateTo
import android.widget.Toast
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

    inline fun <reified T> request(
        showError: Boolean = true,
        handler: () -> APIResult<T>
    ): APIResult<T> {
        return try {
            val result = handler()
            if (result.isFailure) {
                val problem = result.getProblemOrThrow()
                if (problem.title == "Unauthorized") {
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
        Toast.makeText(context, message, Toast.LENGTH_SHORT).show()
    }

    fun onSessionExpired() {
        showToast(context.getString(R.string.session_expired_msg))
        viewModelScope.launch { session.delete(context) }
        context.navigateTo<LoginActivity>()
    }
}