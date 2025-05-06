package epicurius.unit.services.collection

import epicurius.domain.exceptions.CollectionNotAccessible
import epicurius.domain.exceptions.CollectionNotFound
import org.mockito.kotlin.whenever
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith

class GetCollectionServiceTests: CollectionServiceTest() {

    @Test
    fun `Should retrieve a collection successfully`() {
        // given a collection id (FAVOURITE_COLLECTION_ID)

        // mock
        whenever(jdbiCollectionRepositoryMock.checkIfUserIsCollectionOwner(FAVOURITE_COLLECTION_ID, testPublicUser.id)).thenReturn(true)
        whenever(jdbiCollectionRepositoryMock.getCollectionById(FAVOURITE_COLLECTION_ID)).thenReturn(testFavouriteJdbiCollectionModel)

        // when retrieving the collection
        val collection = getCollection(PUBLIC_USER_ID, testPublicUsername, FAVOURITE_COLLECTION_ID)

        // then the collection is retrieved successfully
        assertEquals(testFavouriteCollection, collection)
    }

    @Test
    fun `Should throw CollectionNotFound when retrieving a collection that does not exist`() {
        // given a collection id (FAVOURITE_COLLECTION_ID)

        // mock
        whenever(jdbiCollectionRepositoryMock.getCollectionById(FAVOURITE_COLLECTION_ID)).thenReturn(null)

        // when retrieving the collection
        // then the collection is not found and throws CollectionNotFound exception
        assertFailsWith<CollectionNotFound> {
            getCollection(PUBLIC_USER_ID, testPublicUsername, FAVOURITE_COLLECTION_ID)
        }
    }

    @Test
    fun `Should throw CollectionNotAccessible when retrieving a favourite's collection of another user`() {
        // given a collection id (FAVOURITE_COLLECTION_ID)

        // mock
        whenever(jdbiCollectionRepositoryMock.getCollectionById(FAVOURITE_COLLECTION_ID)).thenReturn(testFavouriteJdbiCollectionModel)

        // when retrieving the collection
        // then the collection is not accessible and throws CollectionNotAccessible exception
        assertFailsWith<CollectionNotAccessible> {
            getCollection(PRIVATE_USER_ID, testPrivateUsername, FAVOURITE_COLLECTION_ID)
        }
    }

    @Test
    fun `Should throw CollectionNotAccessible when retrieving a kitchen book's collection of a private user not being followed`() {
        // given a collection id (KITCHEN_BOOK_COLLECTION_ID)

        // mock
        whenever(jdbiCollectionRepositoryMock.getCollectionById(testKitchenBookCollection.id))
            .thenReturn(testKitchenBookJdbiCollectionModel)
        whenever(jdbiUserRepositoryMock.getUserById(PUBLIC_USER_ID)).thenReturn(testPublicUser)
        whenever(jdbiUserRepositoryMock.checkUserVisibility(testPublicUsername, testPrivateUsername))
            .thenReturn(false)

        // when retrieving the collection
        // then the collection is not accessible and throws CollectionNotAccessible exception
        assertFailsWith<CollectionNotAccessible> {
            getCollection(PRIVATE_USER_ID, testPrivateUsername, testKitchenBookCollection.id)
        }
    }
}