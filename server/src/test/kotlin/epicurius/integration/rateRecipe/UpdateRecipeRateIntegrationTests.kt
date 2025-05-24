package epicurius.integration.rateRecipe

import epicurius.domain.exceptions.AuthorCannotUpdateRating
import epicurius.domain.exceptions.RecipeNotAccessible
import epicurius.domain.exceptions.RecipeNotFound
import epicurius.domain.exceptions.UserHasNotRated
import epicurius.http.utils.Problem
import epicurius.http.utils.Uris
import epicurius.integration.utils.getBody
import epicurius.integration.utils.patch
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertNotNull
import org.junit.jupiter.api.Test
import org.springframework.http.HttpStatus

class UpdateRecipeRateIntegrationTests : RateRecipeIntegrationTest() {

    @Test
    fun `Should update recipe rate successfully with code 204`() {
        // given a user token and a recipe id
        val token = testUser.token

        // when the user rates a recipe
        rateRecipe(token, testRecipe.id, RATING_4)

        // then the recipe is rated successfully
        val response = getRecipeRate(token, testRecipe.id)
        assertNotNull(response)
        assertEquals(RATING_4.toDouble(), response?.rating)

        // and updates the recipe rate
        updateRecipeRate(token, testRecipe.id, RATING_5)

        // then the recipe rate is updated successfully
        val rsp = getRecipeRate(token, testRecipe.id)
        assertNotNull(rsp)
        assertEquals(RATING_5.toDouble(), rsp?.rating)
    }

    @Test
    fun `Should fail with code 404 when trying to update a recipe's rate with non existing recipe id`() {
        // given a user token and a non-existing recipe id
        val token = testUser.token
        val nonExistingRecipeId = 9999

        // when the user tries to update a recipe's rate
        val error = patch<Problem>(
            client,
            api(Uris.Recipe.RATE_RECIPE.replace("{id}", nonExistingRecipeId.toString())),
            body = mapOf(
                "rating" to RATING_5
            ),
            responseStatus = HttpStatus.NOT_FOUND,
            token = token
        )

        // then the recipe rate cannot be updated and fails with code 404
        val bodyError = getBody(error)
        assertEquals(RecipeNotFound().message, bodyError.detail)
    }

    @Test
    fun `Should fail with code 403 when recipe's author tries to update recipe rate`() {
        // given a user token and a recipe id
        val token = authorTestUser.token

        // when the user tries to update their own recipe's rate
        val error = patch<Problem>(
            client,
            api(Uris.Recipe.RATE_RECIPE.replace("{id}", testRecipe.id.toString())),
            body = mapOf(
                "rating" to RATING_5
            ),
            responseStatus = HttpStatus.FORBIDDEN,
            token = token
        )

        // then the recipe rate cannot be updated and fails with code 403
        val bodyError = getBody(error)
        assertEquals(AuthorCannotUpdateRating().message, bodyError.detail)
    }

    @Test
    fun `Should fail with code 403 when trying to update a recipe's rate without following the recipe's private user author`() {
        // given a user token and a private recipe id
        val token = testUser.token

        // when the user tries to update a recipe's rate
        val error = patch<Problem>(
            client,
            api(Uris.Recipe.RATE_RECIPE.replace("{id}", testPrivateRecipe.id.toString())),
            body = mapOf(
                "rating" to RATING_5
            ),
            responseStatus = HttpStatus.FORBIDDEN,
            token = token
        )

        // then the recipe rate cannot be updated and fails with code 403
        val bodyError = getBody(error)
        assertEquals(RecipeNotAccessible().message, bodyError.detail)
    }

    @Test
    fun `Should fail with code 400 when trying to update user's rate that does not exist`() {
        // given a user token and a recipe id
        val token = testUser.token

        // when the user tries to update their recipe rate
        val error = patch<Problem>(
            client,
            api(Uris.Recipe.RATE_RECIPE.replace("{id}", testRecipe.id.toString())),
            body = mapOf(
                "rating" to RATING_5
            ),
            responseStatus = HttpStatus.BAD_REQUEST,
            token = token
        )

        // then the recipe rate cannot be updated and fails with code 400
        val bodyError = getBody(error)
        assertEquals(UserHasNotRated(testUser.user.id, testRecipe.id).message, bodyError.detail)
    }
}
