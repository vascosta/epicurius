package epicurius.unit.repository.rateRecipe

import kotlin.test.Test
import kotlin.test.assertEquals

class GetRateRecipeRepositoryTests : RateRecipeRepositoryTest() {

    @Test
    fun `Should get recipe rate`() {
        // given a recipe (testRecipe)

        // when getting the recipe rate
        val rate = getRecipeRate(testRecipe.id)

        // then the rate should be 0.0
        assertEquals(0.0, rate)
    }

    @Test
    fun `Should get the average rate of a recipe`() {
        // given a recipe (testRecipe) and a user (testUserPublic)
        val average = listOf(5, 3).average()

        // when the user rates the recipe
        rateRecipe(testRecipe.id, testUserPublic.id, 5)
        rateRecipe(testRecipe.id, testUserPrivate.id, 3)

        // and when getting the recipe rate
        val rate = getRecipeRate(testRecipe.id)

        // then the rate should be 5.0
        assertEquals(average, rate)
    }
}
