package epicurius.unit.http.recipe

import epicurius.domain.exceptions.NotTheAuthor
import epicurius.domain.exceptions.RecipeNotFound
import epicurius.domain.user.AuthenticatedUser
import epicurius.domain.user.User
import org.mockito.kotlin.verify
import org.mockito.kotlin.whenever
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith

class DeleteRecipeHttpTests : RecipeHttpTest() {

    @Test
    fun `Should delete a recipe successfully`() {
        // given a user and a recipe id (testAuthenticatedUser, RECIPE_ID)

        // when deleting the recipe
        deleteRecipe(testAuthenticatedUser, RECIPE_ID)
        verify(recipeServiceMock).deleteRecipe(testAuthenticatedUser.user.id, RECIPE_ID)

        // then the recipe is deleted successfully
    }

    @Test
    fun `Should throw RecipeNotFound when deleting a non-existing recipe`() {
        // given a non-existing recipe id
        val recipeId = 9999

        // mock
        whenever(recipeServiceMock.deleteRecipe(testAuthenticatedUser.user.id, recipeId)).thenThrow(RecipeNotFound())

        // when deleting the recipe
        val exception = assertFailsWith<RecipeNotFound> { deleteRecipe(testAuthenticatedUser, recipeId) }

        // then an exception is thrown
        assertEquals(RecipeNotFound().message, exception.message)
    }

    @Test
    fun `Should throw NotTheAuthor exception when deleting a recipe that does not belong to the user`() {
        // given a user id and a recipe id (RECIPE_ID) that does not belong to him
        val notTheAuthorUser = AuthenticatedUser(
            User(9999, "", "", "", "", "", false, emptySet(), emptySet(), ""),
            ""
        )

        // mock
        whenever(recipeServiceMock.deleteRecipe(notTheAuthorUser.user.id, RECIPE_ID)).thenThrow(NotTheAuthor())

        // when deleting the recipe
        val exception = assertFailsWith<NotTheAuthor> { deleteRecipe(notTheAuthorUser, RECIPE_ID) }

        // then an exception is thrown
        assertEquals(NotTheAuthor().message, exception.message)
    }
}
