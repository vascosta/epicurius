package epicurius.unit.services.collection

import epicurius.domain.exceptions.CollectionNotFound
import epicurius.domain.exceptions.NotTheCollectionOwner
import org.mockito.kotlin.verify
import org.mockito.kotlin.whenever
import kotlin.test.Test
import kotlin.test.assertFailsWith

class DeleteCollectionServiceTests : CollectionServiceTest() {

    @Test
    fun `Should delete a collection successfully`() {
        // given a collection id (FAVOURITE_COLLECTION_ID)

        // mock
        whenever(jdbiCollectionRepositoryMock.getCollectionById(FAVOURITE_COLLECTION_ID))
            .thenReturn(testFavouriteJdbiCollectionModel)
        whenever(jdbiCollectionRepositoryMock.checkIfUserIsCollectionOwner(FAVOURITE_COLLECTION_ID, PUBLIC_USER_ID))
            .thenReturn(true)

        // when deleting the collection
        deleteCollection(testPublicUser.id, FAVOURITE_COLLECTION_ID)

        // then the collection is deleted successfully
        verify(jdbiCollectionRepositoryMock).deleteCollection(FAVOURITE_COLLECTION_ID)
    }

    @Test
    fun `Should throw CollectionNotFound exception when deleting a non-existing collection`() {
        // given a non-existing collection id
        val nonExistingCollectionId = 1904

        // mock
        whenever(jdbiCollectionRepositoryMock.getCollectionById(nonExistingCollectionId)).thenReturn(null)

        // when deleting the collection
        // then the collection is not deleted and throws CollectionNotFound exception
        assertFailsWith<CollectionNotFound> { deleteCollection(testPublicUser.id, nonExistingCollectionId) }
    }

    @Test
    fun `Should throw NotTheCollectionOwner exception when deleting a collection that the user does not own`() {
        // given a collection id (FAVOURITE_COLLECTION_ID)

        // mock
        whenever(jdbiCollectionRepositoryMock.getCollectionById(FAVOURITE_COLLECTION_ID)).thenReturn(testFavouriteJdbiCollectionModel)
        whenever(jdbiCollectionRepositoryMock.checkIfUserIsCollectionOwner(FAVOURITE_COLLECTION_ID, PRIVATE_USER_ID))
            .thenReturn(false)

        // when deleting the collection
        // then the collection is not deleted and throws NotTheCollectionOwner exception
        assertFailsWith<NotTheCollectionOwner> { deleteCollection(testPrivateUser.id, FAVOURITE_COLLECTION_ID) }
    }
}
