package epicurius.unit.repository.rateRecipe

import org.junit.jupiter.api.Assertions.assertEquals
import kotlin.test.Test

class DeleteUserRecipeRatingRepositoryTests : RateRecipeRepositoryTest() {

    @Test
    fun `Should delete user recipe rating`() {
        // given a recipe (testRecipe) and a user (testUserPublic)
        val newRating = 5

        // when the user rates the recipe
        rateRecipe(testRecipe.id, testUserPublic.id, newRating)

        // when deleting the user recipe rating
        deleteUserRecipeRating(testRecipe.id, testUserPublic.id)

        // when getting the user recipe rating
        val rate = getRecipeRating(testRecipe.id)

        // then the rate should be 0.0
        assertEquals(0.0, rate)
    }
}
