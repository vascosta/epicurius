package epicurius.unit.http.collection

import epicurius.domain.exceptions.CollectionNotFound
import epicurius.domain.exceptions.NotTheCollectionOwner
import epicurius.http.collection.models.input.UpdateCollectionInputModel
import epicurius.http.collection.models.output.UpdateCollectionOutputModel
import org.mockito.kotlin.whenever
import org.springframework.http.HttpStatus
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith

class UpdateCollectionControllerTests: CollectionHttpTest() {

    private val updateCollectionInputInfo = UpdateCollectionInputModel("New Name")

    @Test
    fun `Should update a collection successfully`() {
        // given a collection id (FAVOURITE_COLLECTION_ID) and new information for updating it (updateCollectionInputInfo)

        // mock
        whenever(collectionServiceMock
            .updateCollection(testPublicAuthenticatedUser.user.id, FAVOURITE_COLLECTION_ID, updateCollectionInputInfo)
        ).thenReturn(testFavouriteCollection.copy(name = updateCollectionInputInfo.name!!))

        // when updating the collection
        val response = collectionController.updateCollection(
            testPublicAuthenticatedUser, FAVOURITE_COLLECTION_ID, updateCollectionInputInfo
        )
        val body = response.body as UpdateCollectionOutputModel

        // then the collection is updated successfully
        assertEquals(HttpStatus.OK, response.statusCode)
        assertEquals(testFavouriteCollection.copy(name = updateCollectionInputInfo.name!!), body.collection)
    }

    @Test
    fun `Should throw CollectionNotFound exception when updating a non-existing collection`() {
        // given a non-existing collection id
        val nonExistingCollectionId = 1904

        // mock
        whenever(collectionServiceMock
            .updateCollection(testPublicAuthenticatedUser.user.id, nonExistingCollectionId, updateCollectionInputInfo)
        ).thenThrow(CollectionNotFound())

        // when updating the collection
        // then the collection is not updated and throws CollectionNotFound exception
        assertFailsWith<CollectionNotFound> {
            collectionController.updateCollection(
                testPublicAuthenticatedUser, nonExistingCollectionId, updateCollectionInputInfo
            )
        }
    }

    @Test
    fun `Should throw NotTheCollectionOwner exception when updating a collection that the user does not own`() {
        // given a collection id (FAVOURITE_COLLECTION_ID) and new information for updating it (updateCollectionInputInfo)

        // mock
        whenever(collectionServiceMock
            .updateCollection(testPrivateAuthenticatedUser.user.id, FAVOURITE_COLLECTION_ID, updateCollectionInputInfo)
        ).thenThrow(NotTheCollectionOwner())

        // when updating the collection
        // then the collection is not owned by the user and throws NotTheOwnerOfCollection exception
        assertFailsWith<NotTheCollectionOwner> {
            collectionController.updateCollection(
                testPrivateAuthenticatedUser, FAVOURITE_COLLECTION_ID, updateCollectionInputInfo
            )
        }
    }
}