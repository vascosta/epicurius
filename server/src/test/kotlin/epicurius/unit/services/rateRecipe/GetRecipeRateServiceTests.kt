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
        // given a user id and a recipe id (USER_ID, RECIPE_ID)

        // mock
        whenever(jdbiRecipeRepositoryMock.getRecipeById(RECIPE_ID)).thenReturn(jdbiRecipeModel)
        whenever(jdbiUserRepositoryMock.checkUserVisibility(AUTHOR_USERNAME, USER_ID)).thenReturn(true)
        whenever(jdbiRateRecipeRepositoryMock.getRecipeRate(RECIPE_ID)).thenReturn(RATING_5.toDouble())

        // when getting the recipe rating
        val rate = getRecipeRate(USER_ID, RECIPE_ID)

        // then the recipe rating is returned successfully
        assertEquals(RATING_5.toDouble(), rate)
    }

    @Test
    fun `Should throw RecipeNotFound exception when getting rating of a non-existing recipe`() {
        // given a non-existing recipe id
        val nonExistingRecipeId = 9999

        // mock
        whenever(jdbiRecipeRepositoryMock.getRecipeById(nonExistingRecipeId)).thenReturn(null)

        // when getting the recipe rating
        // then the recipe rating is not returned and throws RecipeNotFound exception
        assertThrows<RecipeNotFound> { getRecipeRate(USER_ID, nonExistingRecipeId) }
    }

    @Test
    fun `Should throw RecipeNotAccessible exception when recipe is from a private user that the user does not follow`() {
        // given a recipe from a private user that the user does not follow
        val privateRecipeId = 3

        // mock
        whenever(jdbiRecipeRepositoryMock.getRecipeById(privateRecipeId)).thenReturn(jdbiRecipeModel)
        whenever(jdbiUserRepositoryMock.checkUserVisibility(AUTHOR_USERNAME, USER_ID)).thenReturn(false)

        // when getting the recipe rating
        // then the recipe rating is not returned and throws RecipeNotAccessible exception
        assertThrows<RecipeNotAccessible> { getRecipeRate(USER_ID, privateRecipeId) }
    }
}
