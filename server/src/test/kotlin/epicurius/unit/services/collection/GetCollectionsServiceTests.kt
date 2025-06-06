package epicurius.unit.services.collection

import epicurius.domain.collection.CollectionType
import epicurius.repository.jdbi.collection.models.JdbiCollectionProfileModel
import org.mockito.kotlin.whenever
import kotlin.test.Test
import kotlin.test.assertNotNull

class GetCollectionsServiceTests : CollectionServiceTest() {

    @Test
    fun `Should retrieve multiple collections from a user successfully`() {
        // given a collection type
        val collectionType = CollectionType.FAVOURITE
        val limit = 10

        // mock
        val mockJdbiCollectionProfileModel = JdbiCollectionProfileModel(1, "Test Collection")
        whenever(jdbiCollectionRepositoryMock.getCollections(testPublicUser.id, collectionType, null, limit))
            .thenReturn(listOf(mockJdbiCollectionProfileModel))

        // when retrieving the collections
        val collections = getCollections(testPublicUser.id, collectionType, null, limit)

        // then the collection is retrieved successfully
        assertNotNull(collections.find { it.id == mockJdbiCollectionProfileModel.id })
    }
}
