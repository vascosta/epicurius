package epicurius.unit.services.rateRecipe

import epicurius.domain.exceptions.AuthorCannotUpdateRating
import epicurius.domain.exceptions.RecipeNotAccessible
import epicurius.domain.exceptions.RecipeNotFound
import epicurius.domain.exceptions.UserHasNotRated
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import org.mockito.kotlin.verify
import org.mockito.kotlin.whenever

class UpdateRateRecipeServiceTests : RateRecipeServiceTest() {

    @Test
    fun `Should update recipe rate successfully`() {
        // given a user (USER_ID) who has rated a recipe (RECIPE_ID)

        // mock
        whenever(jdbiRecipeRepositoryMock.getRecipeById(RECIPE_ID)).thenReturn(jdbiRecipeModel)
        whenever(jdbiUserRepositoryMock.checkUserVisibility(AUTHOR_USERNAME, USER_ID)).thenReturn(true)
        whenever(jdbiRateRecipeRepositoryMock.checkIfUserAlreadyRated(USER_ID, RECIPE_ID)).thenReturn(true)

        // when updating the recipe rate
        updateRecipeRate(USER_ID, RECIPE_ID, RATING_3)

        // then the recipe rate is updated successfully
        verify(jdbiRateRecipeRepositoryMock).updateRecipeRate(RECIPE_ID, USER_ID, RATING_3)
    }

    @Test
    fun `Should throw RecipeNotFound exception when recipe does not exist`() {
        // given a user (USER_ID) and a recipe (RECIPE_ID) that does not exist
        val nonExistingRecipeId = 9999

        // mock
        whenever(jdbiRecipeRepositoryMock.getRecipeById(nonExistingRecipeId)).thenReturn(null)

        // when updating the recipe rate
        // then the recipe rate cannot be updated and throws RecipeNotFound exception
        assertThrows<RecipeNotFound> {
            updateRecipeRate(USER_ID, nonExistingRecipeId, RATING_3)
        }
    }

    @Test
    fun `Should throw AuthorCannotUpdateRating exception when user is the author and tries to update their own recipe rating`() {
        // given a user (USER_ID) and a recipe (RECIPE_ID) that belongs to him

        // mock
        whenever(jdbiRecipeRepositoryMock.getRecipeById(RECIPE_ID)).thenReturn(jdbiRecipeModel)
        whenever(jdbiUserRepositoryMock.checkUserVisibility(AUTHOR_USERNAME, AUTHOR_ID)).thenReturn(true)

        // when updating the recipe rate
        // then the recipe rate cannot be updated and throws AuthorCannotUpdateRating exception
        assertThrows<AuthorCannotUpdateRating> {
            updateRecipeRate(AUTHOR_ID, RECIPE_ID, RATING_3)
        }
    }

    @Test
    fun `Should throw RecipeNotAccessible exception when recipe is from a private user that the user does not follow`() {
        // given a user (USER_ID) and a recipe (RECIPE_ID) that is not accessible

        // mock
        whenever(jdbiRecipeRepositoryMock.getRecipeById(RECIPE_ID)).thenReturn(jdbiRecipeModel)
        whenever(jdbiUserRepositoryMock.checkUserVisibility(AUTHOR_USERNAME, USER_ID)).thenReturn(false)

        // when updating the recipe rate
        // then the recipe rate cannot be updated and throws RecipeNotAccessible exception
        assertThrows<RecipeNotAccessible> {
            updateRecipeRate(USER_ID, RECIPE_ID, RATING_3)
        }
    }

    @Test
    fun `Should throw UserHasNotRated exception when user has not rated the recipe`() {
        // given a user (USER_ID) and a recipe (RECIPE_ID) that he has not rated

        // mock
        whenever(jdbiRecipeRepositoryMock.getRecipeById(RECIPE_ID)).thenReturn(jdbiRecipeModel)
        whenever(jdbiUserRepositoryMock.checkUserVisibility(AUTHOR_USERNAME, USER_ID)).thenReturn(true)
        whenever(jdbiRateRecipeRepositoryMock.checkIfUserAlreadyRated(USER_ID, RECIPE_ID)).thenReturn(false)

        // when updating the recipe rate
        // then the recipe rate cannot be updated and throws UserHasNotRated exception
        assertThrows<UserHasNotRated> {
            updateRecipeRate(USER_ID, RECIPE_ID, RATING_3)
        }
    }
}
