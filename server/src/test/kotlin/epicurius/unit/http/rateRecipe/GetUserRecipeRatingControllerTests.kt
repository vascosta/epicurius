package epicurius.unit.http.rateRecipe

import epicurius.domain.exceptions.RecipeNotAccessible
import epicurius.domain.exceptions.RecipeNotFound
import epicurius.domain.exceptions.UserHasNotRated
import epicurius.http.controllers.rateRecipe.models.output.GetUserRecipeRatingOutputModel
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import org.mockito.kotlin.whenever
import org.springframework.http.HttpStatus
import kotlin.test.assertNull

class GetUserRecipeRatingControllerTests : RateRecipeControllerTest() {

    @Test
    fun `Should get user's recipe rating successfully`() {
        // given an authenticated user and a recipe id (RECIPE_ID)

        // mock
        whenever(
            rateRecipeServiceMock.getUserRecipeRating(testAuthenticatedUser.user.id, RECIPE_ID)
        ).thenReturn(RATING_3)

        // when getting the user's recipe rating
        val response = getUserRecipeRating(testAuthenticatedUser, RECIPE_ID)

        // then the response should be 3
        assertEquals(HttpStatus.OK, response.statusCode)
        assertEquals(GetUserRecipeRatingOutputModel(RATING_3), response.body)
    }

    @Test
    fun `Should throw RecipeNotFound exception when recipe does not exist`() {
        // given a non-existing recipe id
        val nonExistingRecipeId = 9999

        // mock
        whenever(
            rateRecipeServiceMock.getUserRecipeRating(testAuthenticatedUser.user.id, nonExistingRecipeId)
        ).thenThrow(RecipeNotFound())

        // when getting the user's recipe rating
        // then the user's recipe rating is not returned and throws RecipeNotFound exception
        assertThrows<RecipeNotFound> {
            getUserRecipeRating(testAuthenticatedUser, nonExistingRecipeId)
        }
    }

    @Test
    fun `Should throw RecipeNotAccessible exception when recipe is from a private user that the user does not follow`() {
        // given a recipe id (RECIPE_ID) from a private user that the user (testAuthenticatedUser) does not follow

        // mock
        whenever(
            rateRecipeServiceMock.getUserRecipeRating(testAuthenticatedUser.user.id, RECIPE_ID)
        ).thenThrow(RecipeNotAccessible())

        // when getting the user's recipe rating
        // then the user's recipe rating is not returned and throws RecipeNotAccessible exception
        assertThrows<RecipeNotAccessible> {
            getUserRecipeRating(testAuthenticatedUser, RECIPE_ID)
        }
    }

    @Test
    fun `Should return null when user hasn't rated the recipe`() {
        // given a recipe id (RECIPE_ID) and an authenticated user that hasn't rated the recipe

        // mock
        whenever(
            rateRecipeServiceMock.getUserRecipeRating(testAuthenticatedUser.user.id, RECIPE_ID)
        ).thenReturn(null)

        // when getting the user's recipe rating
        val response = getUserRecipeRating(testAuthenticatedUser, RECIPE_ID)
        val body = response.body as GetUserRecipeRatingOutputModel

        // then the user's recipe rating is not returned and returns null
        assertEquals(HttpStatus.OK, response.statusCode)
        assertNull(body.rating)
    }
}
