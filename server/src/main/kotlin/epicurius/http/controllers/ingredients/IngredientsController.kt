package epicurius.http.controllers.ingredients

import epicurius.domain.user.AuthenticatedUser
import epicurius.http.controllers.ingredients.models.output.GetIngredientsFromPictureOutputModel
import epicurius.http.controllers.ingredients.models.output.GetIngredientsOutputModel
import epicurius.http.controllers.ingredients.models.output.GetSubstituteIngredientsOutputModel
import epicurius.http.pipeline.authentication.AuthenticationRefreshHandler
import epicurius.http.pipeline.authentication.addCookie
import epicurius.http.utils.Uris
import epicurius.services.ingredients.IngredientsService
import jakarta.servlet.http.HttpServletResponse
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
class IngredientsController(
    private val authenticationRefreshHandler: AuthenticationRefreshHandler,
    private val ingredientsService: IngredientsService
) {

    @GetMapping(Uris.Ingredients.INGREDIENTS)
    suspend fun getIngredients(
        authenticatedUser: AuthenticatedUser,
        @RequestParam partial: String,
        response : HttpServletResponse
    ): ResponseEntity<*> {
        val ingredients = ingredientsService.getIngredients(partial)
        return ResponseEntity
            .ok()
            .body(GetIngredientsOutputModel(ingredients))
            .addCookie(response, authenticationRefreshHandler.refreshToken(authenticatedUser.token))
    }

    @GetMapping(Uris.Ingredients.INGREDIENTS_SUBSTITUTES)
    suspend fun getSubstituteIngredients(
        authenticatedUser: AuthenticatedUser,
        @RequestParam name: String,
        response: HttpServletResponse
    ): ResponseEntity<*> {
        val substituteIngredients = ingredientsService.getSubstituteIngredients(name)
        return ResponseEntity
            .ok()
            .body(GetSubstituteIngredientsOutputModel(substituteIngredients))
            .addCookie(response, authenticationRefreshHandler.refreshToken(authenticatedUser.token))
    }

    @PostMapping(Uris.Ingredients.INGREDIENTS, consumes = [MediaType.MULTIPART_FORM_DATA_VALUE])
    suspend fun getIngredientsFromPicture(
        authenticatedUser: AuthenticatedUser,
        @RequestPart("picture") picture: MultipartFile,
        response: HttpServletResponse
    ): ResponseEntity<*> {
        val ingredients = ingredientsService.getIngredientsFromPicture(picture)
        return ResponseEntity
            .ok()
            .body(GetIngredientsFromPictureOutputModel(ingredients))
            .addCookie(response, authenticationRefreshHandler.refreshToken(authenticatedUser.token))
    }
}
