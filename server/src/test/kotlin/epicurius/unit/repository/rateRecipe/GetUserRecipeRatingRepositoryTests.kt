package epicurius.unit.repository.rateRecipe

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test

class GetUserRecipeRatingRepositoryTests : RateRecipeRepositoryTest() {

    @Test
    fun `Should get recipe user rating`() {
        // given a recipe (testRecipe)
        // and a user (testUser)

        // when rating the recipe
        rateRecipe(testRecipe.id, testUserPublic.id, 4)

        // when getting the recipe user rating
        val rate = getUserRecipeRating(testRecipe.id, testUserPublic.id)

        // then the rate should be 0
        assertEquals(4, rate)
    }
}
