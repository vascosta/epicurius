package epicurius.unit.http.collection

import epicurius.domain.exceptions.CollectionNotFound
import epicurius.domain.exceptions.RecipeAlreadyInCollection
import epicurius.domain.exceptions.RecipeNotAccessible
import epicurius.domain.exceptions.RecipeNotFound
import epicurius.http.controllers.collection.models.input.AddRecipeToCollectionInputModel
import epicurius.http.controllers.collection.models.output.AddRecipeToCollectionOutputModel
import org.mockito.kotlin.whenever
import org.springframework.http.HttpStatus
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith

class AddRecipeToCollectionControllerTests : CollectionHttpTest() {

    private val addRecipeToCollectionInputInfo = AddRecipeToCollectionInputModel(RECIPE_ID)

    @Test
    fun `Should add a recipe to a collection successfully`() {
        // given a collection id (FAVOURITE_COLLECTION_ID) and a recipe id (RECIPE_ID)

        // mock
        val mockUpdatedCollection = testFavouriteCollection.copy(
            recipes = listOf(testRecipeInfo)
        )
        whenever(
            collectionServiceMock.addRecipeToCollection(
                testPublicAuthenticatedUser.user.id, FAVOURITE_COLLECTION_ID, RECIPE_ID
            )
        ).thenReturn(mockUpdatedCollection)

        // when adding the recipe to the collection
        val response = addRecipeToCollection(
            testPublicAuthenticatedUser,
            FAVOURITE_COLLECTION_ID,
            addRecipeToCollectionInputInfo
        )
        val body = response.body as AddRecipeToCollectionOutputModel

        // then the recipe is added successfully
        assertEquals(HttpStatus.OK, response.statusCode)
        assertEquals(mockUpdatedCollection, body.collection)
    }

    @Test
    fun `Should throw CollectionNotFound exception when adding a recipe to a non-existing collection`() {
        // given a non-existing collection id
        val nonExistingCollectionId = 1904

        // mock
        whenever(
            collectionServiceMock.addRecipeToCollection(
                testPublicAuthenticatedUser.user.id, nonExistingCollectionId, RECIPE_ID
            )
        ).thenThrow(CollectionNotFound())

        // when adding the recipe to the collection
        // then the recipe is not added and throws CollectionNotFound exception
        assertFailsWith<CollectionNotFound> {
            addRecipeToCollection(
                testPublicAuthenticatedUser, nonExistingCollectionId, addRecipeToCollectionInputInfo
            )
        }
    }

    @Test
    fun `Should throw NotTheCollectionOwner exception when adding a recipe to a collection that the user does not own`() {
        // given a collection id (FAVOURITE_COLLECTION_ID) and a recipe id (RECIPE_ID)

        // mock
        whenever(
            collectionServiceMock.addRecipeToCollection(
                testPrivateAuthenticatedUser.user.id, FAVOURITE_COLLECTION_ID, RECIPE_ID
            )
        ).thenThrow(CollectionNotFound())

        // when adding the recipe to the collection
        // then the recipe is not added and throws CollectionNotFound exception
        assertFailsWith<CollectionNotFound> {
            addRecipeToCollection(
                testPrivateAuthenticatedUser, FAVOURITE_COLLECTION_ID, addRecipeToCollectionInputInfo
            )
        }
    }

    @Test
    fun `Should throw RecipeNotFound exception when adding a non-existing recipe to a collection`() {
        // given a collection id (FAVOURITE_COLLECTION_ID) and a non-existing recipe id
        val nonExistingRecipeId = 1904

        // mock
        whenever(
            collectionServiceMock.addRecipeToCollection(
                testPublicAuthenticatedUser.user.id, FAVOURITE_COLLECTION_ID, nonExistingRecipeId
            )
        ).thenThrow(RecipeNotFound())

        // when adding the recipe to the collection
        // then the recipe is not added and throws RecipeNotFound exception
        assertFailsWith<RecipeNotFound> {
            addRecipeToCollection(
                testPublicAuthenticatedUser, FAVOURITE_COLLECTION_ID, addRecipeToCollectionInputInfo.copy(nonExistingRecipeId)
            )
        }
    }

    @Test
    fun `Should throw RecipeNotAccessible exception when adding a recipe that belongs to a private user not being followed to a Favourite type collection`() {
        // given a collection id (FAVOURITE_COLLECTION_ID) and a recipe id (RECIPE_ID)

        // mock
        whenever(
            collectionServiceMock.addRecipeToCollection(
                testPrivateAuthenticatedUser.user.id, FAVOURITE_COLLECTION_ID, RECIPE_ID
            )
        ).thenThrow(RecipeNotAccessible())

        // when adding the recipe to the collection
        // then the recipe is not added and throws RecipeNotFound exception
        assertFailsWith<RecipeNotAccessible> {
            addRecipeToCollection(
                testPrivateAuthenticatedUser, FAVOURITE_COLLECTION_ID, addRecipeToCollectionInputInfo,
            )
        }
    }

    @Test
    fun `Should throw NotTheRecipeAuthor exception when adding a recipe that does not belong to the user to a Kitchen Book type collection`() {
        // given a collection id (KITCHEN_BOOK_COLLECTION_ID) and a recipe id (RECIPE_ID)

        // mock
        whenever(
            collectionServiceMock.addRecipeToCollection(
                testPublicAuthenticatedUser.user.id, KITCHEN_BOOK_COLLECTION_ID, RECIPE_ID
            )
        ).thenThrow(RecipeNotFound())

        // when adding the recipe to the collection
        // then the recipe is not added and throws RecipeNotFound exception
        assertFailsWith<RecipeNotFound> {
            addRecipeToCollection(
                testPublicAuthenticatedUser, KITCHEN_BOOK_COLLECTION_ID, addRecipeToCollectionInputInfo
            )
        }
    }

    @Test
    fun `Should throw RecipeAlreadyInCollection exception when adding a recipe that is already in the collection`() {
        // given a collection id (FAVOURITE_COLLECTION_ID) and a recipe id (RECIPE_ID)

        // mock
        whenever(
            collectionServiceMock.addRecipeToCollection(
                testPublicAuthenticatedUser.user.id, FAVOURITE_COLLECTION_ID, RECIPE_ID
            )
        ).thenThrow(RecipeAlreadyInCollection())

        // when adding the recipe to the collection
        // then the recipe is not added and throws CollectionNotFound exception
        assertFailsWith<RecipeAlreadyInCollection> {
            addRecipeToCollection(
                testPublicAuthenticatedUser, FAVOURITE_COLLECTION_ID, addRecipeToCollectionInputInfo
            )
        }
    }
}
