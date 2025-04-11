package epicurius.http.recipe

import epicurius.domain.Diet
import epicurius.domain.Intolerance
import epicurius.domain.recipe.Cuisine
import epicurius.domain.recipe.MealType
import epicurius.domain.user.AuthenticatedUser
import epicurius.http.recipe.models.input.CreateRecipeInputModel
import epicurius.http.recipe.models.input.SearchRecipesInputModel
import epicurius.http.recipe.models.input.UpdateRecipeInputModel
import epicurius.http.recipe.models.output.CreateRecipeOutputModel
import epicurius.http.recipe.models.output.GetRecipeOutputModel
import epicurius.http.recipe.models.output.SearchRecipesOutputModel
import epicurius.http.utils.Uris
import epicurius.http.utils.Uris.Recipe.recipe
import epicurius.services.RecipeService
import jakarta.validation.Valid
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.DeleteMapping
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PatchMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RequestPart
import org.springframework.web.bind.annotation.RestController
import org.springframework.web.multipart.MultipartFile

@RestController
@RequestMapping(Uris.PREFIX)
class RecipeController(private val recipeService: RecipeService) {

    @GetMapping(Uris.Recipe.RECIPES)
    fun searchRecipes(
        authenticatedUser: AuthenticatedUser,
        @RequestParam name: String?,
        @RequestParam cuisine: Cuisine?,
        @RequestParam mealType: MealType?,
        @RequestParam ingredients: List<String>?,
        @RequestParam intolerances: List<Intolerance>?,
        @RequestParam diets: List<Diet>?,
        @RequestParam minCalories: Int?,
        @RequestParam maxCalories: Int?,
        @RequestParam minCarbs: Int?,
        @RequestParam maxCarbs: Int?,
        @RequestParam minFat: Int?,
        @RequestParam maxFat: Int?,
        @RequestParam minProtein: Int?,
        @RequestParam maxProtein: Int?,
        @RequestParam minTime: Int?,
        @RequestParam maxTime: Int?,
        @RequestParam maxResults: Int = 10
    ): ResponseEntity<*> {
        val searchForm = SearchRecipesInputModel(
            name = name,
            cuisine = cuisine,
            mealType = mealType,
            ingredients = ingredients?.map { it.replace("-", " ") },
            intolerances = intolerances,
            diets = diets,
            minCalories = minCalories,
            maxCalories = maxCalories,
            minCarbs = minCarbs,
            maxCarbs = maxCarbs,
            minFat = minFat,
            maxFat = maxFat,
            minProtein = minProtein,
            maxProtein = maxProtein,
            minTime = minTime,
            maxTime = maxTime,
            maxResults = maxResults
        )
        val results = recipeService.searchRecipes(authenticatedUser.user.id, searchForm)
        return ResponseEntity.ok().body(SearchRecipesOutputModel(results))
    }

    @GetMapping(Uris.Recipe.RECIPE)
    fun getRecipe(authenticatedUser: AuthenticatedUser, @PathVariable id: Int): ResponseEntity<*> {
        val recipe = recipeService.getRecipe(id)
        return ResponseEntity.ok().body(GetRecipeOutputModel(recipe))
    }

    @PostMapping(Uris.Recipe.RECIPES)
    fun createRecipe(
        authenticatedUser: AuthenticatedUser,
        @Valid @RequestBody body: CreateRecipeInputModel,
        @RequestPart("images") pictures: List<MultipartFile>
    ): ResponseEntity<*> {
        val recipe = recipeService.createRecipe(authenticatedUser.user.id, authenticatedUser.user.username, body, pictures)
        return ResponseEntity.created(recipe(recipe.id)).body(CreateRecipeOutputModel(recipe))
    }

    @PatchMapping(Uris.Recipe.RECIPE)
    fun updateRecipe(
        authenticatedUser: AuthenticatedUser,
        @PathVariable id: Int,
        @Valid @RequestBody body: UpdateRecipeInputModel,
    ): ResponseEntity<*> {
        val updatedRecipe = recipeService.updateRecipe(authenticatedUser.user.id, id, body)
        return ResponseEntity.ok().body(updatedRecipe)
    }

    @DeleteMapping(Uris.Recipe.RECIPE)
    fun deleteRecipe(
        authenticatedUser: AuthenticatedUser,
        @PathVariable id: Int
    ): ResponseEntity<*> {
        recipeService.deleteRecipe(authenticatedUser.user.id, id)
        return ResponseEntity.noContent().build<Unit>()
    }
}
