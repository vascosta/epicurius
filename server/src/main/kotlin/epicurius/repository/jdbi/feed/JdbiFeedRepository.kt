package epicurius.repository.jdbi.feed

import epicurius.domain.user.FollowingStatus
import epicurius.repository.jdbi.feed.contract.FeedRepository
import epicurius.repository.jdbi.recipe.models.JdbiRecipeInfo
import epicurius.services.feed.models.GetFeedModel
import org.jdbi.v3.core.Handle
import org.jdbi.v3.core.kotlin.mapTo

class JdbiFeedRepository(private val handle: Handle) : FeedRepository {

    override fun getFeed(info: GetFeedModel): List<JdbiRecipeInfo> {
        return handle.createQuery(
            """
                SELECT r.id as recipe_id, r.name as recipe_name, r.cuisine, r.meal_type, r.preparation_time, r.servings, r.pictures_names
                FROM dbo.followers f 
                JOIN dbo.recipe r ON f.user_id = r.author_id
                WHERE f.follower_id = :userId 
                AND status = :status 
                AND NOT (r.intolerances && :intolerances) 
                AND r.diets @> :diets
                ORDER BY r.date DESC, r.id DESC
                LIMIT :limit OFFSET :skip
            """
        )
            .bind("userId", info.userId)
            .bind("status", FollowingStatus.ACCEPTED.ordinal)
            .bind("intolerances", info.intolerances.map { it.ordinal }.toTypedArray())
            .bind("diets", info.diets.map { it.ordinal }.toTypedArray())
            .bind("limit", info.pagingParams.limit)
            .bind("skip", info.pagingParams.skip)
            .mapTo<JdbiRecipeInfo>()
            .list()
    }
}
