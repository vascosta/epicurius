package android.epicurius.storage

import android.content.Context
import android.epicurius.domain.Diet
import android.epicurius.domain.Intolerance
import android.epicurius.domain.user.UserInfo
import android.epicurius.services.api.menu.DailyMenu
import java.time.LocalDate

interface Session {
    suspend fun getToken(): String
    suspend fun getUserInfo(): UserInfo
    suspend fun getUserName(): String
    suspend fun getUserIntolerances(): List<Intolerance>
    suspend fun getUserDiets(): List<Diet>
    suspend fun getUserProfilePicture(context: Context): ByteArray?
    suspend fun getDailyMenu(): DailyMenu?
    suspend fun isLoggedIn(): Boolean
    suspend fun save(
        context: Context,
        token: String,
        userInfo: UserInfo,
        profilePicture: ByteArray?
    )
    suspend fun updateUserInfo(userInfo: UserInfo)
    suspend fun updateUserProfilePicture(context: Context, profilePicture: ByteArray?)
    suspend fun updateDailyMenu(dailyMenu: DailyMenu)
    suspend fun delete(context: Context)
    suspend fun deleteProfilePicture(context: Context)
}