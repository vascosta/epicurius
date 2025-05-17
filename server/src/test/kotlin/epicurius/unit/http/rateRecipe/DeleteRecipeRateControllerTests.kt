package epicurius.unit.http.rateRecipe

import epicurius.domain.exceptions.AuthorCannotDeleteRating
import epicurius.domain.exceptions.RecipeNotAccessible
import epicurius.domain.exceptions.RecipeNotFound
import epicurius.domain.exceptions.UserHasNotRated
import epicurius.unit.http.recipe.RecipeHttpTest
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import org.mockito.kotlin.verify
import org.mockito.kotlin.whenever
import org.springframework.http.HttpStatus
import kotlin.test.assertEquals

class DeleteRecipeRateControllerTests : RecipeHttpTest() {

    @Test
    fun `Should delete recipe rate successfully`() {
        // given an authenticated user and a recipe

        // when the user deletes the recipe
        val response = deleteRecipeRate(testAuthenticatedUser, RECIPE_ID)

        // then the recipe should be deleted successfully
        verify(
            rateRecipeServiceMock
        ).deleteRecipeRate(
            testAuthenticatedUser.user.id,
            RECIPE_ID
        )

        assertEquals(HttpStatus.NO_CONTENT, response.statusCode)
    }

    @Test
    fun `Should throw RecipeNotFound exception when recipe does not exist`() {
        // given an authenticated user and a recipe that does not exist
        val nonExistingRecipeId = 9999

        // mock
        whenever(
            rateRecipeServiceMock.deleteRecipeRate(
                testAuthenticatedUser.user.id,
                nonExistingRecipeId
            )
        ).thenThrow(RecipeNotFound())

        // when deleting the recipe rate
        // then the recipe rate cannot be deleted and throws RecipeNotFound exception
        assertThrows<RecipeNotFound> { deleteRecipeRate(testAuthenticatedUser, nonExistingRecipeId) }
    }

    @Test
    fun `Should throw AuthorCannotDeleteRating exception when user is the author and tries to delete their own recipe rating`() {
        // given an authenticated user and a recipe

        // mock
        whenever(
            rateRecipeServiceMock.deleteRecipeRate(
                testAuthenticatedUser.user.id,
                RECIPE_ID
            )
        ).thenThrow(AuthorCannotDeleteRating())

        // when deleting the recipe rate
        // then the recipe rate cannot be deleted and throws AuthorCannotDeleteRating exception
        assertThrows<AuthorCannotDeleteRating> { deleteRecipeRate(testAuthenticatedUser, RECIPE_ID) }
    }

    @Test
    fun `Should throw UserHasNotRated exception when user has not rated the recipe`() {
        // given an authenticated user and a recipe that he has not rated

        // mock
        whenever(
            rateRecipeServiceMock.deleteRecipeRate(
                testAuthenticatedUser.user.id,
                RECIPE_ID
            )
        ).thenThrow(UserHasNotRated(testAuthenticatedUser.user.id, RECIPE_ID))

        // when deleting the recipe rate
        // then the recipe rate cannot be deleted and throws UserHasNotRated exception
        assertThrows<UserHasNotRated> { deleteRecipeRate(testAuthenticatedUser, RECIPE_ID) }
    }

    @Test
    fun `Should throw RecipeNotAccessible exception when recipe is from a private user that the user does not follow`() {
        // given an authenticated user and a recipe that is not accessible

        // mock
        whenever(
            rateRecipeServiceMock.deleteRecipeRate(
                testAuthenticatedUser.user.id,
                RECIPE_ID
            )
        ).thenThrow(RecipeNotAccessible())

        // when deleting the recipe rate
        // then the recipe rate cannot be deleted and throws RecipeNotAccessible exception
        assertThrows<RecipeNotAccessible> { deleteRecipeRate(testAuthenticatedUser, RECIPE_ID) }
    }
}
