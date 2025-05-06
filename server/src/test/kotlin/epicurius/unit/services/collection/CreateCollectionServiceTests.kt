package epicurius.unit.services.collection

import epicurius.domain.collection.CollectionType
import epicurius.domain.exceptions.CollectionAlreadyExists
import epicurius.http.collection.models.input.CreateCollectionInputModel
import org.mockito.kotlin.whenever
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith

class CreateCollectionServiceTests: CollectionServiceTest() {

    private val createCollectionInputInfo = CreateCollectionInputModel("Test Collection", CollectionType.FAVOURITE)

    @Test
    fun `Should create a collection and then retrieve it successfully`() {
        // given information for a new collection (createCollectionInputInfo)

        // mock
        whenever(
            jdbiCollectionRepositoryMock.getCollection(PUBLIC_USER_ID, createCollectionInputInfo.name, createCollectionInputInfo.type)
        ).thenReturn(null)
        whenever(
            jdbiCollectionRepositoryMock.createCollection(PUBLIC_USER_ID, createCollectionInputInfo.name, createCollectionInputInfo.type)
        ).thenReturn(FAVOURITE_COLLECTION_ID)

        // when creating the collection
        val collection = createCollection(PUBLIC_USER_ID, createCollectionInputInfo)

        // then the collection is created successfully
        assertEquals(FAVOURITE_COLLECTION_ID, collection.id)
    }

    @Test
    fun `Should throw CollectionAlreadyExists when creating a collection that already exists`() {
        // given information for a new collection (createCollectionInputInfo)

        // mock
        whenever(
            jdbiCollectionRepositoryMock.getCollection(PUBLIC_USER_ID, createCollectionInputInfo.name, createCollectionInputInfo.type)
        ).thenReturn(testFavouriteJdbiCollectionModel)

        // when creating the collection
        // then the collection is not created and throws CollectionAlreadyExists exception
        assertFailsWith<CollectionAlreadyExists> {
            createCollection(PUBLIC_USER_ID, createCollectionInputInfo)
        }
    }
}