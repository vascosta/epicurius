package android.epicurius.services.api.dailyMenu

import android.epicurius.services.api.dailyMenu.models.output.GetDailyMenuOutputModel
import android.epicurius.services.http.HttpService
import android.epicurius.services.http.utils.APIResult
import android.epicurius.services.http.utils.Uris

class DailyMenuService(private val httpService: HttpService) {

    suspend fun getDailyMenu(
        token: String
    ): APIResult<GetDailyMenuOutputModel> =
        httpService.get<GetDailyMenuOutputModel>(
            Uris.Menu.MENU,
            token = token
        )
}