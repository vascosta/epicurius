package epicurius.http.controllers.rateRecipe

import epicurius.domain.user.AuthenticatedUser
import epicurius.http.controllers.rateRecipe.models.input.RateRecipeInputModel
import epicurius.http.controllers.rateRecipe.models.output.GetRecipeRateOutputModel
import epicurius.http.pipeline.authentication.AuthenticationRefreshHandler
import epicurius.http.pipeline.authentication.cookie.addCookie
import epicurius.http.utils.Uris
import epicurius.services.rateRecipe.RateRecipeService
import jakarta.servlet.http.HttpServletResponse
import jakarta.validation.Valid
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.DeleteMapping
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PatchMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping(Uris.PREFIX)
class RateRecipeController(
    private val authenticationRefreshHandler: AuthenticationRefreshHandler,
    private val rateRecipeService: RateRecipeService
) {

    @GetMapping(Uris.Recipe.RECIPE_RATE)
    fun getRecipeRate(
        authenticatedUser: AuthenticatedUser,
        @PathVariable id: Int,
        response: HttpServletResponse
    ): ResponseEntity<*> {
        val rate = rateRecipeService.getRecipeRate(authenticatedUser.user.name, id)
        return ResponseEntity
            .ok()
            .body(GetRecipeRateOutputModel(rate))
            .addCookie(response, authenticationRefreshHandler.refreshToken(authenticatedUser.token))
    }

    @PostMapping(Uris.Recipe.RECIPE_RATE)
    fun rateRecipe(
        authenticatedUser: AuthenticatedUser,
        @PathVariable id: Int,
        @Valid @RequestBody body: RateRecipeInputModel,
        response: HttpServletResponse
    ): ResponseEntity<*> {
        rateRecipeService.rateRecipe(authenticatedUser.user.id, authenticatedUser.user.name, id, body.rating)
        return ResponseEntity
            .noContent()
            .build<Unit>()
            .addCookie(response, authenticationRefreshHandler.refreshToken(authenticatedUser.token))
    }

    @PatchMapping(Uris.Recipe.RECIPE_RATE)
    fun updateRecipeRate(
        authenticatedUser: AuthenticatedUser,
        @PathVariable id: Int,
        @Valid @RequestBody body: RateRecipeInputModel,
        response: HttpServletResponse
    ): ResponseEntity<*> {
        rateRecipeService.updateRecipeRate(authenticatedUser.user.id, authenticatedUser.user.name, id, body.rating)
        return ResponseEntity
            .noContent()
            .build<Unit>()
            .addCookie(response, authenticationRefreshHandler.refreshToken(authenticatedUser.token))
    }

    @DeleteMapping(Uris.Recipe.RECIPE_RATE)
    fun deleteRecipeRate(
        authenticatedUser: AuthenticatedUser,
        @PathVariable id: Int,
        response: HttpServletResponse
    ): ResponseEntity<*> {
        rateRecipeService.deleteRecipeRate(authenticatedUser.user.id, authenticatedUser.user.name, id)
        return ResponseEntity
            .noContent()
            .build<Unit>()
            .addCookie(response, authenticationRefreshHandler.refreshToken(authenticatedUser.token))
    }
}
