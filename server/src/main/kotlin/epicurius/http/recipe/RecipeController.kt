package epicurius.http.recipe

import epicurius.domain.user.AuthenticatedUser
import epicurius.http.recipe.models.input.SearchRecipesInputModel
import epicurius.http.recipe.models.output.SearchRecipesOutputModel
import epicurius.http.utils.Uris
import epicurius.services.RecipeService
import jakarta.validation.Valid
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping(Uris.PREFIX)
class RecipeController(private val recipeService: RecipeService) {

    @GetMapping(Uris.Recipe.RECIPES)
    fun searchRecipes(
        authenticatedUser: AuthenticatedUser,
        @RequestParam name: String?,
        @Valid @RequestBody body: SearchRecipesInputModel
    ): ResponseEntity<*> {
        val results = recipeService.searchRecipes(authenticatedUser.user.id, name, body)
        return ResponseEntity.ok().body(SearchRecipesOutputModel(results))
    }
}