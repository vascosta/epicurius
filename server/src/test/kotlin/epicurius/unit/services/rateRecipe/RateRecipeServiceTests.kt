package epicurius.unit.services.rateRecipe

import epicurius.domain.exceptions.AuthorCannotRateOwnRecipe
import epicurius.domain.exceptions.RecipeNotAccessible
import epicurius.domain.exceptions.RecipeNotFound
import epicurius.domain.exceptions.UserAlreadyRated
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import org.mockito.kotlin.verify
import org.mockito.kotlin.whenever
import kotlin.test.assertEquals

class RateRecipeServiceTests : RateRecipeServiceTest() {

    @Test
    fun `Should rate recipe successfully`() {
        // given a user (USER_ID), a recipe (RECIPE_ID) and a rating (RATING_5)

        // mock
        whenever(jdbiRecipeRepositoryMock.getRecipeById(RECIPE_ID)).thenReturn(jdbiRecipeModel)
        whenever(jdbiUserRepositoryMock.checkUserVisibility(AUTHOR_USERNAME, USERNAME)).thenReturn(true)
        whenever(jdbiRateRecipeRepositoryMock.checkIfUserAlreadyRated(USER_ID, RECIPE_ID)).thenReturn(false)
        whenever(jdbiRateRecipeRepositoryMock.getRecipeRate(RECIPE_ID)).thenReturn(RATING_5.toDouble())

        // when rating the recipe
        rateRecipe(USER_ID, USERNAME, RECIPE_ID, RATING_5)

        // then the recipe is rated successfully
        verify(jdbiRateRecipeRepositoryMock).rateRecipe(RECIPE_ID, USER_ID, RATING_5)
        val rating = rateRecipeService.getRecipeRate(USERNAME, RECIPE_ID)
        assertEquals(RATING_5.toDouble(), rating)
    }

    @Test
    fun `Should throw RecipeNotFound exception when recipe does not exist`() {
        // given a user (USER_ID) and a recipe (RECIPE_ID) that does not exist
        val nonExistingRecipeId = 9999

        // mock
        whenever(jdbiRecipeRepositoryMock.getRecipeById(nonExistingRecipeId)).thenReturn(null)

        // when getting the recipe rate
        val exception = assertThrows<RecipeNotFound> {
            rateRecipe(USER_ID, USERNAME, nonExistingRecipeId, RATING_5)
        }

        // then the exception is thrown
        assertEquals(RecipeNotFound().message, exception.message)
    }

    @Test
    fun `Should throw AuthorCannotRateOwnRecipe exception when user is the author and tries to rate their own recipe`() {
        // given a user (USER_ID) and a recipe (RECIPE_ID) that belongs to him

        // mock
        whenever(jdbiRecipeRepositoryMock.getRecipeById(RECIPE_ID)).thenReturn(jdbiRecipeModel)
        whenever(jdbiUserRepositoryMock.checkUserVisibility(AUTHOR_USERNAME, USERNAME)).thenReturn(true)

        // when rating the recipe
        val exception = assertThrows<AuthorCannotRateOwnRecipe> {
            rateRecipe(AUTHOR_ID, AUTHOR_USERNAME, RECIPE_ID, RATING_5)
        }

        // then the exception is thrown
        assertEquals(AuthorCannotRateOwnRecipe().message, exception.message)
    }

    @Test
    fun `Should throw RecipeNotAccessible exception when recipe is from a private user that the user does not follow`() {
        // given a user (USER_ID) and a recipe (RECIPE_ID) that belongs to a private user that the user does not follow

        // mock
        whenever(jdbiRecipeRepositoryMock.getRecipeById(RECIPE_ID)).thenReturn(jdbiRecipeModel)
        whenever(jdbiUserRepositoryMock.checkUserVisibility(AUTHOR_USERNAME, USERNAME)).thenReturn(false)

        // when rating the recipe
        val exception = assertThrows<RecipeNotAccessible> {
            rateRecipe(USER_ID, USERNAME, RECIPE_ID, RATING_5)
        }

        // then the exception is thrown
        assertEquals(RecipeNotAccessible().message, exception.message)
    }

    @Test
    fun `Should throw UserAlreadyRated exception when user has already rated the recipe`() {
        // given a user (USER_ID) and a recipe (RECIPE_ID) that the user has already rated

        // mock
        whenever(jdbiRecipeRepositoryMock.getRecipeById(RECIPE_ID)).thenReturn(jdbiRecipeModel)
        whenever(jdbiUserRepositoryMock.checkUserVisibility(AUTHOR_USERNAME, USERNAME)).thenReturn(true)
        whenever(jdbiRateRecipeRepositoryMock.checkIfUserAlreadyRated(USER_ID, RECIPE_ID)).thenReturn(true)

        // when rating the recipe
        val exception = assertThrows<UserAlreadyRated> {
            rateRecipe(USER_ID, USERNAME, RECIPE_ID, RATING_5)
        }

        // then the exception is thrown
        assertEquals(UserAlreadyRated(USER_ID, RECIPE_ID).message, exception.message)
    }
}
