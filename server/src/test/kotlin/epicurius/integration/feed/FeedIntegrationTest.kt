package epicurius.integration.feed

import epicurius.domain.recipe.Recipe
import epicurius.domain.user.AuthenticatedUser
import epicurius.http.controllers.feed.models.output.GetUserFeedOutputModel
import epicurius.http.utils.Uris
import epicurius.integration.EpicuriusIntegrationTest
import epicurius.integration.utils.get
import epicurius.integration.utils.patch
import epicurius.utils.createTestRecipe
import epicurius.utils.createTestUser
import org.junit.jupiter.api.BeforeEach

class FeedIntegrationTest : EpicuriusIntegrationTest() {

    lateinit var testUser: AuthenticatedUser
    lateinit var testAuthorUser: AuthenticatedUser
    lateinit var testRecipe: Recipe

    @BeforeEach
    fun setup() {
        testUser = createTestUser(tm)
        testAuthorUser = createTestUser(tm)
        testRecipe = createTestRecipe(tm, fs, testAuthorUser.user)
    }

    fun getFeed(token: String, skip: Int = 0, limit: Int = 10) =
        get<GetUserFeedOutputModel>(
            client,
            api("${Uris.User.USER_FEED}?skip=$skip&limit=$limit"),
            token = token
        )

    fun follow(token: String, username: String) {
        patch<Unit>(
            client,
            api(Uris.User.USER_FOLLOW.replace("{name}", username)),
            body = "",
            token = token
        )
    }
}
