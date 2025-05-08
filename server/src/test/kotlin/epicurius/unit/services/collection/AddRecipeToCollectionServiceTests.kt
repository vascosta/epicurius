package epicurius.unit.services.collection

import epicurius.domain.exceptions.CollectionNotFound
import epicurius.domain.exceptions.NotTheCollectionOwner
import epicurius.domain.exceptions.NotTheRecipeAuthor
import epicurius.domain.exceptions.RecipeAlreadyInCollection
import epicurius.domain.exceptions.RecipeNotAccessible
import epicurius.domain.exceptions.RecipeNotFound
import epicurius.domain.picture.PictureDomain.Companion.RECIPES_FOLDER
import org.mockito.kotlin.whenever
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith

class AddRecipeToCollectionServiceTests: CollectionServiceTest() {

    @Test
    fun `Should add a recipe to a collection successfully`() {
        // given a collection id (FAVOURITE_COLLECTION_ID) and a recipe id (RECIPE_ID)

        // mock
        val mockJdbiUpdatedCollectionModel = testFavouriteJdbiCollectionModel.copy(
            recipes = listOf(testJdbiRecipeInfo)
        )
        whenever(jdbiCollectionRepositoryMock.getCollectionById(FAVOURITE_COLLECTION_ID))
            .thenReturn(testFavouriteJdbiCollectionModel)
        whenever(jdbiCollectionRepositoryMock.checkIfUserIsCollectionOwner(FAVOURITE_COLLECTION_ID, PUBLIC_USER_ID))
            .thenReturn(true)
        whenever(jdbiRecipeRepositoryMock.getRecipeById(RECIPE_ID)).thenReturn(testJdbiRecipeModel)
        whenever(jdbiUserRepositoryMock.checkUserVisibility(testPublicUsername, testPublicUsername)).thenReturn(true)
        whenever(jdbiCollectionRepositoryMock.checkIfRecipeInCollection(FAVOURITE_COLLECTION_ID, RECIPE_ID))
            .thenReturn(false)
        whenever(jdbiCollectionRepositoryMock.addRecipeToCollection(FAVOURITE_COLLECTION_ID, RECIPE_ID))
            .thenReturn(mockJdbiUpdatedCollectionModel)
        whenever(pictureRepositoryMock.getPicture(testJdbiRecipeModel.picturesNames.first(), RECIPES_FOLDER))
            .thenReturn(byteArrayOf())

        // when adding the recipe to the collection
        val updatedCollection = addRecipeToCollection(
            testPublicUser.id, testPublicUsername, FAVOURITE_COLLECTION_ID, RECIPE_ID
        )

        // then the recipe is added successfully
        assertEquals(FAVOURITE_COLLECTION_ID, updatedCollection.id)
        assertEquals(testRecipeInfo.id, updatedCollection.recipes.first().id)
    }

    @Test
    fun `Should throw CollectionNotFound exception when adding a recipe to a non-existing collection`() {
        // given a non-existing collection id
        val nonExistingCollectionId = 1904

        // mock
        whenever(jdbiCollectionRepositoryMock.getCollectionById(nonExistingCollectionId)).thenReturn(null)

        // when adding the recipe to the collection
        // then the recipe is not added and throws CollectionNotFound exception
        assertFailsWith<CollectionNotFound> {
            addRecipeToCollection(
                testPublicUser.id, testPublicUsername, nonExistingCollectionId, RECIPE_ID
            )
        }
    }

    @Test
    fun `Should throw NotTheCollectionOwner exception when adding a recipe to a collection that the user does not own`() {
        // given a collection id (FAVOURITE_COLLECTION_ID) and a recipe id (RECIPE_ID)

        // mock
        whenever(jdbiCollectionRepositoryMock.getCollectionById(FAVOURITE_COLLECTION_ID))
            .thenReturn(testFavouriteJdbiCollectionModel)
        whenever(jdbiCollectionRepositoryMock.checkIfUserIsCollectionOwner(FAVOURITE_COLLECTION_ID, PRIVATE_USER_ID))
            .thenReturn(false)

        // when adding the recipe to the collection
        // then the recipe is not added and throws NotTheCollectionOwner exception
        assertFailsWith<NotTheCollectionOwner> {
            addRecipeToCollection(
                testPrivateUser.id, testPrivateUsername, FAVOURITE_COLLECTION_ID, RECIPE_ID
            )
        }
    }

