package epicurius.unit.repository.rateRecipe

import org.junit.jupiter.api.Assertions.assertEquals
import kotlin.test.Test

class UpdateUserRecipeRatingRepositoryTests : RateRecipeRepositoryTest() {

    @Test
    fun `Should update the user recipe rating`() {
        // given a recipe (testRecipe) and a user (testUserPublic)
        val newRating = 5

        // when the user rates the recipe
        rateRecipe(testRecipe.id, testUserPrivate.id, 3)

        // when updating the user recipe rating
        val rate = updateUserRecipeRating(testRecipe.id, testUserPrivate.id, newRating)

        // then the rate should be 5.0
        assertEquals(newRating.toDouble(), rate)
    }
}
