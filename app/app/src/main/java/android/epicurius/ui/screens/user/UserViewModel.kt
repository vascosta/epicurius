package android.epicurius.ui.screens.user

import android.content.Context
import android.epicurius.services.EpicuriusService
import android.epicurius.storage.Session
import android.epicurius.ui.EpicuriusViewModel

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
}