    @Test
    fun `Should throw RecipeNotFound exception when adding a non-existing recipe to a collection`() {
        // given a collection id (FAVOURITE_COLLECTION_ID) and a non-existing recipe id
        val nonExistingRecipeId = 1904

        // mock
        whenever(jdbiCollectionRepositoryMock.getCollectionById(FAVOURITE_COLLECTION_ID))
            .thenReturn(testFavouriteJdbiCollectionModel)
        whenever(jdbiCollectionRepositoryMock.checkIfUserIsCollectionOwner(FAVOURITE_COLLECTION_ID, PUBLIC_USER_ID))
            .thenReturn(true)
        whenever(jdbiRecipeRepositoryMock.getRecipeById(nonExistingRecipeId)).thenReturn(null)

        // when adding the recipe to the collection
        // then the recipe is not added and throws RecipeNotFound exception
        assertFailsWith<RecipeNotFound> {
            addRecipeToCollection(
                testPublicUser.id, testPublicUsername, FAVOURITE_COLLECTION_ID, nonExistingRecipeId
            )
        }
    }

    @Test
    fun `Should throw RecipeNotAccessible exception when adding a recipe that belongs to a private user not being followed to a Favourite type collection`() {
        // given a collection id (FAVOURITE_COLLECTION_ID) and a recipe id (RECIPE_ID)

        // mock
        whenever(jdbiCollectionRepositoryMock.getCollectionById(FAVOURITE_COLLECTION_ID))
            .thenReturn(testFavouriteJdbiCollectionModel)
        whenever(jdbiCollectionRepositoryMock.checkIfUserIsCollectionOwner(FAVOURITE_COLLECTION_ID, PUBLIC_USER_ID))
            .thenReturn(true)
        whenever(jdbiRecipeRepositoryMock.getRecipeById(RECIPE_ID)).thenReturn(testJdbiRecipeModel)
        whenever(jdbiCollectionRepositoryMock.checkIfRecipeInCollection(FAVOURITE_COLLECTION_ID, RECIPE_ID))
            .thenReturn(false)
        whenever(jdbiUserRepositoryMock.checkUserVisibility(testPrivateUsername, testPublicUsername)).thenReturn(false)

        // when adding the recipe to the collection
        // then the recipe is not added and throws RecipeNotAccessible exception
        assertFailsWith<RecipeNotAccessible> {
            addRecipeToCollection(
                testPublicUser.id, testPublicUsername, FAVOURITE_COLLECTION_ID, RECIPE_ID
            )
        }
    }

    @Test
    fun `Should throw NotTheRecipeAuthor exception when adding a recipe that does not belong to the user to a Kitchen Book type collection`() {
        // given a collection id (KITCHEN_BOOK_COLLECTION_ID) and a recipe id (RECIPE_ID)

        // mock
        whenever(jdbiCollectionRepositoryMock.getCollectionById(KITCHEN_BOOK_COLLECTION_ID))
            .thenReturn(testKitchenBookJdbiCollectionModel)
        whenever(jdbiCollectionRepositoryMock.checkIfUserIsCollectionOwner(KITCHEN_BOOK_COLLECTION_ID, PRIVATE_USER_ID))
            .thenReturn(true)
        whenever(jdbiRecipeRepositoryMock.getRecipeById(RECIPE_ID)).thenReturn(testJdbiRecipeModel)

        // when adding the recipe to the collection
        // then the recipe is not added and throws NotTheRecipeAuthor exception
        assertFailsWith<NotTheRecipeAuthor> {
            addRecipeToCollection(
                testPrivateUser.id, testPrivateUsername, KITCHEN_BOOK_COLLECTION_ID, RECIPE_ID
            )
        }
    }

    @Test
    fun `Should throw RecipeAlreadyInCollection exception when adding a recipe that is already in the collection`() {
        // given a collection id (FAVOURITE_COLLECTION_ID) and a recipe id (RECIPE_ID)

        // mock
        whenever(jdbiCollectionRepositoryMock.getCollectionById(FAVOURITE_COLLECTION_ID))
            .thenReturn(testFavouriteJdbiCollectionModel)
        whenever(jdbiCollectionRepositoryMock.checkIfUserIsCollectionOwner(FAVOURITE_COLLECTION_ID, PUBLIC_USER_ID))
            .thenReturn(true)
        whenever(jdbiRecipeRepositoryMock.getRecipeById(RECIPE_ID)).thenReturn(testJdbiRecipeModel)
        whenever(jdbiUserRepositoryMock.checkUserVisibility(testPublicUsername, testPublicUsername)).thenReturn(true)
        whenever(jdbiCollectionRepositoryMock.checkIfRecipeInCollection(FAVOURITE_COLLECTION_ID, RECIPE_ID))
            .thenReturn(true)

        // when adding the recipe to the collection
        // then the recipe is not added and throws RecipeAlreadyInCollection exception
        assertFailsWith<RecipeAlreadyInCollection> {
            addRecipeToCollection(
                testPublicUser.id, testPublicUsername, FAVOURITE_COLLECTION_ID, RECIPE_ID
            )
        }
    }


}