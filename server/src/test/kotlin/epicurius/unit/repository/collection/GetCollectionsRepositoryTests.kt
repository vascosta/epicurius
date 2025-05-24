package epicurius.unit.repository.collection

import epicurius.domain.PagingParams
import epicurius.domain.collection.CollectionType
import epicurius.utils.createTestCollection
import epicurius.utils.createTestUser
import kotlin.test.Test
import kotlin.test.assertNotNull

class GetCollectionsRepositoryTests: CollectionRepositoryTest() {

    @Test
    fun `Should retrieve multiple collections from a user successfully`() {
        // given a user with multiple collections
        val user = createTestUser(tm)
        val collectionFavouriteId = createTestCollection(tm, user.user.id, CollectionType.FAVOURITE)
        val collectionKitchenBookId = createTestCollection(tm, user.user.id, CollectionType.KITCHEN_BOOK)

        // when retrieving collections of a specific type
        val favouriteCollections = getCollections(user.user.id, CollectionType.FAVOURITE, PagingParams(0, 1))
        val kitchenBookCollections = getCollections(user.user.id, CollectionType.KITCHEN_BOOK, PagingParams(0, 1))

        // then the collections are retrieved successfully
        assertNotNull(favouriteCollections.find { it.id == collectionFavouriteId })
        assertNotNull(kitchenBookCollections.find { it.id == collectionKitchenBookId })
    }
}