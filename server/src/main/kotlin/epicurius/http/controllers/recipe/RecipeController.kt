package epicurius.http.controllers.recipe

import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import epicurius.domain.Diet
import epicurius.domain.Intolerance
import epicurius.domain.PagingParams
import epicurius.domain.recipe.Cuisine
import epicurius.domain.recipe.MealType
import epicurius.domain.user.AuthenticatedUser
import epicurius.http.controllers.recipe.models.input.CreateRecipeInputModel
import epicurius.http.controllers.recipe.models.input.SearchRecipesInputModel
import epicurius.http.controllers.recipe.models.input.UpdateRecipeInputModel
import epicurius.http.controllers.recipe.models.output.CreateRecipeOutputModel
import epicurius.http.controllers.recipe.models.output.GetRecipeOutputModel
import epicurius.http.controllers.recipe.models.output.SearchRecipesOutputModel
import epicurius.http.controllers.recipe.models.output.UpdateRecipeOutputModel
import epicurius.http.controllers.recipe.models.output.UpdateRecipePicturesOutputModel
import epicurius.http.pipeline.authentication.cookie.addCookie
import epicurius.http.utils.Uris
import epicurius.http.utils.Uris.Recipe.recipe
import epicurius.services.recipe.RecipeService
import jakarta.servlet.http.HttpServletResponse
import jakarta.validation.Valid
import org.springframework.http.MediaType
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

    @GetMapping(Uris.Recipe.RECIPE)
    suspend fun getRecipe(
        authenticatedUser: AuthenticatedUser,
        @PathVariable id: Int,
    ): ResponseEntity<*> {
        val recipe = recipeService.getRecipe(id, authenticatedUser.user.id)
        return ResponseEntity
            .ok()
            .body(GetRecipeOutputModel(recipe))
    }

    @GetMapping(Uris.Recipe.RECIPES)
    fun searchRecipes(
        authenticatedUser: AuthenticatedUser,
        @RequestParam name: String?,
        @RequestParam cuisine: List<Cuisine>?,
        @RequestParam mealType: List<MealType>?,
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
        @RequestParam skip: Int,
        @RequestParam limit: Int,
    ): ResponseEntity<*> {
        val pagingParams = PagingParams(skip, limit)
        val searchForm = SearchRecipesInputModel(
            name = name?.replace("-", " "),
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
            maxTime = maxTime
        )
        val results = recipeService.searchRecipes(authenticatedUser.user.id, searchForm, pagingParams)
        return ResponseEntity
            .ok()
            .body(SearchRecipesOutputModel(results))
    }

    @PostMapping(Uris.Recipe.RECIPES, consumes = [MediaType.MULTIPART_FORM_DATA_VALUE])
    suspend fun createRecipe(
        authenticatedUser: AuthenticatedUser,
        @RequestPart("body") body: String,
        @RequestPart("pictures") pictures: List<MultipartFile>
    ): ResponseEntity<*> {
        val objectMapper = jacksonObjectMapper()
        val recipeInfo = objectMapper.readValue(body, CreateRecipeInputModel::class.java)
        val recipe = recipeService.createRecipe(authenticatedUser.user.id, authenticatedUser.user.name, recipeInfo, pictures.toSet())
        return ResponseEntity
            .created(recipe(recipe.id))
            .body(CreateRecipeOutputModel(recipe))
    }

    @PatchMapping(Uris.Recipe.RECIPE)
    suspend fun updateRecipe(
        authenticatedUser: AuthenticatedUser,
        @PathVariable id: Int,
        @Valid @RequestBody body: UpdateRecipeInputModel,
    ): ResponseEntity<*> {
        val updatedRecipe = recipeService.updateRecipe(authenticatedUser.user.id, id, body)
        return ResponseEntity
            .ok()
            .body(UpdateRecipeOutputModel(updatedRecipe))
    }

    @PatchMapping(Uris.Recipe.RECIPE_PICTURES, consumes = [MediaType.MULTIPART_FORM_DATA_VALUE])
    suspend fun updateRecipePictures(
        authenticatedUser: AuthenticatedUser,
        @PathVariable id: Int,
        @RequestPart("pictures") pictures: List<MultipartFile>,
    ): ResponseEntity<*> {
        val updatedPictures = recipeService.updateRecipePictures(authenticatedUser.user.id, id, pictures.toSet())
        return ResponseEntity
            .ok()
            .body(UpdateRecipePicturesOutputModel(updatedPictures.pictures))
    }

    @DeleteMapping(Uris.Recipe.RECIPE)
    fun deleteRecipe(
        authenticatedUser: AuthenticatedUser,
        @PathVariable id: Int,
    ): ResponseEntity<*> {
        recipeService.deleteRecipe(authenticatedUser.user.id, id)
        return ResponseEntity
            .noContent()
            .build<Unit>()
    }
}
