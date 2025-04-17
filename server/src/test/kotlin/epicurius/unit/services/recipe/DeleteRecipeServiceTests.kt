package epicurius.unit.services.recipe

import epicurius.domain.exceptions.NotTheAuthor
import epicurius.domain.exceptions.RecipeNotFound
import kotlinx.coroutines.runBlocking
import org.mockito.kotlin.verify
import org.mockito.kotlin.whenever
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith

class DeleteRecipeServiceTests : RecipeServiceTest() {

    @Test
    fun `Should delete a recipe successfully`() {
        // given a user id and a recipe id (AUTHOR_ID, RECIPE_ID)

        // mock
        whenever(jdbiRecipeRepositoryMock.getRecipe(RECIPE_ID)).thenReturn(jdbiRecipeModel)

        // when deleting the recipe
        recipeService.deleteRecipe(AUTHOR_ID, RECIPE_ID)
        verify(jdbiRecipeRepositoryMock).deleteRecipe(RECIPE_ID)
        verify(firestoreRecipeRepositoryMock).deleteRecipe(RECIPE_ID)

        // then the recipe is deleted successfully
    }

    @Test
    fun `Should throw RecipeNotFound when deleting a non-existing recipe`() {
        // given a non-existing recipe id
        val nonExistingRecipeId = 9999

        // mock
        whenever(jdbiRecipeRepositoryMock.getRecipe(nonExistingRecipeId)).thenReturn(null)

        // when deleting the recipe
        val exception = assertFailsWith<RecipeNotFound> {
            recipeService.deleteRecipe(AUTHOR_ID, nonExistingRecipeId)
        }

        // then an exception is thrown
        assertEquals(RecipeNotFound().message, exception.message)
    }

    @Test
    fun `Should throw NotTheAuthor exception when deleting a recipe that does not belong to the user`() {
        // given a user id and a recipe id (RECIPE_ID) that does not belong to him
        val userId = 9999

        // mock
        whenever(jdbiRecipeRepositoryMock.getRecipe(RECIPE_ID)).thenReturn(jdbiRecipeModel)

        // when updating the recipe
        val exception = assertFailsWith<NotTheAuthor> {
            runBlocking { recipeService.deleteRecipe(userId, RECIPE_ID) }
        }

        // then an exception is thrown
        assertEquals(NotTheAuthor().message, exception.message)
    }
}
