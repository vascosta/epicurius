package epicurius.unit.http.collection

import epicurius.domain.exceptions.CollectionNotFound
import epicurius.domain.exceptions.NotTheCollectionOwner
import org.mockito.kotlin.verify
import org.mockito.kotlin.whenever
import org.springframework.http.HttpStatus
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith

class DeleteCollectionControllerTests : CollectionControllerTest() {

    @Test
    fun `Should delete a collection successfully`() {
        // given a collection id (FAVOURITE_COLLECTION_ID)

        // when deleting the collection
        val response = deleteCollection(testPublicAuthenticatedUser, FAVOURITE_COLLECTION_ID)

        // then the collection is deleted successfully
        verify(collectionServiceMock).deleteCollection(testPublicAuthenticatedUser.user.id, FAVOURITE_COLLECTION_ID)
        assertEquals(HttpStatus.NO_CONTENT, response.statusCode)
    }

    @Test
    fun `Should throw CollectionNotFound exception when deleting a non-existing collection`() {
        // given a non-existing collection id
        val nonExistingCollectionId = 1904

        // mock
        whenever(collectionServiceMock.deleteCollection(testPublicAuthenticatedUser.user.id, nonExistingCollectionId))
            .thenThrow(CollectionNotFound())

        // when deleting the collection
        // then the collection is not deleted and throws CollectionNotFound exception
        assertFailsWith<CollectionNotFound> {
            deleteCollection(testPublicAuthenticatedUser, nonExistingCollectionId)
        }
    }

    @Test
    fun `Should throw NotTheCollectionOwner exception when deleting a collection that the user does not own`() {
        // given a collection id (FAVOURITE_COLLECTION_ID)

        // mock
        whenever(collectionServiceMock.deleteCollection(testPrivateAuthenticatedUser.user.id, FAVOURITE_COLLECTION_ID))
            .thenThrow(NotTheCollectionOwner())

        // when deleting the collection
        // then the collection is not deleted and throws NotTheCollectionOwner exception
        assertFailsWith<NotTheCollectionOwner> {
            deleteCollection(testPrivateAuthenticatedUser, FAVOURITE_COLLECTION_ID)
        }
    }
}
