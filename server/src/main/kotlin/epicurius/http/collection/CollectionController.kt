package epicurius.http.collection

import epicurius.domain.user.AuthenticatedUser
import epicurius.http.collection.models.input.CreateCollectionInputModel
import epicurius.http.collection.models.output.CreateCollectionOutputModel
import epicurius.http.utils.Uris
import epicurius.http.utils.Uris.Collection.collection
import epicurius.services.collection.CollectionService
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping(Uris.PREFIX)
class CollectionController(val collectionService: CollectionService) {

    @PostMapping(Uris.Collection.COLLECTIONS)
    fun createCollection(
        authenticatedUser: AuthenticatedUser,
        @RequestBody body: CreateCollectionInputModel,
    ): ResponseEntity<*> {
        val collection = collectionService.createCollection(authenticatedUser.user.id, body)
        return ResponseEntity.created(collection(collection.id)).body(CreateCollectionOutputModel(collection))
    }
}