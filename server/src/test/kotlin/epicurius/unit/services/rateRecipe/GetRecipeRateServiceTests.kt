package epicurius.unit.services.rateRecipe

import epicurius.domain.exceptions.RecipeNotAccessible
import epicurius.domain.exceptions.RecipeNotFound
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import org.mockito.kotlin.whenever
import kotlin.test.assertEquals

class GetRecipeRateServiceTests : RateRecipeServiceTest() {

    @Test
    fun `Should get recipe rating successfully`() {
        // given a user id and a recipe id (USERNAME, RECIPE_ID)

        // mock
        whenever(jdbiRecipeRepositoryMock.getRecipeById(RECIPE_ID)).thenReturn(jdbiRecipeModel)
        whenever(jdbiUserRepositoryMock.checkUserVisibility(AUTHOR_USERNAME, USERNAME)).thenReturn(true)
        whenever(jdbiRateRecipeRepositoryMock.getRecipeRate(RECIPE_ID)).thenReturn(RATING_5.toDouble())

        // when getting the recipe rating
        val rate = getRecipeRate(USERNAME, RECIPE_ID)

        // then the recipe rating is returned successfully
        assert(rate == RATING_5.toDouble())
    }

    @Test
    fun `Should throw RecipeNotFound exception when getting rating of a non-existing recipe`() {
        // given a non-existing recipe id
        val nonExistingRecipeId = 9999

        // mock
        whenever(jdbiRecipeRepositoryMock.getRecipeById(nonExistingRecipeId)).thenReturn(null)

        // when getting the recipe rating
        val exception = assertThrows<RecipeNotFound> { getRecipeRate(USERNAME, nonExistingRecipeId) }

        // then the recipe rating is not returned and throws RecipeNotFound exception
        assertEquals(RecipeNotFound().message, exception.message)
    }

    @Test
    fun `Should throw RecipeNotAccessible exception when recipe is from a private user that the user does not follow`() {
        // given a recipe from a private user that the user does not follow
        val privateRecipeId = 3

        // mock
        whenever(jdbiRecipeRepositoryMock.getRecipeById(privateRecipeId)).thenReturn(jdbiRecipeModel)
        whenever(jdbiUserRepositoryMock.checkUserVisibility(AUTHOR_USERNAME, USERNAME)).thenReturn(false)

        // when getting the recipe rating
        val exception = assertThrows<RecipeNotAccessible> { getRecipeRate(USERNAME, privateRecipeId) }

        // then the recipe rating is not returned and throws RecipeNotAccessible exception
        assertEquals(RecipeNotAccessible().message, exception.message)
    }
}
