package epicurius.integration.collection

import epicurius.domain.collection.CollectionType
import epicurius.http.controllers.collection.models.output.AddRecipeToCollectionOutputModel
import epicurius.http.controllers.collection.models.output.CreateCollectionOutputModel
import epicurius.http.controllers.collection.models.output.GetCollectionOutputModel
import epicurius.http.controllers.collection.models.output.RemoveRecipeFromCollectionOutputModel
import epicurius.http.controllers.collection.models.output.UpdateCollectionOutputModel
import epicurius.http.utils.Uris
import epicurius.integration.EpicuriusIntegrationTest
import epicurius.integration.utils.delete
import epicurius.integration.utils.get
import epicurius.integration.utils.getBody
import epicurius.integration.utils.patch
import epicurius.integration.utils.post
import org.springframework.http.HttpStatus

class CollectionIntegrationTest : EpicuriusIntegrationTest() {

    fun getCollection(token: String, id: Int) =
        get<GetCollectionOutputModel>(
            client,
            api(Uris.Collection.COLLECTION.replace("{id}", id.toString())),
            token = token
        )

    fun createCollection(token: String, name: String, type: CollectionType): CreateCollectionOutputModel? {
        val result = post<CreateCollectionOutputModel>(
            client,
            api(Uris.Collection.COLLECTIONS),
            body = mapOf("name" to name, "type" to type),
            responseStatus = HttpStatus.CREATED,
            token = token
        )
        return getBody(result)
    }

    fun addRecipeToCollection(token: String, collectionId: Int, recipeId: Int): AddRecipeToCollectionOutputModel? {
        val result = post<AddRecipeToCollectionOutputModel>(
            client,
            api(Uris.Collection.COLLECTION_RECIPES.replace("{id}", collectionId.toString())),
            mapOf("recipeId" to recipeId),
            HttpStatus.OK,
            token,
        )

        return getBody(result)
    }

    fun updateCollection(token: String, collectionId: Int, name: String): UpdateCollectionOutputModel? {
        val result = patch<UpdateCollectionOutputModel>(
            client,
            api(Uris.Collection.COLLECTION.replace("{id}", collectionId.toString())),
            body = mapOf("name" to name),
            responseStatus = HttpStatus.OK,
            token = token,
        )
        return getBody(result)
    }

    fun removeRecipeFromCollection(token: String, collectionId: Int, recipeId: Int): RemoveRecipeFromCollectionOutputModel? {
        val result = delete<RemoveRecipeFromCollectionOutputModel>(
            client,
            api(
                Uris.Collection.COLLECTION_RECIPE
                    .replace("{id}", collectionId.toString())
                    .replace("{recipeId}", recipeId.toString())
            ),
            HttpStatus.OK,
            token
        )
        return getBody(result)
    }

    fun deleteCollection(token: String, collectionId: Int) =
        delete<Unit>(
            client,
            api(Uris.Collection.COLLECTION.replace("{id}", collectionId.toString())),
            token = token
        )
}
