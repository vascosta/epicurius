package epicurius.repository.jdbi.feed.contract

import epicurius.domain.PagingParams
import epicurius.repository.jdbi.recipe.models.JdbiRecipeInfo

interface FeedRepository {

    fun getFeed(userId: Int, pagingParams: PagingParams): List<JdbiRecipeInfo>
}