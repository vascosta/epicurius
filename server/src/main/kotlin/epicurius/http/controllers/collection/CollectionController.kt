package epicurius.http.controllers.collection

import epicurius.domain.user.AuthenticatedUser
import epicurius.http.controllers.collection.models.input.AddRecipeToCollectionInputModel
import epicurius.http.controllers.collection.models.input.CreateCollectionInputModel
import epicurius.http.controllers.collection.models.input.UpdateCollectionInputModel
import epicurius.http.controllers.collection.models.output.AddRecipeToCollectionOutputModel
import epicurius.http.controllers.collection.models.output.CreateCollectionOutputModel
import epicurius.http.controllers.collection.models.output.GetCollectionOutputModel
import epicurius.http.controllers.collection.models.output.RemoveRecipeFromCollectionOutputModel
import epicurius.http.controllers.collection.models.output.UpdateCollectionOutputModel
import epicurius.http.pipeline.authentication.AuthenticationRefreshHandler
import epicurius.http.pipeline.authentication.addCookie
import epicurius.http.utils.Uris
import epicurius.http.utils.Uris.Collection.collection
import epicurius.services.collection.CollectionService
import jakarta.servlet.http.HttpServletResponse
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
class CollectionController(
    private val authenticationRefreshHandler: AuthenticationRefreshHandler,
    private val collectionService: CollectionService
) {

    @GetMapping(Uris.Collection.COLLECTION)
    fun getCollection(
        authenticatedUser: AuthenticatedUser,
        @PathVariable id: Int,
        response: HttpServletResponse
    ): ResponseEntity<*> {
        val collection = collectionService.getCollection(authenticatedUser.user.id, authenticatedUser.user.name, id)
        return ResponseEntity
            .ok()
            .body(GetCollectionOutputModel(collection))
            .addCookie(response, authenticationRefreshHandler.refreshToken(authenticatedUser.token))
    }

    @PostMapping(Uris.Collection.COLLECTIONS)
    fun createCollection(
        authenticatedUser: AuthenticatedUser,
        @RequestBody body: CreateCollectionInputModel,
        response: HttpServletResponse
    ): ResponseEntity<*> {
        val collection = collectionService.createCollection(authenticatedUser.user.id, body)
        return ResponseEntity
            .created(collection(collection.id))
            .body(CreateCollectionOutputModel(collection))
            .addCookie(response, authenticationRefreshHandler.refreshToken(authenticatedUser.token))
    }

    @PostMapping(Uris.Collection.COLLECTION_RECIPES)
    fun addRecipeToCollection(
        authenticatedUser: AuthenticatedUser,
        @PathVariable id: Int,
        @RequestBody body: AddRecipeToCollectionInputModel,
        response: HttpServletResponse
    ): ResponseEntity<*> {
        val updatedCollection = collectionService.addRecipeToCollection(
            authenticatedUser.user.id, authenticatedUser.user.name, id, body.recipeId
        )
        return ResponseEntity
            .ok()
            .body(AddRecipeToCollectionOutputModel(updatedCollection))
            .addCookie(response, authenticationRefreshHandler.refreshToken(authenticatedUser.token))
    }

    @PatchMapping(Uris.Collection.COLLECTION)
    fun updateCollection(
        authenticatedUser: AuthenticatedUser,
        @PathVariable id: Int,
        @RequestBody body: UpdateCollectionInputModel,
        response: HttpServletResponse
    ): ResponseEntity<*> {
        val updatedCollection = collectionService.updateCollection(authenticatedUser.user.id, id, body)
        return ResponseEntity
            .ok()
            .body(UpdateCollectionOutputModel(updatedCollection))
            .addCookie(response, authenticationRefreshHandler.refreshToken(authenticatedUser.token))
    }

    @DeleteMapping(Uris.Collection.COLLECTION_RECIPE)
    fun removeRecipeFromCollection(
        authenticatedUser: AuthenticatedUser,
        @PathVariable id: Int,
        @PathVariable recipeId: Int,
        response: HttpServletResponse
    ): ResponseEntity<*> {
        val updatedCollection = collectionService.removeRecipeFromCollection(authenticatedUser.user.id, id, recipeId)
        return ResponseEntity
            .ok()
            .body(RemoveRecipeFromCollectionOutputModel(updatedCollection))
            .addCookie(response, authenticationRefreshHandler.refreshToken(authenticatedUser.token))
    }

    @DeleteMapping(Uris.Collection.COLLECTION)
    fun deleteCollection(
        authenticatedUser: AuthenticatedUser,
        @PathVariable id: Int,
        response: HttpServletResponse
    ): ResponseEntity<*> {
        collectionService.deleteCollection(authenticatedUser.user.id, id)
        return ResponseEntity
            .noContent()
            .build<Unit>()
            .addCookie(response, authenticationRefreshHandler.refreshToken(authenticatedUser.token))
    }
}
