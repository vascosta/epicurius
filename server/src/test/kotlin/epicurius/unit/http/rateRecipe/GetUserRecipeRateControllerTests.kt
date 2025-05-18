package epicurius.unit.http.rateRecipe

import epicurius.domain.exceptions.RecipeNotAccessible
import epicurius.domain.exceptions.RecipeNotFound
import epicurius.domain.exceptions.UserHasNotRated
import epicurius.http.controllers.rateRecipe.models.output.GetUserRecipeRateOutputModel
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import org.mockito.kotlin.whenever
import org.springframework.http.HttpStatus

class GetUserRecipeRateControllerTests : RateRecipeControllerTest() {

    @Test
    fun `Should get user's recipe rate successfully`() {
        // given an authenticated user and a recipe id (RECIPE_ID)

        // mock
        whenever(
            rateRecipeServiceMock.getUserRecipeRate(testAuthenticatedUser.user.id, RECIPE_ID)
        ).thenReturn(RATING_3)

        // when getting the user's recipe rate
        val response = getUserRecipeRate(testAuthenticatedUser, RECIPE_ID)

        // then the response should be 3
        assertEquals(HttpStatus.OK, response.statusCode)
        assertEquals(GetUserRecipeRateOutputModel(RATING_3), response.body)
    }

    @Test
    fun `Should throw RecipeNotFound exception when recipe does not exist`() {
        // given a non-existing recipe id
        val nonExistingRecipeId = 9999

        // mock
        whenever(
            rateRecipeServiceMock.getUserRecipeRate(testAuthenticatedUser.user.id, nonExistingRecipeId)
        ).thenThrow(RecipeNotFound())

        // when getting the user's recipe rate
        // then the recipe rating is not returned and throws RecipeNotFound exception
        assertThrows<RecipeNotFound> {
            getUserRecipeRate(testAuthenticatedUser, nonExistingRecipeId)
        }
    }

    @Test
    fun `Should throw RecipeNotAccessible exception when recipe is from a private user that the user does not follow`() {
        // given a recipe id (RECIPE_ID) from a private user that the user (testAuthenticatedUser) does not follow

        // mock
        whenever(
            rateRecipeServiceMock.getUserRecipeRate(testAuthenticatedUser.user.id, RECIPE_ID)
        ).thenThrow(RecipeNotAccessible())

        // when getting the user's recipe rate
        // then the recipe rating is not returned and throws RecipeNotAccessible exception
        assertThrows<RecipeNotAccessible> {
            getUserRecipeRate(testAuthenticatedUser, RECIPE_ID)
        }
    }

    @Test
    fun `Should throw UserHasNotRated exception when user hasn't rated the recipe`() {
        // given a recipe id (RECIPE_ID) and an authenticated user that hasn't rated the recipe

        // mock
        whenever(
            rateRecipeServiceMock.getUserRecipeRate(testAuthenticatedUser.user.id, RECIPE_ID)
        ).thenThrow(UserHasNotRated(testAuthenticatedUser.user.id, RECIPE_ID))

        // when getting the user's recipe rate
        // then the recipe rating is not returned and throws UserHasNotRated exception
        assertThrows<UserHasNotRated> {
            getUserRecipeRate(testAuthenticatedUser, RECIPE_ID)
        }
    }
}
