package epicurius.unit.http.rateRecipe

import epicurius.domain.exceptions.AuthorCannotUpdateRating
import epicurius.domain.exceptions.RecipeNotAccessible
import epicurius.domain.exceptions.RecipeNotFound
import epicurius.domain.exceptions.UserHasNotRated
import epicurius.http.controllers.rateRecipe.models.output.UpdateUserRecipeRatingOutputModel
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import org.mockito.kotlin.whenever
import org.springframework.http.HttpStatus

class UpdateUserRecipeRatingControllerTests : RateRecipeControllerTest() {

    @Test
    fun `Should update user's recipe rating successfully`() {
        // given an authenticated user and a recipe

        // mock
        whenever(
            rateRecipeServiceMock.updateUserRecipeRating(
                testAuthenticatedUser.user.id,
                RECIPE_ID,
                RATING_3
            )
        ).thenReturn(RATING_3.toDouble())

        // when the user updates the recipe rating with a rating of 3
        val response = updateUserRecipeRating(testAuthenticatedUser, RECIPE_ID, RATING_3)

        // then the user's recipe rating should be updated successfully
        assertEquals(HttpStatus.OK, response.statusCode)
        assertEquals(
            UpdateUserRecipeRatingOutputModel(RATING_3.toDouble()),
            response.body
        )
    }

    @Test
    fun `Should throw RecipeNotFound exception when recipe does not exist`() {
        // given an authenticated user and a recipe that does not exist
        val nonExistingRecipeId = 9999

        // mock
        whenever(
            rateRecipeServiceMock.updateUserRecipeRating(
                testAuthenticatedUser.user.id,
                nonExistingRecipeId,
                RATING_3
            )
        ).thenThrow(RecipeNotFound())

        // when updating the user's recipe rating
        // then the user's recipe rating cannot be updated and throws RecipeNotFound exception
        assertThrows<RecipeNotFound> { updateUserRecipeRating(testAuthenticatedUser, nonExistingRecipeId, RATING_3) }
    }

    @Test
    fun `Should throw AuthorCannotUpdateRating exception when user is the author and tries to update their own recipe rating`() {
        // given an authenticated user and a recipe

        // mock
        whenever(
            rateRecipeServiceMock.updateUserRecipeRating(
                testAuthorAuthenticatedUser.user.id,
                RECIPE_ID,
                RATING_3
            )
        ).thenThrow(AuthorCannotUpdateRating())

        // when updating the user's recipe rating
        // then the user's recipe rating cannot be updated and throws AuthorCannotUpdateRating exception
        assertThrows<AuthorCannotUpdateRating> { updateUserRecipeRating(testAuthorAuthenticatedUser, RECIPE_ID, RATING_3) }
    }

    @Test
    fun `Should throw RecipeNotAccessible exception when recipe is from a private user that the user does not follow`() {
        // given an authenticated user and a recipe that is not accessible

        // mock
        whenever(
            rateRecipeServiceMock.updateUserRecipeRating(
                testAuthenticatedUser.user.id,
                RECIPE_ID,
                RATING_3
            )
        ).thenThrow(RecipeNotAccessible())

        // when updating the user's recipe rating
        // then the user's recipe rating cannot be updated and throws RecipeNotAccessible exception
        assertThrows<RecipeNotAccessible> { updateUserRecipeRating(testAuthenticatedUser, RECIPE_ID, RATING_3) }
    }

    @Test
    fun `Should throw UserHasNotRated exception when user has not rated the recipe`() {
        // given an authenticated user and a recipe that he has not rated

        // mock
        whenever(
            rateRecipeServiceMock.updateUserRecipeRating(
                testAuthenticatedUser.user.id,
                RECIPE_ID,
                RATING_3
            )
        ).thenThrow(UserHasNotRated(testAuthenticatedUser.user.id, RECIPE_ID))

        // when updating the user's recipe rating
        // then the user's recipe rating cannot be updated and throws UserHasNotRated exception
        assertThrows<UserHasNotRated> { updateUserRecipeRating(testAuthenticatedUser, RECIPE_ID, RATING_3) }
    }
}
