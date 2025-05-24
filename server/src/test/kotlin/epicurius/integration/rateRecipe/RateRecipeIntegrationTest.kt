package epicurius.integration.rateRecipe

import epicurius.domain.recipe.Recipe
import epicurius.domain.user.AuthenticatedUser
import epicurius.http.controllers.rateRecipe.models.output.GetRecipeRateOutputModel
import epicurius.http.controllers.rateRecipe.models.output.GetUserRecipeRateOutputModel
import epicurius.http.utils.Uris
import epicurius.integration.EpicuriusIntegrationTest
import epicurius.integration.utils.delete
import epicurius.integration.utils.get
import epicurius.integration.utils.patch
import epicurius.integration.utils.post
import epicurius.utils.createTestRecipe
import epicurius.utils.createTestUser
import org.junit.jupiter.api.BeforeEach
import org.springframework.http.HttpStatus

class RateRecipeIntegrationTest : EpicuriusIntegrationTest() {

    lateinit var testUser: AuthenticatedUser
    lateinit var authorTestUser: AuthenticatedUser
    lateinit var testRecipe: Recipe
    lateinit var testPrivateRecipe: Recipe

    @BeforeEach
    fun setup() {
        testUser = createTestUser(tm)
        authorTestUser = createTestUser(tm)
        testRecipe = createTestRecipe(tm, fs, authorTestUser.user)

        val privateUser = createTestUser(tm, true)
        testPrivateRecipe = createTestRecipe(tm, fs, privateUser.user)
    }

    fun getRecipeRate(token: String, recipeId: Int) =
        get<GetRecipeRateOutputModel>(
            client,
            api(Uris.Recipe.RATE_RECIPE.replace("{id}", recipeId.toString())),
            responseStatus = HttpStatus.OK,
            token = token
        )

    fun getUserRecipeRate(token: String, recipeId: Int) =
        get<GetUserRecipeRateOutputModel>(
            client,
            api(Uris.Recipe.USER_RECIPE_RATE.replace("{id}", recipeId.toString())),
            responseStatus = HttpStatus.OK,
            token = token
        )

    fun rateRecipe(token: String, recipeId: Int, rating: Int) =
        post<Unit>(
            client,
            api(Uris.Recipe.RATE_RECIPE.replace("{id}", recipeId.toString())),
            body = mapOf("rating" to rating),
            responseStatus = HttpStatus.NO_CONTENT,
            token = token
        )

    fun updateRecipeRate(token: String, recipeId: Int, rating: Int) =
        patch<Unit>(
            client,
            api(Uris.Recipe.RATE_RECIPE.replace("{id}", recipeId.toString())),
            body = mapOf("rating" to rating),
            responseStatus = HttpStatus.NO_CONTENT,
            token = token
        )

    fun deleteRecipeRate(token: String, recipeId: Int) =
        delete<Unit>(
            client,
            api(Uris.Recipe.RATE_RECIPE.replace("{id}", recipeId.toString())),
            responseStatus = HttpStatus.NO_CONTENT,
            token = token
        )

    companion object {
        const val RATING_5 = 5
        const val RATING_4 = 4
        const val RATING_3 = 3
    }
}
