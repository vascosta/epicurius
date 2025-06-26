package epicurius.http.controllers.rateRecipe

import epicurius.domain.user.AuthenticatedUser
import epicurius.http.controllers.rateRecipe.models.input.RateRecipeInputModel
import epicurius.http.controllers.rateRecipe.models.output.GetRecipeRatingOutputModel
import epicurius.http.controllers.rateRecipe.models.output.GetUserRecipeRatingOutputModel
import epicurius.http.controllers.rateRecipe.models.output.RateRecipeOutputModel
import epicurius.http.controllers.rateRecipe.models.output.UpdateUserRecipeRatingOutputModel
import epicurius.http.media.Uris
import epicurius.http.media.Uris.Recipe.rateRecipe
import epicurius.http.media.createdHttpResponse
import epicurius.http.media.noContentHttpResponse
import epicurius.http.media.okHttpResponse
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
    fun getRecipeRating(
        authenticatedUser: AuthenticatedUser,
        @PathVariable id: Int,
    ): ResponseEntity<*> {
        val rate = rateRecipeService.getRecipeRating(authenticatedUser.user.id, id)
        return okHttpResponse(GetRecipeRatingOutputModel(rate))
    }

    @GetMapping(Uris.Recipe.USER_RECIPE_RATING)
    fun getUserRecipeRating(
        authenticatedUser: AuthenticatedUser,
        @PathVariable id: Int
    ): ResponseEntity<*> {
        val rate = rateRecipeService.getUserRecipeRate(authenticatedUser.user.id, id)
        return okHttpResponse(GetUserRecipeRatingOutputModel(rate))
    }

    @PostMapping(Uris.Recipe.RATE_RECIPE)
    fun rateRecipe(
        authenticatedUser: AuthenticatedUser,
        @PathVariable id: Int,
        @Valid @RequestBody body: RateRecipeInputModel,
    ): ResponseEntity<*> {
        val rate = rateRecipeService.rateRecipe(authenticatedUser.user.id, id, body.rating)
        return createdHttpResponse(rateRecipe(id), RateRecipeOutputModel(rate))
    }

    @PatchMapping(Uris.Recipe.RATE_RECIPE)
    fun updateUserRecipeRate(
        authenticatedUser: AuthenticatedUser,
        @PathVariable id: Int,
        @Valid @RequestBody body: RateRecipeInputModel,
    ): ResponseEntity<*> {
        val rate = rateRecipeService.updateUserRecipeRating(authenticatedUser.user.id, id, body.rating)
        return okHttpResponse(UpdateUserRecipeRatingOutputModel(rate))
    }

    @DeleteMapping(Uris.Recipe.RATE_RECIPE)
    fun deleteUserRecipeRating(
        authenticatedUser: AuthenticatedUser,
        @PathVariable id: Int,
    ): ResponseEntity<*> {
        rateRecipeService.deleteUserRecipeRate(authenticatedUser.user.id, id)
        return noContentHttpResponse()
    }
}
