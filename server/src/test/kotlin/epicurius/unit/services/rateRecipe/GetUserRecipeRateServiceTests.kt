package epicurius.unit.services.rateRecipe

import epicurius.domain.exceptions.RecipeNotAccessible
import epicurius.domain.exceptions.RecipeNotFound
import epicurius.domain.exceptions.UserHasNotRated
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import org.mockito.kotlin.whenever
import kotlin.test.assertEquals

class GetUserRecipeRateServiceTests : RateRecipeServiceTest() {

    @Test
    fun `Should get user's recipe rate successfully`() {
        // given a user id and a recipe id (USER_ID, RECIPE_ID)

        // mock
        whenever(jdbiRecipeRepositoryMock.getRecipeById(RECIPE_ID)).thenReturn(jdbiRecipeModel)
        whenever(jdbiUserRepositoryMock.checkUserVisibility(AUTHOR_USERNAME, USER_ID)).thenReturn(true)
        whenever(jdbiRateRecipeRepositoryMock.checkIfUserAlreadyRated(USER_ID, RECIPE_ID)).thenReturn(true)
        whenever(jdbiRateRecipeRepositoryMock.getUserRecipeRate(RECIPE_ID, USER_ID)).thenReturn(RATING_3)

        // when getting the user's recipe rate
        val rate = getUserRecipeRate(USER_ID, RECIPE_ID)

        // then the rate should be 3
        assertEquals(RATING_3, rate)
    }

    @Test
    fun `Should throw RecipeNotFound exception when recipe does not exist`() {
        // given a non-existing recipe id
        val nonExistingRecipeId = 9999

        // mock
        whenever(jdbiRecipeRepositoryMock.getRecipeById(nonExistingRecipeId)).thenReturn(null)

        // when getting the user's recipe rate
        // then the recipe rating is not returned and throws RecipeNotFound exception
        assertThrows<RecipeNotFound> { getUserRecipeRate(USER_ID, nonExistingRecipeId) }
    }

    @Test
    fun `Should throw RecipeNotAccessible exception when recipe is from a private user that the user does not follow`() {
        // given a recipe id (RECIPE_ID) from a private user that the user (USER_ID) does not follow

        // mock
        whenever(jdbiRecipeRepositoryMock.getRecipeById(RECIPE_ID)).thenReturn(jdbiRecipeModel)
        whenever(jdbiUserRepositoryMock.checkUserVisibility(AUTHOR_USERNAME, USER_ID)).thenReturn(false)

        // when getting the user's recipe rate
        // then the recipe rating is not returned and throws RecipeNotAccessible exception
        assertThrows<RecipeNotAccessible> { getUserRecipeRate(USER_ID, RECIPE_ID) }
    }

    @Test
    fun `Should throw UserHasNotRated exception when user hasn't rated the recipe`() {
        // given a recipe id (RECIPE_ID) and a user id (USER_ID) that hasn't rated the recipe

        // mock
        whenever(jdbiRecipeRepositoryMock.getRecipeById(RECIPE_ID)).thenReturn(jdbiRecipeModel)
        whenever(jdbiUserRepositoryMock.checkUserVisibility(AUTHOR_USERNAME, USER_ID)).thenReturn(true)
        whenever(jdbiRateRecipeRepositoryMock.checkIfUserAlreadyRated(USER_ID, RECIPE_ID)).thenReturn(false)

        // when getting the user's recipe rate
        // then the recipe rating is not returned and throws UserHasNotRated exception
        assertThrows<UserHasNotRated> { getUserRecipeRate(USER_ID, RECIPE_ID) }
    }
}