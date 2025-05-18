package epicurius.unit.repository.collection

import kotlin.test.Test
import kotlin.test.assertFalse
import kotlin.test.assertTrue

class ChecksCollectionRepositoryTests : CollectionRepositoryTest() {

    @Test
    fun `Should check if a user is the owner of a collection successfully`() {
        // given a collection id (testCollectionId) and a user id (testOwner.id)

        // when checking if the user is the owner of the collection
        val isOwner = checkIfUserIsCollectionOwner(testCollectionId, testOwner.user.id)

        // then the user is the owner of the collection
        assertTrue(isOwner)
    }

    @Test
    fun `Should check if a recipe is in the collection successfully`() {
        // given a collection id (testCollectionId) and a recipe id (testRecipeId)

        // when checking if the recipe is in the collection
        val isInCollection = checkIfRecipeInCollection(testCollectionId, testRecipe.id)

        // then the recipe is in the collection
        assertFalse(isInCollection)
    }
}
