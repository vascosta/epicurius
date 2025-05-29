package android.epicurius.storage

import android.content.Context
import android.epicurius.domain.Diet
import android.epicurius.domain.Intolerance
import android.epicurius.domain.user.UserInfo

interface Session {
    suspend fun getToken(): String
    suspend fun getUserInfo(): UserInfo
    suspend fun getUserName(): String
    suspend fun getUserIntolerances(): List<Intolerance>
    suspend fun getUserDiets(): List<Diet>
    suspend fun getUserProfilePicture(context: Context): ByteArray?
    suspend fun isLoggedIn(): Boolean
    suspend fun save(
        context: Context,
        token: String,
        userInfo: UserInfo,
        profilePicture: ByteArray?
    )
    suspend fun updateUserIntolerances(intolerances: List<Intolerance>)
    suspend fun updateUserDiets(diets: List<Diet>)
    suspend fun updateUserProfilePicture(context: Context, profilePicture: ByteArray?)
    suspend fun delete(context: Context)
    suspend fun deleteProfilePicture(context: Context)
}