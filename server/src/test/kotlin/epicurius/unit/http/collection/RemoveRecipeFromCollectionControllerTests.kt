package epicurius.unit.http.collection

import epicurius.domain.exceptions.CollectionNotFound
import epicurius.domain.exceptions.NotTheCollectionOwner
import epicurius.domain.exceptions.RecipeNotFound
import epicurius.domain.exceptions.RecipeNotInCollection
import epicurius.http.controllers.collection.models.output.RemoveRecipeFromCollectionOutputModel
import org.mockito.kotlin.whenever
import org.springframework.http.HttpStatus
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith

class RemoveRecipeFromCollectionControllerTests : CollectionHttpTest() {

    @Test
    fun `Should remove a recipe from a collection successfully`() {
        // given a collection id (FAVOURITE_COLLECTION_ID) and a recipe id (RECIPE_ID)

        // mock
        whenever(
            collectionServiceMock
                .removeRecipeFromCollection(testPublicAuthenticatedUser.user.id, FAVOURITE_COLLECTION_ID, RECIPE_ID)
        ).thenReturn(testFavouriteCollection)
        whenever(authenticationRefreshHandlerMock.refreshToken(testPublicAuthenticatedUser.token)).thenReturn(mockCookie)

        // when removing the recipe from the collection
        val response = removeRecipeFromCollection(
            testPublicAuthenticatedUser,
            FAVOURITE_COLLECTION_ID,
            RECIPE_ID,
            mockResponse
        )
        val body = response.body as RemoveRecipeFromCollectionOutputModel

        // then the recipe is removed successfully
        assertEquals(HttpStatus.OK, response.statusCode)
        assertEquals(testFavouriteCollection, body.collection)
    }

    @Test
    fun `Should throw CollectionNotFound exception when removing a recipe from a non-existing collection`() {
        // given a non-existing collection id
        val nonExistingCollectionId = 1904

        // mock
        whenever(
            collectionServiceMock
                .removeRecipeFromCollection(testPublicAuthenticatedUser.user.id, nonExistingCollectionId, RECIPE_ID)
        ).thenThrow(CollectionNotFound())

        // when removing the recipe from the collection
        // then the recipe is not removed and throws CollectionNotFound exception
        assertFailsWith<CollectionNotFound> {
            removeRecipeFromCollection(testPublicAuthenticatedUser, nonExistingCollectionId, RECIPE_ID, mockResponse)
        }
    }

    @Test
    fun `Should throw NotTheCollectionOwner exception when removing a recipe from a collection that the user does not own`() {
        // given a collection id (FAVOURITE_COLLECTION_ID) and a recipe id (RECIPE_ID)

        // mock
        whenever(
            collectionServiceMock
                .removeRecipeFromCollection(testPrivateAuthenticatedUser.user.id, FAVOURITE_COLLECTION_ID, RECIPE_ID)
        ).thenThrow(NotTheCollectionOwner())

        // when removing the recipe from the collection
        // then the recipe is not removed and throws NotTheCollectionOwner exception
        assertFailsWith<NotTheCollectionOwner> {
            removeRecipeFromCollection(testPrivateAuthenticatedUser, FAVOURITE_COLLECTION_ID, RECIPE_ID, mockResponse)
        }
    }

    @Test
    fun `Should throw RecipeNotFound exception when removing a non-existing recipe from a collection`() {
        // given a collection id (FAVOURITE_COLLECTION_ID) and a non-existing recipe id
        val nonExistingRecipeId = 1904

        // mock
        whenever(
            collectionServiceMock
                .removeRecipeFromCollection(testPublicAuthenticatedUser.user.id, FAVOURITE_COLLECTION_ID, nonExistingRecipeId)
        ).thenThrow(RecipeNotFound())

        // when removing the recipe from the collection
        // then the recipe is not removed and throws RecipeNotFound exception
        assertFailsWith<RecipeNotFound> {
            removeRecipeFromCollection(testPublicAuthenticatedUser, FAVOURITE_COLLECTION_ID, nonExistingRecipeId, mockResponse)
        }
    }

    @Test
    fun `Should throw RecipeNotInCollection exception when removing a recipe that is not in the collection`() {
        // given a collection id (FAVOURITE_COLLECTION_ID) and a recipe id (RECIPE_ID)

        // mock
        whenever(
            collectionServiceMock
                .removeRecipeFromCollection(testPublicAuthenticatedUser.user.id, FAVOURITE_COLLECTION_ID, RECIPE_ID)
        ).thenThrow(RecipeNotInCollection())

        // when removing the recipe from the collection
        // then the recipe is not removed and throws RecipeNotInCollection exception
        assertFailsWith<RecipeNotInCollection> {
            removeRecipeFromCollection(testPublicAuthenticatedUser, FAVOURITE_COLLECTION_ID, RECIPE_ID, mockResponse)
        }
    }
}
