package epicurius.integration.rateRecipe

import epicurius.domain.recipe.Recipe
import epicurius.domain.user.AuthenticatedUser
import epicurius.http.controllers.rateRecipe.models.output.GetRecipeRatingOutputModel
import epicurius.http.controllers.rateRecipe.models.output.GetUserRecipeRatingOutputModel
import epicurius.http.controllers.rateRecipe.models.output.RateRecipeOutputModel
import epicurius.http.controllers.rateRecipe.models.output.UpdateUserRecipeRatingOutputModel
import epicurius.http.media.Uris
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
        testRecipe = createTestRecipe(tm, authorTestUser.user)

        val privateUser = createTestUser(tm, true)
        testPrivateRecipe = createTestRecipe(tm, privateUser.user)
    }

    fun getRecipeRating(token: String, recipeId: Int) =
        get<GetRecipeRatingOutputModel>(
            client,
            api(Uris.Recipe.RATE_RECIPE.replace("{id}", recipeId.toString())),
            responseStatus = HttpStatus.OK,
            token = token
        )

    fun getUserRecipeRating(token: String, recipeId: Int) =
        get<GetUserRecipeRatingOutputModel>(
            client,
            api(Uris.Recipe.USER_RECIPE_RATING.replace("{id}", recipeId.toString())),
            responseStatus = HttpStatus.OK,
            token = token
        )

    fun rateRecipe(token: String, recipeId: Int, rating: Int) =
        post<RateRecipeOutputModel>(
            client,
            api(Uris.Recipe.RATE_RECIPE.replace("{id}", recipeId.toString())),
            body = mapOf("rating" to rating),
            responseStatus = HttpStatus.CREATED,
            token = token
        )

    fun updateUserRecipeRating(token: String, recipeId: Int, rating: Int) =
        patch<UpdateUserRecipeRatingOutputModel>(
            client,
            api(Uris.Recipe.RATE_RECIPE.replace("{id}", recipeId.toString())),
            body = mapOf("rating" to rating),
            responseStatus = HttpStatus.OK,
            token = token
        )

    fun deleteUserRecipeRating(token: String, recipeId: Int) =
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
