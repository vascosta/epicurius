package epicurius.http.collection

import epicurius.domain.user.AuthenticatedUser
import epicurius.http.collection.models.input.AddRecipeToCollectionInputModel
import epicurius.http.collection.models.input.CreateCollectionInputModel
import epicurius.http.collection.models.input.UpdateCollectionInputModel
import epicurius.http.collection.models.output.AddRecipeToCollectionOutputModel
import epicurius.http.collection.models.output.CreateCollectionOutputModel
import epicurius.http.collection.models.output.GetCollectionOutputModel
import epicurius.http.collection.models.output.UpdateCollectionOutputModel
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
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping(Uris.PREFIX)
class CollectionController(val collectionService: CollectionService) {

    @GetMapping(Uris.Collection.COLLECTION)
    fun getCollection(
        authenticatedUser: AuthenticatedUser,
        @PathVariable id: Int
    ): ResponseEntity<*> {
        val collection = collectionService.getCollection(authenticatedUser.user.id, authenticatedUser.user.name, id)
        return ResponseEntity.ok(GetCollectionOutputModel(collection))
    }

    @PostMapping(Uris.Collection.COLLECTIONS)
    fun createCollection(
        authenticatedUser: AuthenticatedUser,
        @RequestBody body: CreateCollectionInputModel
    ): ResponseEntity<*> {
        val collection = collectionService.createCollection(authenticatedUser.user.id, body)
        return ResponseEntity.created(collection(collection.id)).body(CreateCollectionOutputModel(collection))
    }

    @PostMapping(Uris.Collection.COLLECTION_RECIPES)
    fun addRecipeToCollection(
        authenticatedUser: AuthenticatedUser,
        @PathVariable id: Int,
        @RequestBody body: AddRecipeToCollectionInputModel
    ): ResponseEntity<*> {
        val updatedCollection = collectionService.addRecipeToCollection(
            authenticatedUser.user.id, authenticatedUser.user.name, id, body.recipeId
        )
        return ResponseEntity.ok().body(AddRecipeToCollectionOutputModel(updatedCollection))
    }

    @PatchMapping(Uris.Collection.COLLECTION)
    fun updateCollection(
        authenticatedUser: AuthenticatedUser,
        @PathVariable id: Int,
        @RequestBody body: UpdateCollectionInputModel
    ): ResponseEntity<*> {
        val updatedCollection = collectionService.updateCollection(authenticatedUser.user.id, id, body)
        return ResponseEntity.ok().body(UpdateCollectionOutputModel(updatedCollection))
    }

    @DeleteMapping(Uris.Collection.COLLECTION_RECIPE)
    fun removeRecipeFromCollection(
        authenticatedUser: AuthenticatedUser,
        @PathVariable id: Int,
        @PathVariable recipeId: Int,
    ): ResponseEntity<*> {
        val updatedCollection = collectionService.removeRecipeFromCollection(authenticatedUser.user.id, id, recipeId)
        return ResponseEntity.ok().body(AddRecipeToCollectionOutputModel(updatedCollection))
    }

    @DeleteMapping(Uris.Collection.COLLECTION)
    fun deleteCollection(authenticatedUser: AuthenticatedUser, @PathVariable id: Int): ResponseEntity<*> {
        collectionService.deleteCollection(authenticatedUser.user.id, id)
        return ResponseEntity.noContent().build<Unit>()
    }
}
