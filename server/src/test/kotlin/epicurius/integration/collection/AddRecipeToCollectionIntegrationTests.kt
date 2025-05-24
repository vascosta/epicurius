package epicurius.integration.collection

import epicurius.domain.collection.CollectionType
import epicurius.domain.exceptions.CollectionNotFound
import epicurius.domain.exceptions.NotTheCollectionOwner
import epicurius.domain.exceptions.NotTheRecipeAuthor
import epicurius.domain.exceptions.RecipeAlreadyInCollection
import epicurius.domain.exceptions.RecipeNotAccessible
import epicurius.domain.exceptions.RecipeNotFound
import epicurius.http.utils.Problem
import epicurius.http.utils.Uris
import epicurius.integration.utils.getBody
import epicurius.integration.utils.post
import epicurius.utils.createTestCollection
import epicurius.utils.createTestRecipe
import epicurius.utils.createTestUser
import org.springframework.http.HttpStatus
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotNull

class AddRecipeToCollectionIntegrationTests : CollectionIntegrationTest() {

    private val testUser = createTestUser(tm)
    private val privateTestUser = createTestUser(tm, true)
    private val testFavouritesCollectionId = createTestCollection(tm, testUser.user.id, CollectionType.FAVOURITE)
    private val testRecipe = createTestRecipe(tm, fs, testUser.user)
    private val privateTestRecipe = createTestRecipe(tm, fs, privateTestUser.user)

    @Test
    fun `Should add a recipe to a collection successfully wit code 200`() {
        // given a collection and a recipe (testFavouritesCollection and testRecipe)

        // when adding the recipe to the collection
        val body = addRecipeToCollection(testUser.token, testFavouritesCollectionId, testRecipe.id)

        // then the recipe is added successfully with code 200
        assertNotNull(body)
        assertNotNull(body.collection.recipes.find { it.id == testRecipe.id })
    }

    @Test
    fun `Should fail with code 404 when adding a recipe to a non-existing collection`() {
        // given a non-existing collection id
        val nonExistingCollectionId = 9999

        // when adding the recipe to the collection
        val error = post<Problem>(
            client,
            api(Uris.Collection.COLLECTION_RECIPES.replace("{id}", nonExistingCollectionId.toString())),
            mapOf("recipeId" to testRecipe.id),
            HttpStatus.NOT_FOUND,
            testUser.token,
        )

        // then the recipe is not added and fails with code 404
        val errorBody = getBody(error)
        assertEquals(CollectionNotFound().message, errorBody.detail)
    }

    @Test
    fun `Should fail with code 403 when adding a recipe to a collection that the user does not own`() {
        // given a collection and a recipe (testFavouritesCollection and testRecipe)

        // when adding the recipe to the collection
        val error = post<Problem>(
            client,
            api(Uris.Collection.COLLECTION_RECIPES.replace("{id}", testFavouritesCollectionId.toString())),
            mapOf("recipeId" to testRecipe.id),
            HttpStatus.FORBIDDEN,
            privateTestUser.token,
        )

        // then the recipe is not added and fails with code 403
        val errorBody = getBody(error)
        assertEquals(NotTheCollectionOwner().message, errorBody.detail)
    }

    @Test
    fun `Should fail with code 404 when adding a non-existing recipe to a collection`() {
        // given a collection id (testFavouritesCollectionId) and a non-existing recipe id
        val nonExistingRecipeId = 9999

        // when adding the recipe to the collection
        val error = post<Problem>(
            client,
            api(Uris.Collection.COLLECTION_RECIPES.replace("{id}", testFavouritesCollectionId.toString())),
            mapOf("recipeId" to nonExistingRecipeId),
            HttpStatus.NOT_FOUND,
            testUser.token,
        )

        // then the recipe is not added and fails with code 404
        val errorBody = getBody(error)
        assertEquals(RecipeNotFound().message, errorBody.detail)
    }

    @Test
    fun `Should throw RecipeNotAccessible exception when adding a recipe that belongs to a private user not being followed to a Favourite type collection`() {
        // given a collection id (testFavouritesCollectionId) and a recipe (privateTestRecipe)

        // when adding the recipe to the collection
        val error = post<Problem>(
            client,
            api(Uris.Collection.COLLECTION_RECIPES.replace("{id}", testFavouritesCollectionId.toString())),
            mapOf("recipeId" to privateTestRecipe.id),
            HttpStatus.FORBIDDEN,
            testUser.token,
        )

        // then the recipe is not added and fails with code 403
        val errorBody = getBody(error)
        assertEquals(RecipeNotAccessible().message, errorBody.detail)
    }

    @Test
    fun `Should fail with code 403 when adding a recipe that does not belong to the user to a Kitchen Book type collection`() {
        // given a collection id and a recipe (privateTestRecipe)
        val kitchenBookCollectionId = createTestCollection(tm, testUser.user.id, CollectionType.KITCHEN_BOOK)

        // when adding the recipe to the collection
        val error = post<Problem>(
            client,
            api(Uris.Collection.COLLECTION_RECIPES.replace("{id}", kitchenBookCollectionId.toString())),
            mapOf("recipeId" to privateTestRecipe.id),
            HttpStatus.FORBIDDEN,
            testUser.token,
        )

        // then the recipe is not added and fails with code 403
        val errorBody = getBody(error)
        assertEquals(NotTheRecipeAuthor().message, errorBody.detail)
    }

    @Test
    fun `Should fail with code 409 when adding a recipe that is already in the collection`() {
        // given a collection id (testFavouritesCollectionId) and a recipe
        val recipe = createTestRecipe(tm, fs, testUser.user)
        addRecipeToCollection(testUser.token, testFavouritesCollectionId, recipe.id)

        // when adding the recipe again to the collection
        val error = post<Problem>(
            client,
            api(Uris.Collection.COLLECTION_RECIPES.replace("{id}", testFavouritesCollectionId.toString())),
            mapOf("recipeId" to recipe.id),
            HttpStatus.CONFLICT,
            testUser.token,
        )

        // then the recipe is not added and fails with code 409
        val errorBody = getBody(error)
        assertEquals(RecipeAlreadyInCollection().message, errorBody.detail)
    }
}
