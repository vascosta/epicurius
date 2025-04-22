package epicurius.http.ingredients

import epicurius.http.recipe.models.output.GetIngredientsFromPictureOutputModel
import epicurius.http.utils.Uris
import epicurius.services.ingredients.IngredientsService
import org.springframework.http.MediaType
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestPart
import org.springframework.web.bind.annotation.RestController
import org.springframework.web.multipart.MultipartFile

@RestController
@RequestMapping(Uris.PREFIX)
class IngredientsController(private val ingredientsService: IngredientsService) {

    @PostMapping(Uris.Ingredient.INGREDIENTS, consumes = [MediaType.MULTIPART_FORM_DATA_VALUE])
    suspend fun getIngredientsFromPicture(
        @RequestPart("picture") picture: MultipartFile,
    ): ResponseEntity<*> {
        val ingredients = ingredientsService.getIngredientsFromPicture(picture)
        return ResponseEntity.ok().body(GetIngredientsFromPictureOutputModel(ingredients))
    }
}
