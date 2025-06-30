package epicurius.unit.http.recipe

import epicurius.domain.exceptions.RecipeNotAccessible
import epicurius.domain.exceptions.UserNotFound
import epicurius.domain.recipe.RecipeInfo
import epicurius.http.controllers.recipe.models.output.GetUserRecipesOutputModel
import epicurius.utils.generateRandomUsername
import kotlinx.coroutines.runBlocking
import org.mockito.kotlin.whenever
import org.springframework.http.HttpStatus
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith

class GetUserRecipesControllerTests : RecipeControllerTest() {

    private val limit = 10

    @Test
    fun `Should retrieve the user's recipes successfully`() {
        // given a user id

        // mock
        val recipeInfoMock = RecipeInfo(
            id = RECIPE_ID,
            name = testRecipe.name,
            authorUsername = testRecipe.authorUsername,
            rating = 0.0,
            cuisine = testRecipe.cuisine,
            mealType = testRecipe.mealType,
            preparationTime = testRecipe.preparationTime,
            servings = testRecipe.servings,
            picture = testRecipe.pictures.first()
        )
        whenever(
            runBlocking {
                recipeServiceMock.getUserRecipes(testAuthenticatedUser.user.id, null, null, limit)
            }
        ).thenReturn(listOf(recipeInfoMock))

        // when retrieving the user's recipes
        val response = runBlocking { getUserRecipes(testAuthenticatedUser, null, null, limit) }
        val body = response.body as GetUserRecipesOutputModel

        // then the recipes are retrieved successfully
        assertEquals(HttpStatus.OK, response.statusCode)
        assertEquals(listOf(recipeInfoMock), body.recipes)
    }

    @Test
    fun `Should retrieve another user's recipes successfully`() {
        // given a user id and a username
        val username = generateRandomUsername()

        // mock
        val recipeInfoMock = RecipeInfo(
            id = RECIPE_ID,
            name = testRecipe.name,
            authorUsername = testRecipe.authorUsername,
            rating = 0.0,
            cuisine = testRecipe.cuisine,
            mealType = testRecipe.mealType,
            preparationTime = testRecipe.preparationTime,
            servings = testRecipe.servings,
            picture = testRecipe.pictures.first()
        )
        whenever(
            runBlocking {
                recipeServiceMock.getUserRecipes(testAuthenticatedUser.user.id, username, null, limit)
            }
        ).thenReturn(listOf(recipeInfoMock))

        // when retrieving the user's recipes
        val response = runBlocking { getUserRecipes(testAuthenticatedUser, username, null, limit) }
        val body = response.body as GetUserRecipesOutputModel

        // then the recipes are retrieved successfully
        assertEquals(HttpStatus.OK, response.statusCode)
        assertEquals(listOf(recipeInfoMock), body.recipes)
    }

    @Test
    fun `Should throw UserNotFound exception when retrieving the recipes from a non-existing user`() {
        // given a non-existing username
        val username = generateRandomUsername()

        // mock
        whenever(
            runBlocking {
                recipeServiceMock.getUserRecipes(testAuthenticatedUser.user.id, username, null, limit)
            }
        ).thenThrow(UserNotFound(username))

        // when retrieving the user's recipes
        // then the recipes are not retrieved and throws UserNotFound exception
        assertFailsWith<UserNotFound> {
            runBlocking { getUserRecipes(testAuthenticatedUser, username, null, limit) }
        }
    }

    @Test
    fun `Should throw RecipeNotAccessible exception when retrieving the recipes from a private user not followed`() {
        // given a user id and a username
        val username = generateRandomUsername()

        // mock
        whenever(
            runBlocking {
                recipeServiceMock.getUserRecipes(testAuthenticatedUser.user.id, username, null, limit)
            }
        ).thenThrow(RecipeNotAccessible())

        // when retrieving the user's recipes
        // then the recipes are not retrieved and throws RecipeNotAccessible exception
        assertFailsWith<RecipeNotAccessible> {
            runBlocking { getUserRecipes(testAuthenticatedUser, username, null, limit) }
        }
    }
}
