package epicurius.unit.http.rateRecipe

import epicurius.domain.exceptions.RecipeNotAccessible
import epicurius.domain.exceptions.RecipeNotFound
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import org.mockito.kotlin.verify
import org.mockito.kotlin.whenever
import org.springframework.http.HttpStatus

class GetRecipeRateHttpTests : RateRecipeHttpTest() {

    @Test
    fun `Should get recipe rate successfully`() {
        // given an authenticated user and a recipe
        // when the user gets the recipe rate
        val response = getRecipeRate(testAuthenticatedUser, RECIPE_ID)

        // then the recipe rate should be returned successfully
        verify(rateRecipeServiceMock).getRecipeRate(testAuthenticatedUser.user.name, RECIPE_ID)
        assertEquals(HttpStatus.OK, response.statusCode)
    }

    @Test
    fun `Should throw RecipeNotFound exception when recipe does not exist`() {
        // given an authenticated user and a recipe that does not exist
        val nonExistingRecipeId = 9999

        // mock
        whenever(
            rateRecipeServiceMock.getRecipeRate(testAuthenticatedUser.user.name, nonExistingRecipeId)
        ).thenThrow(RecipeNotFound())

        // when getting the recipe rate
        // then RecipeNotFound exception is thrown
        assertThrows<RecipeNotFound> { getRecipeRate(testAuthenticatedUser, nonExistingRecipeId) }
    }

    @Test
    fun `Should throw RecipeNotAccessible exception when user does not have access to the recipe`() {
        // given an authenticated user and a recipe that is from a private user

        // mock
        whenever(
            rateRecipeServiceMock.getRecipeRate(testAuthenticatedUser.user.name, RECIPE_ID)
        ).thenThrow(RecipeNotAccessible())

        // when getting the recipe rate
        // then RecipeNotFound exception is thrown
        assertThrows<RecipeNotAccessible> { getRecipeRate(testAuthenticatedUser, RECIPE_ID) }
    }
}
