package epicurius.integration.rateRecipe

import epicurius.domain.exceptions.AuthorCannotRateOwnRecipe
import epicurius.domain.exceptions.RecipeNotAccessible
import epicurius.domain.exceptions.RecipeNotFound
import epicurius.domain.exceptions.UserAlreadyRated
import epicurius.http.utils.Problem
import epicurius.http.utils.Uris
import epicurius.integration.utils.getBody
import epicurius.integration.utils.post
import org.junit.jupiter.api.Test
import org.springframework.http.HttpStatus
import kotlin.test.assertEquals

class RateRecipeIntegrationTests : RateRecipeIntegrationTest() {

    @Test
    fun `Should rate recipe successfully with code 204`() {
        // given a user token and a recipe id
        val token = testUser.token

        // when the user rates a recipe
        rateRecipe(token, testRecipe.id, RATING_5)
    }

    @Test
    fun `Should fail with code 404 when trying to rate a recipe with non existing id`() {
        // given a user token and a non-existing recipe id
        val token = testUser.token
        val nonExistingRecipeId = 9999

        // when the user tries to rate a recipe
        val error = post<Problem>(
            client,
            api(Uris.Recipe.RATE_RECIPE.replace("{id}", nonExistingRecipeId.toString())),
            body = mapOf(
                "rating" to RATING_5
            ),
            responseStatus = HttpStatus.NOT_FOUND,
            token = token
        )

        // then the recipe cannot be rated and fails with code 404
        val bodyError = getBody(error)
        assertEquals(RecipeNotFound().message, bodyError.detail)
    }

    @Test
    fun `Should fail with code 403 when recipe's author tries to rate their own recipe`() {
        // given a user token and a recipe id
        val token = authorTestUser.token

        // when the user tries to rate their own recipe
        val error = post<Problem>(
            client,
            api(Uris.Recipe.RATE_RECIPE.replace("{id}", testRecipe.id.toString())),
            body = mapOf(
                "rating" to RATING_5
            ),
            responseStatus = HttpStatus.FORBIDDEN,
            token = token
        )

        // then the recipe cannot be rated and fails with code 403
        val bodyError = getBody(error)
        assertEquals(AuthorCannotRateOwnRecipe().message, bodyError.detail)
    }

    @Test
    fun `Should fail with code 403 when trying to rate a recipe without following the recipe's private user author`() {
        // given a user token and a recipe id
        val token = testUser.token

        // when the user tries to rate a recipe
        val error = post<Problem>(
            client,
            api(Uris.Recipe.RATE_RECIPE.replace("{id}", testPrivateRecipe.id.toString())),
            body = mapOf(
                "rating" to RATING_5
            ),
            responseStatus = HttpStatus.FORBIDDEN,
            token = token
        )

        // then the recipe cannot be rated and fails with code 403
        val bodyError = getBody(error)
        assertEquals(RecipeNotAccessible().message, bodyError.detail)
    }

    @Test
    fun `Should fail with code 409 when trying to rate a recipe that the user has already rated`() {
        // given a user token and a recipe id
        val token = testUser.token

        // when the user tries to rate a recipe
        rateRecipe(token, testRecipe.id, RATING_5)

        val error = post<Problem>(
            client,
            api(Uris.Recipe.RATE_RECIPE.replace("{id}", testRecipe.id.toString())),
            body = mapOf(
                "rating" to RATING_5
            ),
            responseStatus = HttpStatus.CONFLICT,
            token = token
        )

        // then the recipe cannot be rated and fails with code 409
        val bodyError = getBody(error)
        assertEquals(UserAlreadyRated(testUser.user.id, testRecipe.id).message, bodyError.detail)
    }
}