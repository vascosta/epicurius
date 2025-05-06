package epicurius.unit.repository.collection

import kotlin.test.Test

class CheckIfUserIsCollectionOwnerRepositoryTests: CollectionRepositoryTest() {

    @Test
    fun `Should check if a user is the owner of a collection`() {
        // given a collection id (testCollectionId) and a user id (testOwner.id)

        // when checking if the user is the owner of the collection
        val isOwner = checkIfUserIsCollectionOwner(testCollectionId, testOwner.id)

        // then the user is the owner of the collection
        assert(isOwner)
    }
}