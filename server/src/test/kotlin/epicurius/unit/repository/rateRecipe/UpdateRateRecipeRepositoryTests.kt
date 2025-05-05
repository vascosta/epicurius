package epicurius.unit.repository.rateRecipe

import org.junit.jupiter.api.Assertions.assertEquals
import kotlin.test.Test

class UpdateRateRecipeRepositoryTests : RateRecipeRepositoryTest() {

    @Test
    fun `Should update recipe rate`() {
        // given a recipe (testRecipe) and a user (testUserPublic)
        val newRating = 5

        // when the user rates the recipe
        rateRecipe(testRecipe4.id, testUserPrivate.id, 3)

        // when updating the recipe rate
        updateRecipeRate(testRecipe4.id, testUserPrivate.id, newRating)

        // when getting the recipe rate
        val rate = getRecipeRate(testRecipe4.id)

        // then the rate should be 5.0
        assertEquals(newRating.toDouble(), rate)
    }
}
