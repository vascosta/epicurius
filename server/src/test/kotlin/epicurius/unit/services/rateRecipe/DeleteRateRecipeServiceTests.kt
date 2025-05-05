package epicurius.unit.services.rateRecipe

import epicurius.domain.exceptions.RecipeNotFound
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.assertThrows
import org.mockito.kotlin.verify
import org.mockito.kotlin.whenever
import kotlin.test.Test

class DeleteRateRecipeServiceTests : RateRecipeServiceTest() {

    @Test
    fun `Should delete recipe rate successfully`() {
        // given a user (USER_ID) who has rated a recipe (RECIPE_ID)

        // mock
        whenever(jdbiRecipeRepositoryMock.getRecipeById(RECIPE_ID)).thenReturn(jdbiRecipeModel)
        whenever(jdbiUserRepositoryMock.checkUserVisibility(AUTHOR_USERNAME, USERNAME)).thenReturn(true)
        whenever(jdbiRateRecipeRepositoryMock.checkIfUserAlreadyRated(USER_ID, RECIPE_ID)).thenReturn(true)

        // when deleting the recipe rate
        deleteRecipeRate(USER_ID, USERNAME, RECIPE_ID)

        // then the recipe rate is deleted successfully
        verify(jdbiRateRecipeRepositoryMock).deleteRecipeRate(RECIPE_ID, USER_ID)
    }

    @Test
    fun `Should throw RecipeNotFound exception when recipe does not exist`() {
        // given a user (USER_ID) and a recipe (RECIPE_ID) that does not exist
        val nonExistingRecipeId = 9999

        // mock
        whenever(jdbiRecipeRepositoryMock.getRecipeById(nonExistingRecipeId)).thenReturn(null)

        // when deleting the recipe rate
        val exception = assertThrows<RecipeNotFound> {
            deleteRecipeRate(USER_ID, USERNAME, nonExistingRecipeId)
        }

        // then the exception is thrown
        assertEquals(RecipeNotFound().message, exception.message)
    }

    @Test
    fun `Should throw AuthorCannotDeleteRating exception when user is the author and tries to delete their own recipe rating`() {
        // given a user (USER_ID) and a recipe (RECIPE_ID) that belongs to him

        // mock
        whenever(jdbiRecipeRepositoryMock.getRecipeById(RECIPE_ID)).thenReturn(jdbiRecipeModel)
        whenever(jdbiUserRepositoryMock.checkUserVisibility(AUTHOR_USERNAME, AUTHOR_USERNAME)).thenReturn(true)

        // when deleting the recipe rate
        val exception = assertThrows<epicurius.domain.exceptions.AuthorCannotDeleteRating> {
            deleteRecipeRate(AUTHOR_ID, AUTHOR_USERNAME, RECIPE_ID)
        }

        // then the exception is thrown
        assertEquals(epicurius.domain.exceptions.AuthorCannotDeleteRating().message, exception.message)
    }

    @Test
    fun `Should throw UserHasNotRated exception when user has not rated the recipe`() {
        // given a user (USER_ID) and a recipe (RECIPE_ID) that he has not rated

        // mock
        whenever(jdbiRecipeRepositoryMock.getRecipeById(RECIPE_ID)).thenReturn(jdbiRecipeModel)
        whenever(jdbiUserRepositoryMock.checkUserVisibility(AUTHOR_USERNAME, USERNAME)).thenReturn(true)
        whenever(jdbiRateRecipeRepositoryMock.checkIfUserAlreadyRated(USER_ID, RECIPE_ID)).thenReturn(false)

        // when deleting the recipe rate
        val exception = assertThrows<epicurius.domain.exceptions.UserHasNotRated> {
            deleteRecipeRate(USER_ID, USERNAME, RECIPE_ID)
        }

        // then the exception is thrown
        assertEquals(epicurius.domain.exceptions.UserHasNotRated(USER_ID, RECIPE_ID).message, exception.message)
    }

    @Test
    fun `Should throw RecipeNotAccessible exception when recipe is from a private user that the user does not follow`() {
        // given a user (USER_ID) and a recipe (RECIPE_ID) that is not accessible

        // mock
        whenever(jdbiRecipeRepositoryMock.getRecipeById(RECIPE_ID)).thenReturn(jdbiRecipeModel)
        whenever(jdbiUserRepositoryMock.checkUserVisibility(AUTHOR_USERNAME, USERNAME)).thenReturn(false)

        // when deleting the recipe rate
        val exception = assertThrows<epicurius.domain.exceptions.RecipeNotAccessible> {
            deleteRecipeRate(USER_ID, USERNAME, RECIPE_ID)
        }

        // then the exception is thrown
        assertEquals(epicurius.domain.exceptions.RecipeNotAccessible().message, exception.message)
    }
}
