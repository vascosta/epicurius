package android.epicurius.services.http.api.menu

import android.epicurius.services.http.HttpService
import android.epicurius.services.http.api.menu.models.output.GetDailyMenuOutputModel
import android.epicurius.services.http.utils.APIResult
import android.epicurius.services.http.utils.Uris

class MenuService(private val httpService: HttpService) {

    suspend fun getDailyMenu(
        token: String
    ): APIResult<GetDailyMenuOutputModel> =
        httpService.get<GetDailyMenuOutputModel>(
            Uris.Menu.MENU,
            token = token
        )
}