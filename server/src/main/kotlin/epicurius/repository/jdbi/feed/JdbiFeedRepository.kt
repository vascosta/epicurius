package epicurius.repository.jdbi.feed

import epicurius.domain.PagingParams
import epicurius.domain.user.FollowingStatus
import epicurius.repository.jdbi.feed.contract.FeedRepository
import epicurius.repository.jdbi.recipe.models.JdbiRecipeInfo
import org.jdbi.v3.core.Handle
import org.jdbi.v3.core.kotlin.mapTo

class JdbiFeedRepository(private val handle: Handle) : FeedRepository {

    override fun getFeed(userId: Int, pagingParams: PagingParams): List<JdbiRecipeInfo> {
        return handle.createQuery(
            """
                SELECT r.id, r.name, r.cuisine, r.meal_type, r.preparation_time, r.servings, r.pictures_names
                FROM dbo.followers f 
                JOIN dbo.recipe r ON f.user_id = r.author_id
                WHERE f.follower_id = :userId AND status = :status
                ORDER BY r.date DESC, r.id DESC
                LIMIT :limit OFFSET :skip
            """
        )
            .bind("userId", userId)
            .bind("status", FollowingStatus.ACCEPTED.ordinal)
            .bind("limit", pagingParams.limit)
            .bind("skip", pagingParams.skip)
            .mapTo<JdbiRecipeInfo>()
            .list()
    }
}