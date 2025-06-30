package epicurius.integration.recipe

import epicurius.domain.exceptions.RecipeNotAccessible
import epicurius.domain.exceptions.RecipeNotFound
import epicurius.domain.exceptions.UserNotFound
import epicurius.http.media.Problem
import epicurius.http.media.Uris
import epicurius.integration.utils.addQueryParams
import epicurius.integration.utils.get
import epicurius.utils.createTestRecipe
import epicurius.utils.createTestUser
import epicurius.utils.generateRandomUsername
import org.springframework.http.HttpStatus
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotNull
import kotlin.test.assertTrue

class GetUserRecipesIntegrationTests : RecipeIntegrationTest() {

    private val limit = 10

    @Test
    fun `Should retrieve the user's recipes successfully with code 200`() {
        // given an authenticated user and a limit for the number of recipes to retrieve
        createTestRecipe(tm, testAuthor.user)

        // when retrieving the user's recipes
        val response = getUserRecipes(testAuthor.token, null, null, limit)

        // then the recipes are retrieved successfully
        assertNotNull(response)
        assertTrue(response.recipes.isNotEmpty())
        assertEquals(2, response.recipes.size)
        assertTrue(response.recipes.all { it.authorUsername == testAuthor.user.name })
    }

    @Test
    fun `Should retrieve another user's recipes successfully with code 200`() {
        // given an authenticated user and a username
        createTestRecipe(tm, testAuthor.user)

        // when retrieving the user's recipes
        val response = getUserRecipes(testUser.token, testAuthor.user.name, null, limit)

        // then the recipes are retrieved successfully
        assertNotNull(response)
        assertTrue(response.recipes.isNotEmpty())
        assertEquals(2, response.recipes.size)
        assertTrue(response.recipes.all { it.authorUsername == testAuthor.user.name })
    }

    @Test
    fun `Should fail with code 404 when retrieving the recipes from a non-existing user`() {
        // given a non-existing username
        val username = generateRandomUsername()

        // when retrieving the recipe
        val error = get<Problem>(
            client,
            api(Uris.User.USER_RECIPES.addQueryParams(
                mapOf(
                    "username" to username,
                    "limit" to limit
                )
            )),
            responseStatus = HttpStatus.NOT_FOUND,
            token = testAuthor.token
        )
        assertNotNull(error)

        // then the recipes are not retrieved
        assertEquals(UserNotFound(username).message, error.detail)
    }


    @Test
    fun `Should fail with code 403 when retrieving the recipes from a private user not followed`() {
        // given a user not following the author and a recipe id

        // when retrieving the recipes
        val error = get<Problem>(
            client,
            api(Uris.User.USER_RECIPES.addQueryParams(
                mapOf(
                    "username" to testUser.user.name,
                    "limit" to limit
                )
            )),
            responseStatus = HttpStatus.FORBIDDEN,
            token = testAuthor.token
        )
        assertNotNull(error)

        // then the recipes are not retrieved
        assertEquals(RecipeNotAccessible().message, error.detail)
    }
}
