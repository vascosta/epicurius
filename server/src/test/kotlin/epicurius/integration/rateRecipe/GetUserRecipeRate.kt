package epicurius.integration.rateRecipe

import epicurius.domain.exceptions.RecipeNotAccessible
import epicurius.domain.exceptions.RecipeNotFound
import epicurius.domain.exceptions.UserHasNotRated
import epicurius.http.utils.Problem
import epicurius.http.utils.Uris
import epicurius.integration.utils.get
import org.junit.jupiter.api.Assertions.assertEquals
import org.springframework.http.HttpStatus
import kotlin.test.Test
import kotlin.test.assertNotNull

class GetUserRecipeRate : RateRecipeIntegrationTest() {

    @Test
    fun `Should get user recipe rate successfully with code 200`() {
        // given a user token and a recipe id
        val token = testUser.token

        // when the user rates a recipe
        rateRecipe(token, testRecipe.id, RATING_3)

        // when the user gets their recipe rate
        val response = getUserRecipeRate(token, testRecipe.id)

        // then the response is successful
        assertNotNull(response)
        assertEquals(RATING_3, response.rating)
    }

    @Test
    fun `Should fail with code 404 when trying to retrieve a user's recipe rate with non existing id`() {
        // given a user token and a non-existing recipe id
        val token = testUser.token
        val nonExistingRecipeId = 9999

        // when the user tries to get their recipe rate
        val error = get<Problem>(
            client,
            api(Uris.Recipe.USER_RECIPE_RATE.replace("{id}", nonExistingRecipeId.toString())),
            responseStatus = HttpStatus.NOT_FOUND,
            token = token
        )

        // then the user recipe rate cannot be retrieved and fails with code 404
        assertNotNull(error)
        assertEquals(RecipeNotFound().message, error.detail)
    }

    @Test
    fun `Should fail with code 403 when trying to retrieve a user's recipe rate without following the recipe's private user author`() {
        // given a user token and a private recipe id
        val token = testUser.token

        // when the user tries to get their recipe rate
        val error = get<Problem>(
            client,
            api(Uris.Recipe.USER_RECIPE_RATE.replace("{id}", testPrivateRecipe.id.toString())),
            responseStatus = HttpStatus.FORBIDDEN,
            token = token
        )

        // then the user recipe rate cannot be retrieved and fails with code 403
        assertNotNull(error)
        assertEquals(RecipeNotAccessible().message, error.detail)
    }

    @Test
    fun `Should fail with code 400 when trying to retrieve a user's recipe rate that has not been rated`() {
        // given a user token and a recipe id
        val token = testUser.token

        // when the user tries to get their recipe rate
        val error = get<Problem>(
            client,
            api(Uris.Recipe.USER_RECIPE_RATE.replace("{id}", testRecipe.id.toString())),
            responseStatus = HttpStatus.BAD_REQUEST,
            token = token
        )

        // then the user recipe rate cannot be retrieved and fails with code 400
        assertNotNull(error)
        assertEquals(UserHasNotRated(testUser.user.id, testRecipe.id).message, error.detail)
    }
}
