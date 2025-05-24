package epicurius.integration.collection

import epicurius.domain.collection.CollectionType
import epicurius.domain.exceptions.CollectionNotFound
import epicurius.domain.exceptions.NotTheCollectionOwner
import epicurius.domain.exceptions.RecipeNotFound
import epicurius.domain.exceptions.RecipeNotInCollection
import epicurius.http.utils.Problem
import epicurius.http.utils.Uris
import epicurius.integration.utils.delete
import epicurius.integration.utils.getBody
import epicurius.utils.createTestCollection
import epicurius.utils.createTestRecipe
import epicurius.utils.createTestUser
import org.springframework.http.HttpStatus
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotNull
import kotlin.test.assertNull

class RemoveRecipeFromCollectionIntegrationTests: CollectionIntegrationTest() {

    private val testUser = createTestUser(tm)
    private val testCollectionId = createTestCollection(tm, testUser.user.id, CollectionType.FAVOURITE)
    private val testRecipe = createTestRecipe(tm, fs, testUser.user)

    @Test
    fun `Should remove a recipe from a collection successfully with code 200`() {
        // given a collection with a recipe
        tm.run { it.collectionRepository.addRecipeToCollection(testCollectionId, testRecipe.id) }

        // when removing the recipe from the collection
        val body = removeRecipeFromCollection(testUser.token, testCollectionId, testRecipe.id)

        // then the recipe is removed successfully with code 200
        assertNotNull(body)
        assertNull(body.collection.recipes.find { it.id == testRecipe.id })
    }

    @Test
    fun `Should fail with code 404 when removing a recipe from a non-existing collection`() {
        // given a non-existing collection id
        val nonExistingCollectionId = 9999

        // when removing the recipe from the collection
        val error = delete<Problem>(
            client,
            api(
                Uris.Collection.COLLECTION_RECIPE
                    .replace("{id}", nonExistingCollectionId.toString())
                    .replace("{recipeId}", testRecipe.id.toString())
            ),
            HttpStatus.NOT_FOUND,
            testUser.token
        )

        // then the recipe is not removed and fails with code 404
        val errorBody = getBody(error)
        assertEquals(CollectionNotFound().message, errorBody.detail)
    }

    @Test
    fun `Should fail with code 403 when removing a recipe from a collection that the user does not own`() {
        // given a collection id that the user does not own
        val user = createTestUser(tm, true)

        // when removing the recipe from the collection
        val error = delete<Problem>(
            client,
            api(
                Uris.Collection.COLLECTION_RECIPE
                    .replace("{id}", testCollectionId.toString())
                    .replace("{recipeId}", testRecipe.id.toString())
            ),
            HttpStatus.FORBIDDEN,
            user.token
        )

        // then the recipe is not removed and fails with code 403
        val errorBody = getBody(error)
        assertEquals(NotTheCollectionOwner().message, errorBody.detail)
    }

    @Test
    fun `Should fail with code 404 when removing a non-existing recipe from a collection`() {
        // given a collection id and a non-existing recipe id
        val nonExistingRecipeId = 9999

        // when removing the recipe from the collection
        val error = delete<Problem>(
            client,
            api(
                Uris.Collection.COLLECTION_RECIPE
                    .replace("{id}", testCollectionId.toString())
                    .replace("{recipeId}", nonExistingRecipeId.toString())
            ),
            HttpStatus.NOT_FOUND,
            testUser.token
        )

        // then the recipe is not removed and fails with code 404
        val errorBody = getBody(error)
        assertEquals(RecipeNotFound().message, errorBody.detail)
    }

    @Test
    fun `Should fail with code 409 when removing a recipe that is not in the collection`() {
        // given a collection id and a recipe id that is not in the collection
        val recipeNotInCollectionId = createTestRecipe(tm, fs, testUser.user).id

        // when removing the recipe from the collection
        val error = delete<Problem>(
            client,
            api(
                Uris.Collection.COLLECTION_RECIPE
                    .replace("{id}", testCollectionId.toString())
                    .replace("{recipeId}", recipeNotInCollectionId.toString())
            ),
            HttpStatus.CONFLICT,
            testUser.token
        )

        // then the recipe is not removed and fails with code 409
        val errorBody = getBody(error)
        assertEquals(RecipeNotInCollection().message, errorBody.detail)
    }
}