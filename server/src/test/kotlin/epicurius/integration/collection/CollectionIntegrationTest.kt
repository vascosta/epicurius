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
import epicurius.integration.utils.patch
import epicurius.integration.utils.post

class CollectionIntegrationTest: EpicuriusIntegrationTest() {

    fun getCollection(token: String, id: Int) =
        get<GetCollectionOutputModel>(
            client,
            api(Uris.Collection.COLLECTION.replace("{id}", id.toString())),
            token = token
        )

    fun createCollection(token: String, name: String, type: CollectionType) =
        post<CreateCollectionOutputModel>(
            client,
            api(Uris.Collection.COLLECTIONS),
            token = token,
            body = mapOf("name" to name, "type" to type)
        )

    fun addRecipeToCollection(token: String, collectionId: Int, recipeId: Int) =
        post<AddRecipeToCollectionOutputModel>(
            client,
            api(Uris.Collection.COLLECTION_RECIPES.replace("{id}", collectionId.toString())),
            token = token,
            body = mapOf("recipeId" to recipeId)
        )

    fun updateCollection(token: String, collectionId: Int, name: String) =
        patch<UpdateCollectionOutputModel>(
            client,
            api(Uris.Collection.COLLECTION.replace("{id}", collectionId.toString())),
            token = token,
            body = mapOf("name" to name)
        )

    fun removeRecipeFromCollection(token: String, collectionId: Int, recipeId: Int) =
        delete<RemoveRecipeFromCollectionOutputModel>(
            client,
            api(Uris.Collection.COLLECTION_RECIPE
                .replace("{id}", collectionId.toString())
                .replace("{recipeId}", recipeId.toString())
            ),
            token = token
        )

    fun deleteCollection(token: String, collectionId: Int) =
        delete<Unit>(
            client,
            api(Uris.Collection.COLLECTION.replace("{id}", collectionId.toString())),
            token = token
        )
}