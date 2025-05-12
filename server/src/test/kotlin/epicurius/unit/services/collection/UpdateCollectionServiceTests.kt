package epicurius.unit.services.collection

import epicurius.domain.exceptions.CollectionNotFound
import epicurius.domain.exceptions.NotTheCollectionOwner
import epicurius.http.controllers.collection.models.input.UpdateCollectionInputModel
import org.mockito.kotlin.whenever
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith

class UpdateCollectionServiceTests : CollectionServiceTest() {

    private val updateCollectionInputInfo = UpdateCollectionInputModel("New Name")

    @Test
    fun `Should update a collection successfully`() {
        // given a collection id (FAVOURITE_COLLECTION_ID) and new information for updating it (updateCollectionInputInfo)

        // mock
        val mockUpdatedCollection = testFavouriteJdbiCollectionModel.copy(name = updateCollectionInputInfo.name!!)
        whenever(jdbiCollectionRepositoryMock.getCollectionById(FAVOURITE_COLLECTION_ID)).thenReturn(testFavouriteJdbiCollectionModel)
        whenever(
            jdbiCollectionRepositoryMock.checkIfUserIsCollectionOwner(FAVOURITE_COLLECTION_ID, testPublicUser.id)
        ).thenReturn(true)
        whenever(
            jdbiCollectionRepositoryMock.updateCollection(FAVOURITE_COLLECTION_ID, updateCollectionInputInfo.name)
        ).thenReturn(mockUpdatedCollection)

        // when updating the collection
        val updatedCollection = updateCollection(testPublicUser.id, FAVOURITE_COLLECTION_ID, updateCollectionInputInfo)

        // then the collection is updated successfully
        assertEquals(testFavouriteCollection.copy(name = mockUpdatedCollection.name), updatedCollection)
    }

    @Test
    fun `Should throw CollectionNotFound exception when updating a non-existing collection`() {
        // given a non-existing collection id
        val nonExistingCollectionId = 1904

        // mock
        whenever(jdbiCollectionRepositoryMock.getCollectionById(nonExistingCollectionId)).thenReturn(null)

        // when updating the collection
        // then the collection is not updated and throws CollectionNotFound exception
        assertFailsWith<CollectionNotFound> {
            updateCollection(testPublicUser.id, nonExistingCollectionId, updateCollectionInputInfo)
        }
    }

    @Test
    fun `Should throw NotTheCollectionOwner exception when updating a collection that the user does not own`() {
        // given a collection id (FAVOURITE_COLLECTION_ID) and new information for updating it (updateCollectionInputInfo)

        // mock
        whenever(jdbiCollectionRepositoryMock.getCollectionById(FAVOURITE_COLLECTION_ID)).thenReturn(testFavouriteJdbiCollectionModel)
        whenever(
            jdbiCollectionRepositoryMock.checkIfUserIsCollectionOwner(FAVOURITE_COLLECTION_ID, testPrivateUser.id)
        ).thenReturn(false)

        // when updating the collection
        // then the collection is not owned by the user and throws NotTheOwnerOfCollection exception
        assertFailsWith<NotTheCollectionOwner> {
            updateCollection(testPrivateUser.id, FAVOURITE_COLLECTION_ID, updateCollectionInputInfo)
        }
    }
}
