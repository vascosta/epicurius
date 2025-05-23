package epicurius.unit.http.rateRecipe

import epicurius.domain.exceptions.AuthorCannotUpdateRating
import epicurius.domain.exceptions.RecipeNotAccessible
import epicurius.domain.exceptions.RecipeNotFound
import epicurius.domain.exceptions.UserHasNotRated
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import org.mockito.kotlin.verify
import org.mockito.kotlin.whenever
import org.springframework.http.HttpStatus

class UpdateRateRecipeControllerTests : RateRecipeControllerTest() {

    @Test
    fun `Should update rate recipe successfully`() {
        // given an authenticated user and a recipe

        // when the user updates the recipe with a rating of 3
        val response = updateRecipeRate(testAuthenticatedUser, RECIPE_ID, RATING_3)

        // then the recipe should be updated successfully
        verify(
            rateRecipeServiceMock
        ).updateRecipeRate(
            testAuthenticatedUser.user.id,
            RECIPE_ID,
            RATING_3
        )

        assertEquals(HttpStatus.NO_CONTENT, response.statusCode)
    }

    @Test
    fun `Should throw RecipeNotFound exception when recipe does not exist`() {
        // given an authenticated user and a recipe that does not exist
        val nonExistingRecipeId = 9999

        // mock
        whenever(
            rateRecipeServiceMock.updateRecipeRate(
                testAuthenticatedUser.user.id,
                nonExistingRecipeId,
                RATING_3
            )
        ).thenThrow(RecipeNotFound())

        // when updating the recipe rate
        // then the recipe rate cannot be updated and throws RecipeNotFound exception
        assertThrows<RecipeNotFound> { updateRecipeRate(testAuthenticatedUser, nonExistingRecipeId, RATING_3) }
    }

    @Test
    fun `Should throw AuthorCannotUpdateRating exception when user is the author and tries to update their own recipe rating`() {
        // given an authenticated user and a recipe

        // mock
        whenever(
            rateRecipeServiceMock.updateRecipeRate(
                testAuthorAuthenticatedUser.user.id,
                RECIPE_ID,
                RATING_3
            )
        ).thenThrow(AuthorCannotUpdateRating())

        // when updating the recipe rate
        // then the recipe rate cannot be updated and throws AuthorCannotUpdateRating exception
        assertThrows<AuthorCannotUpdateRating> { updateRecipeRate(testAuthorAuthenticatedUser, RECIPE_ID, RATING_3) }
    }

    @Test
    fun `Should throw RecipeNotAccessible exception when recipe is from a private user that the user does not follow`() {
        // given an authenticated user and a recipe that is not accessible

        // mock
        whenever(
            rateRecipeServiceMock.updateRecipeRate(
                testAuthenticatedUser.user.id,
                RECIPE_ID,
                RATING_3
            )
        ).thenThrow(RecipeNotAccessible())

        // when updating the recipe rate
        // then the recipe rate cannot be updated and throws RecipeNotAccessible exception
        assertThrows<RecipeNotAccessible> { updateRecipeRate(testAuthenticatedUser, RECIPE_ID, RATING_3) }
    }

    @Test
    fun `Should throw UserHasNotRated exception when user has not rated the recipe`() {
        // given an authenticated user and a recipe that he has not rated

        // mock
        whenever(
            rateRecipeServiceMock.updateRecipeRate(
                testAuthenticatedUser.user.id,
                RECIPE_ID,
                RATING_3
            )
        ).thenThrow(UserHasNotRated(testAuthenticatedUser.user.id, RECIPE_ID))

        // when updating the recipe rate
        // then the recipe rate cannot be updated and throws UserHasNotRated exception
        assertThrows<UserHasNotRated> { updateRecipeRate(testAuthenticatedUser, RECIPE_ID, RATING_3) }
    }
}
