package epicurius.unit.repository.rateRecipe

import org.junit.jupiter.api.Assertions.assertEquals
import kotlin.test.Test

class DeleteRateRecipeRepositoryTests : RateRecipeRepositoryTest() {

    @Test
    fun `Should delete recipe rate`() {
        // given a recipe (testRecipe) and a user (testUserPublic)
        val newRating = 5

        // when the user rates the recipe
        rateRecipe(testRecipe5.id, testUserPublic.user.id, newRating)

        // when deleting the recipe rate
        deleteRecipeRate(testRecipe5.id, testUserPublic.user.id)

        // when getting the recipe rate
        val rate = getRecipeRate(testRecipe5.id)

        // then the rate should be 0.0
        assertEquals(0.0, rate)
    }
}
