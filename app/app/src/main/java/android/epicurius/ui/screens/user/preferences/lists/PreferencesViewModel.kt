package android.epicurius.ui.screens.user.preferences.lists

import android.content.Context
import android.epicurius.domain.Diet
import android.epicurius.domain.Intolerance
import android.epicurius.services.EpicuriusService
import android.epicurius.services.api.user.models.input.UpdateUserInputModel
import android.epicurius.storage.Session
import android.epicurius.ui.EpicuriusViewModel
import android.epicurius.ui.screens.utils.apiSuccess
import android.epicurius.ui.screens.utils.loading
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch

class PreferencesViewModel(
    service: EpicuriusService,
    session: Session,
    context: Context
): EpicuriusViewModel(service, session, context) {

    fun updatePreferences(
        intolerances: Set<Intolerance>?,
        diets: Set<Diet>?
    ) {
        disableButtons()
        val updateUserInfo = UpdateUserInputModel(null, null, null, null, null, intolerances, diets)
        viewModelScope.launch { handleUpdatePreferences(updateUserInfo) }
    }

    private suspend fun handleUpdatePreferences(updateUserInfo: UpdateUserInputModel) {
        val result = request {
            val token = session.getToken()
            service.userService.updateUser(token, updateUserInfo)
        }
        when {
            result.isSuccess -> {
                val oldUserInfo = session.getUserInfo()
                val newUserInfo = oldUserInfo.copy(
                    name = oldUserInfo.name,
                    email = oldUserInfo.email,
                    country = oldUserInfo.country,
                    privacy = oldUserInfo.privacy,
                    intolerances = updateUserInfo.intolerances?.toList() ?: oldUserInfo.intolerances,
                    diets = updateUserInfo.diets?.toList() ?: oldUserInfo.diets,
                    profilePictureName = oldUserInfo.profilePictureName
                )
                session.updateUserInfo(newUserInfo)
            }
        }
        enableButtons()
    }
}