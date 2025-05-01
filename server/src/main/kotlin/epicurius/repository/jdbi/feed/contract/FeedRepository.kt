package epicurius.repository.jdbi.feed.contract

import epicurius.repository.jdbi.recipe.models.JdbiRecipeInfo
import epicurius.services.feed.models.GetFeedModel

interface FeedRepository {

    fun getFeed(info: GetFeedModel): List<JdbiRecipeInfo>
}
