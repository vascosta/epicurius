package epicurius.integration.rateRecipe

import epicurius.domain.exceptions.RecipeNotAccessible
import epicurius.domain.exceptions.RecipeNotFound
import epicurius.http.utils.Problem
import epicurius.http.utils.Uris
import epicurius.integration.utils.get
import epicurius.utils.createTestUser
import org.junit.jupiter.api.Test
import org.springframework.http.HttpStatus
import kotlin.test.assertEquals
import kotlin.test.assertNotNull

class GetRecipeRateIntegrationTests : RateRecipeIntegrationTest() {

    companion object {
        val testUser1 = createTestUser(tm)
        val testUser2 = createTestUser(tm)
    }

    @Test
    fun `Should retrieve recipe's rate successfully with code 200`() {
        // given a user token and a recipe id
        val token = testUser.token

        // when two users rate the same recipe
        rateRecipe(testUser1.token, testRecipe.id, RATING_5)
        rateRecipe(testUser2.token, testRecipe.id, RATING_4)

        // and when another user retrieves the recipe's rate
        val response = getRecipeRate(token, testRecipe.id)

        // then the recipe's rate is retrieved successfully
        val avg = (RATING_5 + RATING_4) / 2.0
        assertNotNull(response)
        assertEquals(avg, response.rating)
    }

    @Test
    fun `Should fail with code 404 when trying to retrieve a recipe's rate with non existing id`() {
        // given a user token and a non-existing recipe id
        val token = testUser.token
        val nonExistingRecipeId = 9999

        // when the user tries to retrieve a recipe's rate
        val error = get<Problem>(
            client,
            api(Uris.Recipe.RATE_RECIPE.replace("{id}", nonExistingRecipeId.toString())),
            responseStatus = HttpStatus.NOT_FOUND,
            token = token
        )

        // then the recipe rate cannot be retrieved and fails with code 404
        assertNotNull(error)
        assertEquals(RecipeNotFound().message, error.detail)
    }

    @Test
    fun `Should fail with code 403 when trying to retrieve a recipe's rate without following the recipe's private user author`() {
        // given a user token and a private recipe id
        val token = testUser.token

        // when the user tries to retrieve a recipe's rate
        val error = get<Problem>(
            client,
            api(Uris.Recipe.RATE_RECIPE.replace("{id}", testPrivateRecipe.id.toString())),
            responseStatus = HttpStatus.FORBIDDEN,
            token = token
        )

        // then the recipe rate cannot be retrieved and fails with code 403
        assertNotNull(error)
        assertEquals(RecipeNotAccessible().message, error.detail)
    }
}