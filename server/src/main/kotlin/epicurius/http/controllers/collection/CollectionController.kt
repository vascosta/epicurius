package epicurius.http.controllers.collection

import epicurius.domain.PagingParams
import epicurius.domain.collection.CollectionType
import epicurius.domain.user.AuthenticatedUser
import epicurius.http.controllers.collection.models.input.AddRecipeToCollectionInputModel
import epicurius.http.controllers.collection.models.input.CreateCollectionInputModel
import epicurius.http.controllers.collection.models.input.UpdateCollectionInputModel
import epicurius.http.controllers.collection.models.output.AddRecipeToCollectionOutputModel
import epicurius.http.controllers.collection.models.output.CreateCollectionOutputModel
import epicurius.http.controllers.collection.models.output.GetCollectionOutputModel
import epicurius.http.controllers.collection.models.output.GetCollectionsOutputModel
import epicurius.http.controllers.collection.models.output.RemoveRecipeFromCollectionOutputModel
import epicurius.http.controllers.collection.models.output.UpdateCollectionOutputModel
import epicurius.http.utils.Uris
import epicurius.http.utils.Uris.Collection.collection
import epicurius.services.collection.CollectionService
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.DeleteMapping
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PatchMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping(Uris.PREFIX)
class CollectionController(private val collectionService: CollectionService) {

    @GetMapping(Uris.Collection.COLLECTION)
    fun getCollection(
        authenticatedUser: AuthenticatedUser,
        @PathVariable id: Int,
    ): ResponseEntity<*> {
        val collection = collectionService.getCollection(authenticatedUser.user.id, id)
        return ResponseEntity
            .ok()
            .body(GetCollectionOutputModel(collection))
    }

    @GetMapping(Uris.Collection.COLLECTIONS)
    fun getCollections(
        authenticatedUser: AuthenticatedUser,
        @RequestParam collectionType: CollectionType,
        @RequestParam skip: Int,
        @RequestParam limit: Int
    ): ResponseEntity<*> {
        val pagingParams = PagingParams(skip, limit)
        val collections = collectionService.getCollections(authenticatedUser.user.id, collectionType, pagingParams)
        return ResponseEntity
            .ok()
            .body(GetCollectionsOutputModel(collections))
    }

    @PostMapping(Uris.Collection.COLLECTIONS)
    fun createCollection(
        authenticatedUser: AuthenticatedUser,
        @RequestBody body: CreateCollectionInputModel,
    ): ResponseEntity<*> {
        val collection = collectionService.createCollection(authenticatedUser.user.id, body)
        return ResponseEntity
            .created(collection(collection.id))
            .body(CreateCollectionOutputModel(collection))
    }

    @PostMapping(Uris.Collection.COLLECTION_RECIPES)
    fun addRecipeToCollection(
        authenticatedUser: AuthenticatedUser,
        @PathVariable id: Int,
        @RequestBody body: AddRecipeToCollectionInputModel,
    ): ResponseEntity<*> {
        val updatedCollection = collectionService.addRecipeToCollection(
            authenticatedUser.user.id, id, body.recipeId
        )
        return ResponseEntity
            .ok()
            .body(AddRecipeToCollectionOutputModel(updatedCollection))
    }

    @PatchMapping(Uris.Collection.COLLECTION)
    fun updateCollection(
        authenticatedUser: AuthenticatedUser,
        @PathVariable id: Int,
        @RequestBody body: UpdateCollectionInputModel,
    ): ResponseEntity<*> {
        val updatedCollection = collectionService.updateCollection(authenticatedUser.user.id, id, body)
        return ResponseEntity
            .ok()
            .body(UpdateCollectionOutputModel(updatedCollection))
    }

    @DeleteMapping(Uris.Collection.COLLECTION_RECIPE)
    fun removeRecipeFromCollection(
        authenticatedUser: AuthenticatedUser,
        @PathVariable id: Int,
        @PathVariable recipeId: Int,
    ): ResponseEntity<*> {
        val updatedCollection = collectionService.removeRecipeFromCollection(authenticatedUser.user.id, id, recipeId)
        return ResponseEntity
            .ok()
            .body(RemoveRecipeFromCollectionOutputModel(updatedCollection))
    }

    @DeleteMapping(Uris.Collection.COLLECTION)
    fun deleteCollection(
        authenticatedUser: AuthenticatedUser,
        @PathVariable id: Int,
    ): ResponseEntity<*> {
        collectionService.deleteCollection(authenticatedUser.user.id, id)
        return ResponseEntity
            .noContent()
            .build<Unit>()
    }
}
