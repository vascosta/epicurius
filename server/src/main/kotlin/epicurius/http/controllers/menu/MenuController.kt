package epicurius.http.controllers.menu

import epicurius.domain.user.AuthenticatedUser
import epicurius.http.controllers.menu.models.out.GetDailyMenuOutputModel
import epicurius.http.pipeline.authentication.AuthenticationRefreshHandler
import epicurius.http.pipeline.authentication.addCookie
import epicurius.http.utils.Uris
import epicurius.services.menu.MenuService
import jakarta.servlet.http.HttpServletResponse
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping(Uris.PREFIX)
class MenuController(
    private val authenticationRefreshHandler: AuthenticationRefreshHandler,
    private val menuService: MenuService
) {

    @GetMapping(Uris.Menu.MENU)
    fun getDailyMenu(
        authenticatedUser: AuthenticatedUser,
        response: HttpServletResponse
    ): ResponseEntity<*> {
        val dailyMenu = menuService.getDailyMenu(authenticatedUser.user.intolerances, authenticatedUser.user.diets)
        return ResponseEntity
            .ok()
            .body(GetDailyMenuOutputModel(dailyMenu))
            .addCookie(response, authenticationRefreshHandler.refreshToken(authenticatedUser.token))
    }
}
