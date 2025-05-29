package android.epicurius.storage

import android.content.Context
import android.epicurius.domain.Diet
import android.epicurius.domain.Intolerance
import android.epicurius.domain.exceptions.UserNotLoggedInException
import android.epicurius.domain.exceptions.UserProfilePictureNotSaved
import android.epicurius.domain.user.UserInfo
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import com.google.gson.Gson
import kotlinx.coroutines.flow.first
import java.io.File
import java.io.IOException

class SessionDataStore(
    private val store: DataStore<Preferences>,
    private val gson: Gson
) : Session {

    override suspend fun getToken() =
        store.data.first()[TOKEN_KEY] ?: throw UserNotLoggedInException()

    override suspend fun getUserInfo() =
        store.data.first()[USER_INFO_KEY]?.let {
                json -> gson.fromJson(json, UserInfo::class.java)
        } ?: throw UserNotLoggedInException()

    override suspend fun getUserName() = getUserInfo().name

    override suspend fun getUserIntolerances() = getUserInfo().intolerances

    override suspend fun getUserDiets() = getUserInfo().diets

    override suspend fun getUserProfilePicture(context: Context): ByteArray? {
        store.data.first()[USER_PROFILE_PICTURE_NAME_KEY]?.let {
            val file = File(context.filesDir, it)
            return if (file.exists()) file.readBytes() else null
        } ?: return null
    }

    override suspend fun isLoggedIn() = store.data.first()[TOKEN_KEY] != null

    override suspend fun save(
        context: Context,
        token: String,
        userInfo: UserInfo,
        profilePicture: ByteArray?
    ) {
        store.edit {
            it[TOKEN_KEY] = token
            it[USER_INFO_KEY] = gson.toJson(userInfo)

            if (userInfo.profilePictureName != null)
                it[USER_PROFILE_PICTURE_NAME_KEY] = userInfo.profilePictureName
        }

        if (profilePicture != null && userInfo.profilePictureName != null) {
            try {
                val file = File(context.filesDir, userInfo.profilePictureName)
                file.writeBytes(profilePicture)
            } catch (e: IOException) {
                throw UserProfilePictureNotSaved()
            }
        }
    }

    override suspend fun updateUserIntolerances(intolerances: List<Intolerance>) {
        val updatedUser = getUserInfo().copy(intolerances = intolerances)
        store.edit {
            it[USER_INFO_KEY] = gson.toJson(updatedUser)
        }
    }

    override suspend fun updateUserDiets(diets: List<Diet>) {
        val updatedUser = getUserInfo().copy(diets = diets)
        store.edit {
            it[USER_INFO_KEY] = gson.toJson(updatedUser)
        }
    }

    override suspend fun updateUserProfilePicture(context: Context, profilePicture: ByteArray?) {
        if (profilePicture == null)
            deleteProfilePicture(context)
    }


    override suspend fun delete(context: Context) {
        deleteProfilePicture(context)

        store.edit {
            it.remove(TOKEN_KEY)
            it.remove(USER_INFO_KEY)
        }
    }

    override suspend fun deleteProfilePicture(context: Context) {
        store.data.first()[USER_PROFILE_PICTURE_NAME_KEY]?.let {
            val file = File(context.filesDir, it)
            if (file.exists()) file.delete() else null
        }

        store.edit {
            it.remove(USER_PROFILE_PICTURE_NAME_KEY)
        }
    }

    companion object {
        private val TOKEN_KEY = stringPreferencesKey("tokenKey")
        private val USER_INFO_KEY = stringPreferencesKey("userInfoKey")
        private val USER_PROFILE_PICTURE_NAME_KEY = stringPreferencesKey("userProfilePictureNameKey")
    }
}