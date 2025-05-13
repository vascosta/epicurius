package epicurius.unit.http.recipe

import epicurius.domain.exceptions.NotTheRecipeAuthor
import epicurius.domain.exceptions.RecipeNotFound
import epicurius.domain.user.AuthenticatedUser
import epicurius.domain.user.User
import org.mockito.kotlin.verify
import org.mockito.kotlin.whenever
import org.springframework.http.HttpStatus
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith

class DeleteRecipeControllerTests : RecipeHttpTest() {

    @Test
    fun `Should delete a recipe successfully`() {
        // given a user and a recipe id (testAuthenticatedUser, RECIPE_ID)

        // when deleting the recipe
        val response = deleteRecipe(testAuthenticatedUser, RECIPE_ID)

        // then the recipe is deleted successfully
        verify(recipeServiceMock).deleteRecipe(testAuthenticatedUser.user.id, RECIPE_ID)
        assertEquals(HttpStatus.NO_CONTENT, response.statusCode)
    }

    @Test
    fun `Should throw RecipeNotFound when deleting a non-existing recipe`() {
        // given a non-existing recipe id
        val nonExistingRecipeId = 9999

        // mock
        whenever(recipeServiceMock.deleteRecipe(testAuthenticatedUser.user.id, nonExistingRecipeId)).thenThrow(RecipeNotFound())

        // when deleting the recipe
        // then the recipe is not deleted and throws RecipeNotFound exception
        assertFailsWith<RecipeNotFound> { deleteRecipe(testAuthenticatedUser, nonExistingRecipeId) }
    }

    @Test
    fun `Should throw NotTheAuthor exception when deleting a recipe that does not belong to the user`() {
        // given a user id and a recipe id (RECIPE_ID) that does not belong to him
        val notTheAuthorUser = AuthenticatedUser(
            User(9999, "", "", "", "", "", false, emptyList(), emptyList(), ""),
            ""
        )

        // mock
        whenever(recipeServiceMock.deleteRecipe(notTheAuthorUser.user.id, RECIPE_ID)).thenThrow(NotTheRecipeAuthor())

        // when deleting the recipe
        // then the recipe is not deleted and throws NotTheAuthor exception
        assertFailsWith<NotTheRecipeAuthor> { deleteRecipe(notTheAuthorUser, RECIPE_ID) }
    }
}
