package epicurius.unit.repository.rateRecipe

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test

class RateRecipeRepositoryTests : RateRecipeRepositoryTest() {

    @Test
    fun `Should rate recipe successfully`() {
        // given a recipe (testRecipe) and a user (testUserPublic)

        // when the user rates the recipe
        rateRecipe(testRecipe.id, testUserPublic.user.id, 5)

        // when getting the recipe rate
        val rate = getRecipeRate(testRecipe.id)

        // then the rate should be 5.0
        assertEquals(5.0, rate)
    }

    @Test
    fun `Should get the average rate of a recipe`() {
        // given a recipe (testRecipe) and a user (testUserPublic)
        val average = listOf(5, 3).average()

        // when the user rates the recipe
        rateRecipe(testRecipe2.id, testUserPublic.user.id, 5)
        rateRecipe(testRecipe2.id, testUserPrivate.user.id, 3)

        // when getting the recipe rate
        val rate = getRecipeRate(testRecipe2.id)

        // then the rate should be 5.0
        assertEquals(average, rate)
    }
}
