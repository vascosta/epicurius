package epicurius.unit.repository.rateRecipe

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test

class GetUserRecipeRateRepositoryTests : RateRecipeRepositoryTest() {

    @Test
    fun `Should get recipe user rate`() {
        // given a recipe (testRecipe)
        // and a user (testUser)

        // when rating the recipe
        rateRecipe(testRecipe.id, testUserPublic.id, 4)

        // when getting the recipe user rate
        val rate = getRecipeUserRate(testRecipe.id, testUserPublic.id)

        // then the rate should be 0
        assertEquals(4, rate)
    }
}
