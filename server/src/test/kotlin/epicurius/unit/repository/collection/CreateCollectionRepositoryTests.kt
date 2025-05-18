package epicurius.unit.repository.collection

import epicurius.domain.collection.CollectionType
import epicurius.utils.createTestCollection
import org.junit.jupiter.api.Assertions.assertEquals
import kotlin.test.Test
import kotlin.test.assertNotNull
import kotlin.test.assertNull
import kotlin.test.assertTrue

class CreateCollectionRepositoryTests : CollectionRepositoryTest() {

    @Test
    fun `Should create a collection and then retrieve it successfully`() {
        // given information for a new collection
        val collectionName = "Test Collection"
        val collectionType = CollectionType.FAVOURITE

        // when creating the collection
        val collectionId = createCollection(testOwner.user.id, collectionName, collectionType)

        // then the collection is created successfully
        val jdbiCollection = getCollection(testOwner.user.id, collectionName, collectionType)
        val jdbiCollectionById = getCollectionById(collectionId)
        assertNotNull(jdbiCollection)
        assertNotNull(jdbiCollectionById)
        assertEquals(testOwner.user.id, jdbiCollection.ownerId)
        assertEquals(testOwner.user.id, jdbiCollectionById.ownerId)
        assertEquals(collectionName, jdbiCollection.name)
        assertEquals(collectionName, jdbiCollectionById.name)
        assertEquals(collectionType, jdbiCollection.type)
        assertEquals(collectionType, jdbiCollectionById.type)
        assertTrue(jdbiCollection.recipes.isEmpty())
        assertTrue(jdbiCollectionById.recipes.isEmpty())
    }

    @Test
    fun `Should create a collection and then delete it successfully`() {
        // given a collection
        val collectionId = createTestCollection(tm, testOwner.user.id, CollectionType.FAVOURITE)

        // when deleting the collection
        deleteCollection(collectionId)

        // then the collection is deleted successfully
        val jdbiCollection = getCollectionById(collectionId)
        assertNull(jdbiCollection)
    }
}
