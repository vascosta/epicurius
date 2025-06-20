package epicurius.integration.recipe

import epicurius.domain.exceptions.NotTheRecipeAuthor
import epicurius.domain.exceptions.RecipeNotFound
import epicurius.http.media.Problem
import epicurius.http.media.Uris
import epicurius.integration.utils.delete
import epicurius.integration.utils.getBody
import org.springframework.http.HttpStatus
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotNull

class DeleteRecipeIntegrationTests : RecipeIntegrationTest() {

    @Test
    fun `Should delete a recipe with code 204`() {
        // given a user and a recipe id

        // when deleting the recipe
        // then the recipe is deleted successfully
        val response = deleteRecipe(testAuthor.token, testRecipe.id)
        assertNotNull(response)
    }

    @Test
    fun `Should fail with code 404 when deleting a non-existing recipe`() {
        // given a user and a non-existing recipe id
        val nonExistingRecipeId = 9999

        // when trying to delete the non-existing recipe
        val error = delete<Problem>(
            client,
            api(Uris.Recipe.RECIPE.replace("{id}", nonExistingRecipeId.toString())),
            responseStatus = HttpStatus.NOT_FOUND,
            token = testAuthor.token
        )
        assertNotNull(error)

        // then it should fail with code 404
        val errorBody = getBody(error)
        assertEquals(RecipeNotFound().message, errorBody.detail)
    }

    @Test
    fun `Should fail with code 403 when deleting a recipe that does not belong to the user`() {
        // given a user and a recipe id that does not belong to him

        // when trying to delete the recipe
        val error = delete<Problem>(
            client,
            api(Uris.Recipe.RECIPE.replace("{id}", testRecipe.id.toString())),
            responseStatus = HttpStatus.FORBIDDEN,
            token = testUser.token
        )
        assertNotNull(error)

        // then it should fail with code 403
        val errorBody = getBody(error)
        assertEquals(NotTheRecipeAuthor().message, errorBody.detail)
    }
}
