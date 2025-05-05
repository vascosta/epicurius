package epicurius.unit.repository.rateRecipe

import kotlin.test.Test
import kotlin.test.assertEquals

class GetRateRecipeRepositoryTests : RateRecipeRepositoryTest() {

    @Test
    fun `Should get recipe rate`() {
        // given a recipe (testRecipe)

        // when getting the recipe rate
        val rate = getRecipeRate(testRecipe3.id)

        // then the rate should be 0.0
        assertEquals(0.0, rate)
    }
}
