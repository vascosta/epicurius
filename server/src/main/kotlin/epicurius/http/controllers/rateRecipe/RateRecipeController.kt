package epicurius.http.controllers.rateRecipe

import epicurius.domain.user.AuthenticatedUser
import epicurius.http.controllers.rateRecipe.models.input.RateRecipeInputModel
import epicurius.http.controllers.rateRecipe.models.output.GetRecipeRateOutputModel
import epicurius.http.controllers.rateRecipe.models.output.GetUserRecipeRateOutputModel
import epicurius.http.utils.Uris
import epicurius.services.rateRecipe.RateRecipeService
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
class RateRecipeController(private val rateRecipeService: RateRecipeService) {

    @GetMapping(Uris.Recipe.RATE_RECIPE)
    fun getRecipeRate(
        authenticatedUser: AuthenticatedUser,
        @PathVariable id: Int,
    ): ResponseEntity<*> {
        val rate = rateRecipeService.getRecipeRate(authenticatedUser.user.id, id)
        return ResponseEntity
            .ok()
            .body(GetRecipeRateOutputModel(rate))
    }

    @GetMapping(Uris.Recipe.USER_RECIPE_RATE)
    fun getUserRecipeRate(
        authenticatedUser: AuthenticatedUser,
        @PathVariable id: Int
    ): ResponseEntity<*> {
        val rate = rateRecipeService.getUserRecipeRate(authenticatedUser.user.id, id)
        return ResponseEntity
            .ok()
            .body(GetUserRecipeRateOutputModel(rate))
    }

    @PostMapping(Uris.Recipe.RATE_RECIPE)
    fun rateRecipe(
        authenticatedUser: AuthenticatedUser,
        @PathVariable id: Int,
        @Valid @RequestBody body: RateRecipeInputModel,
    ): ResponseEntity<*> {
        rateRecipeService.rateRecipe(authenticatedUser.user.id, id, body.rating)
        return ResponseEntity
            .noContent()
            .build<Unit>()
    }

    @PatchMapping(Uris.Recipe.RATE_RECIPE)
    fun updateRecipeRate(
        authenticatedUser: AuthenticatedUser,
        @PathVariable id: Int,
        @Valid @RequestBody body: RateRecipeInputModel,
    ): ResponseEntity<*> {
        rateRecipeService.updateRecipeRate(authenticatedUser.user.id, id, body.rating)
        return ResponseEntity
            .noContent()
            .build<Unit>()
    }

    @DeleteMapping(Uris.Recipe.RATE_RECIPE)
    fun deleteRecipeRate(
        authenticatedUser: AuthenticatedUser,
        @PathVariable id: Int,
    ): ResponseEntity<*> {
        rateRecipeService.deleteRecipeRate(authenticatedUser.user.id, id)
        return ResponseEntity
            .noContent()
            .build<Unit>()
    }
}
