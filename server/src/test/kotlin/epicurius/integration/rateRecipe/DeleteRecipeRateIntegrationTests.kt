package epicurius.integration.rateRecipe

import epicurius.domain.exceptions.AuthorCannotDeleteRating
import epicurius.domain.exceptions.RecipeNotAccessible
import epicurius.domain.exceptions.RecipeNotFound
import epicurius.domain.exceptions.UserHasNotRated
import epicurius.http.utils.Problem
import epicurius.http.utils.Uris
import epicurius.integration.utils.delete
import epicurius.integration.utils.getBody
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertNotNull
import org.junit.jupiter.api.Test
import org.springframework.http.HttpStatus

class DeleteRecipeRateIntegrationTests : RateRecipeIntegrationTest() {

    @Test
    fun `Should delete recipe rate successfully with code 204`() {
        // given a user token and a recipe id
        val token = testUser.token

        // when the user rates a recipe
        rateRecipe(token, testRecipe.id, RATING_5)

        // then the recipe is rated successfully
        val response = getRecipeRate(token, testRecipe.id)
        assertNotNull(response)
        assertEquals(RATING_5.toDouble(), response?.rating)

        // and deletes the recipe rate
        deleteRecipeRate(token, testRecipe.id)

        // then the recipe rate is deleted successfully
        val rsp = getRecipeRate(token, testRecipe.id)
        assertNotNull(rsp)
        assertEquals(0.0, rsp?.rating)
    }

    @Test
    fun `Should fail with code 404 when trying to delete a recipe's rate with non existing recipe id`() {
        // given a user token and a non-existing recipe id
        val token = testUser.token
        val nonExistingRecipeId = 9999

        // when the user tries to delete a recipe's rate
        val error = delete<Problem>(
            client,
            api(Uris.Recipe.RATE_RECIPE.replace("{id}", nonExistingRecipeId.toString())),
            responseStatus = HttpStatus.NOT_FOUND,
            token = token
        )

        // then the recipe is not found
        val bodyError = getBody(error)
        assertEquals(RecipeNotFound().message, bodyError.detail)
    }

    @Test
    fun `Should fail with code 403 when recipe's author tries to delete recipe rate`() {
        // given a user token and a recipe id
        val token = authorTestUser.token

        // when the user tries to delete their own recipe's rate
        val error = delete<Problem>(
            client,
            api(Uris.Recipe.RATE_RECIPE.replace("{id}", testRecipe.id.toString())),
            responseStatus = HttpStatus.FORBIDDEN,
            token = token
        )

        // then the recipe is not accessible
        val bodyError = getBody(error)
        assertEquals(AuthorCannotDeleteRating().message, bodyError.detail)
    }

    @Test
    fun `Should fail with code 403 when trying to delete a recipe's rate without following the recipe's private user author`() {
        // given a user token and a private recipe id
        val token = testUser.token

        // when the user tries to delete a recipe's rate
        val error = delete<Problem>(
            client,
            api(Uris.Recipe.RATE_RECIPE.replace("{id}", testPrivateRecipe.id.toString())),
            responseStatus = HttpStatus.FORBIDDEN,
            token = token
        )

        // then the recipe is not accessible
        val bodyError = getBody(error)
        assertEquals(RecipeNotAccessible().message, bodyError.detail)
    }

    @Test
    fun `Should fail with code 400 when trying to delete a recipe's rate that has not been rated`() {
        // given a user token and a recipe id
        val token = testUser.token

        // when the user tries to delete a recipe's rate
        val error = delete<Problem>(
            client,
            api(Uris.Recipe.RATE_RECIPE.replace("{id}", testRecipe.id.toString())),
            responseStatus = HttpStatus.BAD_REQUEST,
            token = token
        )

        // then the recipe has not been rated
        val bodyError = getBody(error)
        assertEquals(UserHasNotRated(testUser.user.id, testRecipe.id).message, bodyError.detail)
    }
}