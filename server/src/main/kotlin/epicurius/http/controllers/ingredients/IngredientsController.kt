package epicurius.http.controllers.ingredients

import epicurius.domain.user.AuthenticatedUser
import epicurius.http.controllers.ingredients.models.output.GetIngredientsOutputModel
import epicurius.http.controllers.ingredients.models.output.GetSubstituteIngredientsOutputModel
import epicurius.http.controllers.ingredients.models.output.IdentifyIngredientsInPictureOutputModel
import epicurius.http.media.Uris
import epicurius.http.media.okHttpResponse
import epicurius.services.ingredients.IngredientsService
import org.springframework.http.MediaType
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RequestPart
import org.springframework.web.bind.annotation.RestController
import org.springframework.web.multipart.MultipartFile

@RestController
@RequestMapping(Uris.PREFIX)
class IngredientsController(private val ingredientsService: IngredientsService) {

    @GetMapping(Uris.Ingredients.INGREDIENTS)
    suspend fun getIngredients(
        authenticatedUser: AuthenticatedUser,
        @RequestParam partial: String,
    ): ResponseEntity<*> {
        val ingredients = ingredientsService.getIngredients(partial)
        return okHttpResponse(GetIngredientsOutputModel(ingredients))
    }

    @GetMapping(Uris.Ingredients.INGREDIENTS_SUBSTITUTES)
    suspend fun getSubstituteIngredients(
        authenticatedUser: AuthenticatedUser,
        @RequestParam name: String,
    ): ResponseEntity<*> {
        val substituteIngredients = ingredientsService.getSubstituteIngredients(name)
        return okHttpResponse(GetSubstituteIngredientsOutputModel(substituteIngredients))
    }

    @PostMapping(Uris.Ingredients.INGREDIENTS, consumes = [MediaType.MULTIPART_FORM_DATA_VALUE])
    suspend fun identifyIngredientsInPicture(
        authenticatedUser: AuthenticatedUser,
        @RequestPart("picture") picture: MultipartFile,
    ): ResponseEntity<*> {
        val ingredients = ingredientsService.identifyIngredientsInPicture(picture)
        return okHttpResponse(IdentifyIngredientsInPictureOutputModel(ingredients))
    }
}
