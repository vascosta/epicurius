package epicurius.unit.http.rateRecipe

import epicurius.domain.exceptions.RecipeNotAccessible
import epicurius.domain.exceptions.RecipeNotFound
import epicurius.http.controllers.rateRecipe.models.output.GetRecipeRatingOutputModel
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import org.mockito.kotlin.whenever
import org.springframework.http.HttpStatus

class GetRecipeRatingControllerTests : RateRecipeControllerTest() {

    @Test
    fun `Should get recipe rate successfully`() {
        // given an authenticated user and a recipe

        // mock
        whenever(
            rateRecipeServiceMock.getRecipeRating(testAuthenticatedUser.user.id, RECIPE_ID)
        ).thenReturn(RATING_5.toDouble())

        // when the user gets the recipe rating
        val response = getRecipeRating(testAuthenticatedUser, RECIPE_ID)

        // then the recipe rating should be returned successfully
        assertEquals(HttpStatus.OK, response.statusCode)
        assertEquals(
            GetRecipeRatingOutputModel(RATING_5.toDouble()),
            response.body
        )
    }

    @Test
    fun `Should throw RecipeNotFound exception when recipe does not exist`() {
        // given an authenticated user and a recipe that does not exist
        val nonExistingRecipeId = 9999

        // mock
        whenever(
            rateRecipeServiceMock.getRecipeRating(testAuthenticatedUser.user.id, nonExistingRecipeId)
        ).thenThrow(RecipeNotFound())

        // when getting the recipe rating
        // then the recipe rating is not returned and throws RecipeNotFound exception
        assertThrows<RecipeNotFound> { getRecipeRating(testAuthenticatedUser, nonExistingRecipeId) }
    }

    @Test
    fun `Should throw RecipeNotAccessible exception when user does not have access to the recipe`() {
        // given an authenticated user and a recipe that is from a private user

        // mock
        whenever(
            rateRecipeServiceMock.getRecipeRating(testAuthenticatedUser.user.id, RECIPE_ID)
        ).thenThrow(RecipeNotAccessible())

        // when getting the recipe rating
        // then the recipe rating is not returned and throws RecipeNotAccessible exception
        assertThrows<RecipeNotAccessible> { getRecipeRating(testAuthenticatedUser, RECIPE_ID) }
    }
}
