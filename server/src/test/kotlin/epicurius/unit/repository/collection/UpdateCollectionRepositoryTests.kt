package epicurius.unit.repository.collection

import kotlin.test.Test
import kotlin.test.assertEquals

class UpdateCollectionRepositoryTests : CollectionRepositoryTest() {

    @Test
    fun `Should update a collection and then retrieve it successfully`() {
        // given a collection (testCollectionId) and new information for updating it
        val newName = "Updated Collection Name"

        // when updating the collection
        val updatedCollection = updateCollection(testCollectionId, newName)

        // then the collection is updated successfully
        assertEquals(newName, updatedCollection.name)
    }
}
