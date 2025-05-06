package epicurius.unit.services.collection

import epicurius.domain.exceptions.CollectionNotFound
import epicurius.domain.exceptions.NotTheCollectionOwner
import epicurius.domain.exceptions.RecipeNotFound
import epicurius.domain.exceptions.RecipeNotInCollection
import org.mockito.kotlin.whenever
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith
import kotlin.test.assertTrue

class RemoveRecipeFromCollectionServiceTests: CollectionServiceTest() {

    @Test
    fun `Should remove a recipe from a collection successfully`() {
        // given a collection id (FAVOURITE_COLLECTION_ID) and a recipe id (RECIPE_ID)

        // mock
        val mockJdbiUpdatedCollectionModel = testFavouriteJdbiCollectionModel.copy(
            recipes = emptyList()
        )
        whenever(jdbiCollectionRepositoryMock.getCollectionById(FAVOURITE_COLLECTION_ID))
            .thenReturn(testFavouriteJdbiCollectionModel)
        whenever(jdbiCollectionRepositoryMock.checkIfUserIsCollectionOwner(FAVOURITE_COLLECTION_ID, PUBLIC_USER_ID))
            .thenReturn(true)
        whenever(jdbiRecipeRepositoryMock.getRecipeById(RECIPE_ID)).thenReturn(testJdbiRecipeModel)
        whenever(jdbiCollectionRepositoryMock.checkIfRecipeInCollection(FAVOURITE_COLLECTION_ID, RECIPE_ID))
            .thenReturn(true)
        whenever(jdbiCollectionRepositoryMock.removeRecipeFromCollection(FAVOURITE_COLLECTION_ID, RECIPE_ID))
            .thenReturn(mockJdbiUpdatedCollectionModel)

        // when removing the recipe from the collection
        val updatedCollection = removeRecipeFromCollection(testPublicUser.id, FAVOURITE_COLLECTION_ID, RECIPE_ID)

        // then the recipe is removed successfully
        assertEquals(FAVOURITE_COLLECTION_ID, updatedCollection.id)
        assertTrue(updatedCollection.recipes.isEmpty())
    }

    @Test
    fun `Should throw CollectionNotFound exception when removing a recipe from a non-existing collection`() {
        // given a non-existing collection id
        val nonExistingCollectionId = 1904

        // mock
        whenever(jdbiCollectionRepositoryMock.getCollectionById(nonExistingCollectionId)).thenReturn(null)

        // when removing the recipe from the collection
        // then the recipe is not removed and throws CollectionNotFound exception
        assertFailsWith<CollectionNotFound> {
            removeRecipeFromCollection(testPublicUser.id, nonExistingCollectionId, RECIPE_ID)
        }
    }

    @Test
    fun `Should throw NotTheCollectionOwner exception when removing a recipe from a collection that the user does not own`() {
        // given a collection id (FAVOURITE_COLLECTION_ID) and a recipe id (RECIPE_ID)

        // mock
        whenever(jdbiCollectionRepositoryMock.getCollectionById(FAVOURITE_COLLECTION_ID))
            .thenReturn(testFavouriteJdbiCollectionModel)
        whenever(jdbiCollectionRepositoryMock.checkIfUserIsCollectionOwner(FAVOURITE_COLLECTION_ID, PRIVATE_USER_ID))
            .thenReturn(false)

        // when removing the recipe from the collection
        // then the recipe is not removed and throws NotTheCollectionOwner exception
        assertFailsWith<NotTheCollectionOwner> {
            removeRecipeFromCollection(testPrivateUser.id, FAVOURITE_COLLECTION_ID, RECIPE_ID)
        }
    }

    @Test
    fun `Should throw RecipeNotFound exception when removing a recipe to a collection that does not exist`() {
        // given a collection id (FAVOURITE_COLLECTION_ID) and a non-existing recipe id
        val nonExistingRecipeId = 1904

        // mock
        whenever(jdbiCollectionRepositoryMock.getCollectionById(FAVOURITE_COLLECTION_ID))
            .thenReturn(testFavouriteJdbiCollectionModel)
        whenever(jdbiCollectionRepositoryMock.checkIfUserIsCollectionOwner(FAVOURITE_COLLECTION_ID, PUBLIC_USER_ID))
            .thenReturn(true)
        whenever(jdbiRecipeRepositoryMock.getRecipeById(nonExistingRecipeId)).thenReturn(null)

        // when removing the recipe from the collection
        // then the recipe is not removed and throws RecipeNotFound exception
        assertFailsWith<RecipeNotFound> {
            removeRecipeFromCollection(testPublicUser.id, FAVOURITE_COLLECTION_ID, nonExistingRecipeId)
        }
    }

    @Test
    fun `Should throw RecipeNotInCollection exception when removing a recipe that is not in the collection`() {
        // given a collection id (FAVOURITE_COLLECTION_ID) and a recipe id (RECIPE_ID)

        // mock
        whenever(jdbiCollectionRepositoryMock.getCollectionById(FAVOURITE_COLLECTION_ID))
            .thenReturn(testFavouriteJdbiCollectionModel)
        whenever(jdbiCollectionRepositoryMock.checkIfUserIsCollectionOwner(FAVOURITE_COLLECTION_ID, PUBLIC_USER_ID))
            .thenReturn(true)
        whenever(jdbiRecipeRepositoryMock.getRecipeById(RECIPE_ID)).thenReturn(testJdbiRecipeModel)
        whenever(jdbiCollectionRepositoryMock.checkIfRecipeInCollection(FAVOURITE_COLLECTION_ID, RECIPE_ID))
            .thenReturn(false)

        // when removing the recipe from the collection
        // then the recipe is not removed and throws RecipeNotInCollection exception
        assertFailsWith<RecipeNotInCollection> {
            removeRecipeFromCollection(testPublicUser.id, FAVOURITE_COLLECTION_ID, RECIPE_ID)
        }
    }
}