package android.epicurius.ui.screens.collections

import android.content.Context
import android.epicurius.domain.collection.COLLECTION_NAME_LENGTH_MSG
import android.epicurius.domain.collection.MAX_COLLECTION_NAME_LENGTH
import android.epicurius.domain.collection.MIN_COLLECTION_NAME_LENGTH
import android.epicurius.domain.user.USERNAME_LENGTH_MSG
import android.epicurius.services.EpicuriusService
import android.epicurius.storage.Session
import android.epicurius.ui.EpicuriusViewModel

open class CollectionsViewModel(
    service: EpicuriusService,
    session: Session,
    context: Context
): EpicuriusViewModel(service, session, context) {

    fun validateCollectionName(name: String): Boolean = validateName(name)

    private fun validateName(name: String): Boolean {
        if (!name.isBlank() || name.length !in MIN_COLLECTION_NAME_LENGTH..MAX_COLLECTION_NAME_LENGTH) {
            showToast(COLLECTION_NAME_LENGTH_MSG)
            return false
        }
        return true
    }
}