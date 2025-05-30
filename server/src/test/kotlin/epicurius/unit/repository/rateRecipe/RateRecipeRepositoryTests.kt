package epicurius.unit.repository.rateRecipe

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test

class RateRecipeRepositoryTests : RateRecipeRepositoryTest() {

    @Test
    fun `Should rate recipe successfully`() {
        // given a recipe (testRecipe) and a user (testUserPublic)

        // when the user rates the recipe
        val rate = rateRecipe(testRecipe.id, testUserPublic.id, 5)

        // then the rate should be 5.0
        assertEquals(5.0, rate)
    }
}
