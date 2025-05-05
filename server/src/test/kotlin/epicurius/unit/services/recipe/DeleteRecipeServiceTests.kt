package epicurius.unit.services.recipe

import epicurius.domain.exceptions.NotTheAuthor
import epicurius.domain.exceptions.RecipeNotFound
import org.mockito.kotlin.verify
import org.mockito.kotlin.whenever
import kotlin.test.Test
import kotlin.test.assertFailsWith

class DeleteRecipeServiceTests : RecipeServiceTest() {

    @Test
    fun `Should delete a recipe successfully`() {
        // given a user id and a recipe id (AUTHOR_ID, RECIPE_ID)

        // mock
        whenever(jdbiRecipeRepositoryMock.getRecipeById(RECIPE_ID)).thenReturn(jdbiRecipeModel)

        // when deleting the recipe
        deleteRecipe(AUTHOR_ID, RECIPE_ID)

        // then the recipe is deleted successfully
        verify(jdbiRecipeRepositoryMock).deleteRecipe(RECIPE_ID)
        verify(firestoreRecipeRepositoryMock).deleteRecipe(RECIPE_ID)
    }

    @Test
    fun `Should throw RecipeNotFound exception when deleting a non-existing recipe`() {
        // given a non-existing recipe id
        val nonExistingRecipeId = 9999

        // mock
        whenever(jdbiRecipeRepositoryMock.getRecipeById(nonExistingRecipeId)).thenReturn(null)

        // when deleting the recipe
        // then the recipe is not deleted and throws RecipeNotFound exception
        assertFailsWith<RecipeNotFound> { deleteRecipe(AUTHOR_ID, nonExistingRecipeId) }
    }

    @Test
    fun `Should throw NotTheAuthor exception when deleting a recipe that does not belong to the user`() {
        // given a user id and a recipe id (RECIPE_ID) that does not belong to him
        val userId = 9999

        // mock
        whenever(jdbiRecipeRepositoryMock.getRecipeById(RECIPE_ID)).thenReturn(jdbiRecipeModel)

        // when deleting the recipe
        // then the recipe is not deleted and throws NotTheAuthor exception
        assertFailsWith<NotTheAuthor> { deleteRecipe(userId, RECIPE_ID) }
    }
}
