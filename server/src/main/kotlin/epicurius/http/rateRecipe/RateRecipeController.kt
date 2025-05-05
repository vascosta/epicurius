package epicurius.http.rateRecipe

import epicurius.domain.user.AuthenticatedUser
import epicurius.http.rateRecipe.models.input.RateRecipeInputModel
import epicurius.http.rateRecipe.models.output.GetRecipeRateOutputModel
import epicurius.http.utils.Uris
import epicurius.services.rateRecipe.RateRecipeService
import jakarta.validation.Valid
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping(Uris.PREFIX)
class RateRecipeController(private val rateRecipeService: RateRecipeService) {

    @GetMapping(Uris.RateRecipe.RATE)
    fun getRecipeRate(authenticatedUser: AuthenticatedUser, @PathVariable id: Int): ResponseEntity<*> {
        val rate = rateRecipeService.getRecipeRate(authenticatedUser.user.name, id)
        return ResponseEntity.ok(GetRecipeRateOutputModel(rate))
    }

    @PostMapping(Uris.RateRecipe.RATE)
    fun rateRecipe(
        authenticatedUser: AuthenticatedUser,
        @PathVariable id: Int,
        @Valid @RequestBody body: RateRecipeInputModel
    ): ResponseEntity<*> {
        rateRecipeService.rateRecipe(authenticatedUser.user.id, authenticatedUser.user.name, id, body.rating)
        return ResponseEntity.noContent().build<Unit>()
    }
}
