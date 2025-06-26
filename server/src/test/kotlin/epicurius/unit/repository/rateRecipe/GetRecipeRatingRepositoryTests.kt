package epicurius.unit.repository.rateRecipe

import kotlin.test.Test
import kotlin.test.assertEquals

class GetRecipeRatingRepositoryTests : RateRecipeRepositoryTest() {

    @Test
    fun `Should get recipe rating`() {
        // given a recipe (testRecipe)

        // when getting the recipe rating
        val rate = getRecipeRating(testRecipe.id)

        // then the rate should be 0.0
        assertEquals(0.0, rate)
    }

    @Test
    fun `Should get the average rating of a recipe`() {
        // given a recipe (testRecipe) and a user (testUserPublic)
        val average = listOf(5, 3).average()

        // when the user rates the recipe
        rateRecipe(testRecipe.id, testUserPublic.id, 5)
        rateRecipe(testRecipe.id, testUserPrivate.id, 3)

        // and when getting the recipe rating
        val rate = getRecipeRating(testRecipe.id)

        // then the rate should be 5.0
        assertEquals(average, rate)
    }
}
