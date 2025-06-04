package epicurius.integration.menu

import epicurius.http.controllers.menu.models.out.GetDailyMenuOutputModel
import epicurius.http.media.Uris
import epicurius.integration.EpicuriusIntegrationTest
import epicurius.integration.utils.get

class MenuIntegrationTest: EpicuriusIntegrationTest() {

    fun getDailyMenu(token: String) =
        get<GetDailyMenuOutputModel>(
            client,
            api(Uris.Menu.MENU),
            token = token
        )
}