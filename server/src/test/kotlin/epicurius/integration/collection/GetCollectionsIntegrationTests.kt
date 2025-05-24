package epicurius.integration.collection

import epicurius.domain.PagingParams
import epicurius.domain.collection.CollectionType
import epicurius.utils.createTestCollection
import epicurius.utils.createTestUser
import kotlin.test.Test
import kotlin.test.assertNotNull

class GetCollectionsIntegrationTests: CollectionIntegrationTest() {

    private val testUser = createTestUser(tm)
    private val testCollectionId = createTestCollection(tm, testUser.user.id, CollectionType.KITCHEN_BOOK)
    private val testCollectionId2 = createTestCollection(tm, testUser.user.id, CollectionType.KITCHEN_BOOK)

    @Test
    fun `Should retrieve multiple collections from a user successfully`() {
        // given a collection type and paging parameters
        val collectionType = CollectionType.KITCHEN_BOOK
        val pagingParams = PagingParams(0, 10)

        // when retrieving the collections
        val body = getCollections(testUser.token, collectionType, pagingParams.skip, pagingParams.limit)

        // then the collections are retrieved successfully
        assertNotNull(body)
        assertNotNull(body.collections.find { it.id == testCollectionId })
        assertNotNull(body.collections.find { it.id == testCollectionId2 })
    }
}