package epicurius.unit.http.rateRecipe

import epicurius.domain.exceptions.AuthorCannotRateOwnRecipe
import epicurius.domain.exceptions.RecipeNotAccessible
import epicurius.domain.exceptions.RecipeNotFound
import epicurius.domain.exceptions.UserAlreadyRated
import epicurius.http.controllers.rateRecipe.models.output.GetRecipeRateOutputModel
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import org.mockito.kotlin.verify
import org.mockito.kotlin.whenever
import org.springframework.http.HttpStatus
import kotlin.test.assertEquals

class RateRecipeControllerTests : RateRecipeControllerTest() {

    @Test
    fun `Should rate recipe successfully`() {
        // given an authenticated user and a recipe

        // when the user rates the recipe with a rating of 5
        val response = rateRecipe(testAuthenticatedUser, RECIPE_ID, RATING_5)

        // then the recipe should be rated successfully
        verify(
            rateRecipeServiceMock
        ).rateRecipe(
            testAuthenticatedUser.user.id,
            RECIPE_ID,
            RATING_5
        )

        assertEquals(HttpStatus.NO_CONTENT, response.statusCode)

        // mock
        whenever(
            rateRecipeServiceMock.getRecipeRate(testAuthenticatedUser.user.id, RECIPE_ID)
        ).thenReturn(RATING_5.toDouble())

        // when getting the recipe rate
        val rating = getRecipeRate(testAuthenticatedUser, RECIPE_ID)
        assertEquals(HttpStatus.OK, rating.statusCode)
        assertEquals(GetRecipeRateOutputModel(RATING_5.toDouble()), rating.body)
    }

    @Test
    fun `Should throw RecipeNotFound exception when recipe does not exist`() {
        // given an authenticated user and a recipe that does not exist
        val nonExistingRecipeId = 9999

        // mock
        whenever(
            rateRecipeServiceMock.rateRecipe(
                testAuthenticatedUser.user.id,
                nonExistingRecipeId,
                RATING_5
            )
        ).thenThrow(RecipeNotFound())

        // when rating a non-existing recipe
        // then the recipe cannot be rated and throws RecipeNotFound exception
        assertThrows<RecipeNotFound> { rateRecipe(testAuthenticatedUser, nonExistingRecipeId, RATING_5) }
    }

    @Test
    fun `Should throw AuthorCannotRateOwnRecipe exception when user is the author and tries to rate their own recipe`() {
        // given an authenticated user and a recipe that belongs to him

        // mock
        whenever(
            rateRecipeServiceMock.rateRecipe(
                testAuthorAuthenticatedUser.user.id,
                RECIPE_ID,
                RATING_5
            )
        ).thenThrow(AuthorCannotRateOwnRecipe())

        // when rating the recipe
        // then the recipe cannot be rated and throws AuthorCannotRateOwnRecipe exception
        assertThrows<AuthorCannotRateOwnRecipe> { rateRecipe(testAuthorAuthenticatedUser, RECIPE_ID, RATING_5) }
    }

    @Test
    fun `Should throw RecipeNotAccessible exception when recipe is from a private user that the user does not follow`() {
        // given an authenticated user and a recipe that belongs to a private user that the user does not follow

        // mock
        whenever(
            rateRecipeServiceMock.rateRecipe(
                testAuthenticatedUser.user.id,
                RECIPE_ID,
                RATING_5
            )
        ).thenThrow(RecipeNotAccessible())

        // when rating the recipe
        // then the recipe cannot be rated and throws RecipeNotAccessible exception
        assertThrows<RecipeNotAccessible> { rateRecipe(testAuthenticatedUser, RECIPE_ID, RATING_5) }
    }

    @Test
    fun `Should throw UserAlreadyRated exception when user has already rated the recipe`() {
        // given an authenticated user and a recipe that the user has already rated

        // mock
        whenever(
            rateRecipeServiceMock.rateRecipe(
                testAuthenticatedUser.user.id,
                RECIPE_ID,
                RATING_5
            )
        ).thenThrow(UserAlreadyRated(testAuthenticatedUser.user.id, RECIPE_ID))

        // when rating the recipe
        // then the recipe cannot be rated and throws UserAlreadyRated exception
        assertThrows<UserAlreadyRated> { rateRecipe(testAuthenticatedUser, RECIPE_ID, RATING_5) }
    }
}
