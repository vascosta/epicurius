package epicurius.unit.services.recipe

import epicurius.domain.exceptions.RecipeNotAccessible
import epicurius.domain.exceptions.UserNotFound
import epicurius.domain.picture.PictureDomain.Companion.RECIPES_FOLDER
import epicurius.repository.jdbi.recipe.models.JdbiRecipeInfo
import epicurius.utils.generateRandomUsername
import kotlinx.coroutines.runBlocking
import org.mockito.kotlin.whenever
import kotlin.test.Test
import kotlin.test.assertFailsWith
import kotlin.test.assertTrue

class GetUserRecipesServiceTests : RecipeServiceTest() {

    private val limit = 1

    @Test
    fun `Should retrieve the user's recipes successfully`() {
        // given a user id (AUTHOR_ID)

        // mock
        val jdbiRecipeInfoMock = JdbiRecipeInfo(
            id = RECIPE_ID,
            name = createRecipeInputInfo.name,
            authorUsername = authorUsername,
            rating = 0.0,
            cuisine = createRecipeInputInfo.cuisine,
            mealType = createRecipeInputInfo.mealType,
            preparationTime = createRecipeInputInfo.preparationTime,
            servings = createRecipeInputInfo.servings,
            picturesNames = listOf(testPicture.name),
        )
        whenever(jdbiRecipeRepositoryMock.getUserRecipes(AUTHOR_ID, null, limit))
            .thenReturn(listOf(jdbiRecipeInfoMock))
        whenever(pictureRepositoryMock.getPicture(testPicture.name, RECIPES_FOLDER)).thenReturn(testPicture.bytes)

        // when retrieving the user's recipes
        val recipes = runBlocking { getUserRecipes(AUTHOR_ID, null, null, limit) }

        // then the recipes are retrieved successfully
        assertTrue(recipes.contains(jdbiRecipeInfoMock.toRecipeInfo(testPicture.bytes)))
    }

    @Test
    fun `Should retrieve another user's recipes successfully`() {
        // given a user id (AUTHOR_ID) and a username
        val username = generateRandomUsername()

        // mock
        val jdbiRecipeInfoMock = JdbiRecipeInfo(
            id = RECIPE_ID,
            name = createRecipeInputInfo.name,
            authorUsername = authorUsername,
            rating = 0.0,
            cuisine = createRecipeInputInfo.cuisine,
            mealType = createRecipeInputInfo.mealType,
            preparationTime = createRecipeInputInfo.preparationTime,
            servings = createRecipeInputInfo.servings,
            picturesNames = listOf(testPicture.name),
        )
        whenever(jdbiUserRepositoryMock.getUser(username))
            .thenReturn(author.copy(id = USER_ID, name = username))
        whenever(jdbiUserRepositoryMock.checkUserVisibility(username, AUTHOR_ID)).thenReturn(true)
        whenever(jdbiRecipeRepositoryMock.getUserRecipes(USER_ID, null, limit))
            .thenReturn(listOf(jdbiRecipeInfoMock))
        whenever(pictureRepositoryMock.getPicture(testPicture.name, RECIPES_FOLDER)).thenReturn(testPicture.bytes)

        // when retrieving the user's recipes
        val recipes = runBlocking { getUserRecipes(AUTHOR_ID, username, null, limit) }

        // then the recipes are retrieved successfully
        assertTrue(recipes.contains(jdbiRecipeInfoMock.toRecipeInfo(testPicture.bytes)))
    }

    @Test
    fun `Should throw UserNotFound exception when retrieving the recipes from a non-existing user`() {
        // given a non-existing username
        val username = generateRandomUsername()

        // mock
        whenever(jdbiUserRepositoryMock.getUser(username)).thenReturn(null)

        // when retrieving the user's recipes
        // then the recipes are not retrieved and throws UserNotFound exception
        assertFailsWith<UserNotFound> { runBlocking { getUserRecipes(AUTHOR_ID, username, null, limit) } }
    }

    @Test
    fun `Should throw RecipeNotAccessible exception when retrieving the recipes from a private user not followed`() {
        // given a user id (AUTHOR_ID) and a username
        val username = generateRandomUsername()

        // mock
        whenever(jdbiUserRepositoryMock.checkUserVisibility(username, AUTHOR_ID)).thenReturn(false)

        // when retrieving the user's recipes
        // then the recipes are not retrieved and throws RecipeNotAccessible exception
        assertFailsWith<RecipeNotAccessible> { runBlocking { getUserRecipes(AUTHOR_ID, username, null, limit) } }
    }
}
