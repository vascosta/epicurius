package android.epicurius.services.http.api.collection

import android.epicurius.services.http.HttpService
import android.epicurius.services.http.api.collection.models.input.AddRecipeToCollectionInputModel
import android.epicurius.services.http.api.collection.models.input.CreateCollectionInputModel
import android.epicurius.services.http.api.collection.models.output.AddRecipeToCollectionOutputModel
import android.epicurius.services.http.api.collection.models.output.CreateCollectionOutputModel
import android.epicurius.services.http.api.collection.models.output.GetCollectionOutputModel
import android.epicurius.services.http.api.collection.models.output.GetCollectionsOutputModel
import android.epicurius.services.http.api.collection.models.output.RemoveRecipeFromCollectionOutputModel
import android.epicurius.services.http.api.collection.models.output.UpdateCollectionOutputModel
import android.epicurius.services.http.utils.APIResult
import android.epicurius.services.http.utils.Uris
import epicurius.domain.collection.CollectionType
import epicurius.http.controllers.collection.models.input.UpdateCollectionInputModel

class CollectionService(private val httpService: HttpService) {

    suspend fun getCollection(
        token: String,
        id: Int
    ): APIResult<GetCollectionOutputModel> =
        httpService.get<GetCollectionOutputModel>(
            Uris.Collection.COLLECTION,
            pathParams = mapOf("id" to id),
            token = token
        )

    suspend fun getCollections(
        token: String,
        type: CollectionType,
        skip: Int,
        limit: Int
    ): APIResult<GetCollectionsOutputModel> =
        httpService.get<GetCollectionsOutputModel>(
            Uris.Collection.COLLECTIONS,
            queryParams = mapOf("collectionType" to type, "skip" to skip, "limit" to limit),
            token = token
        )

    suspend fun createCollection(
        token: String,
        createCollectionInfo: CreateCollectionInputModel
    ): APIResult<CreateCollectionOutputModel> =
        httpService.post<CreateCollectionOutputModel>(
            Uris.Collection.COLLECTIONS,
            createCollectionInfo,
            token = token
        )

    suspend fun addRecipeToCollection(
        token: String,
        id: Int,
        addRecipeInfo: AddRecipeToCollectionInputModel
    ): APIResult<AddRecipeToCollectionOutputModel> =
        httpService.post<AddRecipeToCollectionOutputModel>(
            Uris.Collection.COLLECTION_RECIPES,
            addRecipeInfo,
            pathParams = mapOf("id" to id),
            token = token
        )

    suspend fun updateCollection(
        token: String,
        id: Int,
        updateCollectionInfo: UpdateCollectionInputModel
    ): APIResult<UpdateCollectionOutputModel> =
        httpService.patch<UpdateCollectionOutputModel>(
            Uris.Collection.COLLECTION,
            updateCollectionInfo,
            pathParams = mapOf("id" to id),
            token = token
        )

    suspend fun removeRecipeFromCollection(
        token: String,
        id: Int,
        recipeId: Int
    ): APIResult<RemoveRecipeFromCollectionOutputModel> =
        httpService.delete<RemoveRecipeFromCollectionOutputModel>(
            Uris.Collection.COLLECTION_RECIPE,
            pathParams = mapOf("id" to id, "recipeId" to recipeId),
            token = token
        )

    suspend fun deleteCollection(
        token: String,
        id: Int
    ): APIResult<Unit> =
        httpService.delete<Unit>(
            Uris.Collection.COLLECTION,
            pathParams = mapOf("id" to id),
            token = token
        )
}