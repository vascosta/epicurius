package epicurius.http.controllers.menu

import epicurius.domain.user.AuthenticatedUser
import epicurius.http.controllers.menu.models.out.GetDailyMenuOutputModel
import epicurius.http.utils.Uris
import epicurius.services.menu.MenuService
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping(Uris.PREFIX)
class MenuController(private val menuService: MenuService) {

    @GetMapping(Uris.Menu.MENU)
    fun getDailyMenu(
        authenticatedUser: AuthenticatedUser,
    ): ResponseEntity<*> {
        val dailyMenu = menuService.getDailyMenu(authenticatedUser.user.intolerances, authenticatedUser.user.diets)
        return ResponseEntity
            .ok()
            .body(GetDailyMenuOutputModel(dailyMenu))
    }
}
