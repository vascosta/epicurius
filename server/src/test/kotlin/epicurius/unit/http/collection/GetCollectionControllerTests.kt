package epicurius.unit.http.collection

import epicurius.domain.exceptions.CollectionNotFound
import epicurius.http.collection.models.output.GetCollectionOutputModel
import org.mockito.kotlin.whenever
import org.springframework.http.HttpStatus
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith

class GetCollectionControllerTests: CollectionHttpTest() {

    @Test
    fun `Should retrieve a collection successfully`() {
        // given a collection id (FAVOURITE_COLLECTION_ID)

        // mock
        whenever(collectionServiceMock.getCollection(
            testPublicAuthenticatedUser.user.id, testPublicAuthenticatedUser.user.name, FAVOURITE_COLLECTION_ID)
        ).thenReturn(testFavouriteCollection)

        // when retrieving the collection
        val response = getCollection(testPublicAuthenticatedUser, FAVOURITE_COLLECTION_ID)
        val body = response.body as GetCollectionOutputModel

        // then the collection is retrieved successfully
        assertEquals(HttpStatus.OK, response.statusCode)
        assertEquals(testFavouriteCollection, body.collection)
    }

    @Test
    fun `Should throw CollectionNotFound when retrieving a collection that does not exist`() {
        // given a non-existing collection id
        val nonExistingCollectionId = 9999

        // mock
        whenever(collectionServiceMock.getCollection(
            testPublicAuthenticatedUser.user.id, testPublicAuthenticatedUser.user.name, nonExistingCollectionId)
        ).thenThrow(CollectionNotFound())

        // when retrieving the collection
        // then the collection is not found and throws CollectionNotFound exception
        assertFailsWith<CollectionNotFound> { getCollection(testPublicAuthenticatedUser, nonExistingCollectionId) }
    }

    @Test
    fun `Should throw CollectionNotAccessible when retrieving a favourite's collection of another user`() {
        // given a collection id (FAVOURITE_COLLECTION_ID)

        // mock
        whenever(collectionServiceMock.getCollection(
            testPrivateAuthenticatedUser.user.id, testPrivateAuthenticatedUser.user.name, FAVOURITE_COLLECTION_ID)
        ).thenThrow(CollectionNotFound())

        // when retrieving the collection
        // then the collection is not accessible and throws CollectionNotAccessible exception
        assertFailsWith<CollectionNotFound> {
            getCollection(testPrivateAuthenticatedUser, FAVOURITE_COLLECTION_ID)
        }
    }

    @Test
    fun `Should throw CollectionNotAccessible when retrieving a kitchen book's collection of a private user not being followed`() {
        // given a collection id (KITCHEN_BOOK_COLLECTION_ID)

        // mock
        whenever(collectionServiceMock.getCollection(
            testPrivateAuthenticatedUser.user.id, testPrivateAuthenticatedUser.user.name, KITCHEN_BOOK_COLLECTION_ID)
        ).thenThrow(CollectionNotFound())

        // when retrieving the collection
        // then the collection is not accessible and throws CollectionNotAccessible exception
        assertFailsWith<CollectionNotFound> { getCollection(testPrivateAuthenticatedUser, KITCHEN_BOOK_COLLECTION_ID) }
    }
}