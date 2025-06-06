package epicurius.services.feed.models

import epicurius.domain.Diet
import epicurius.domain.Intolerance

data class GetFeedModel(
    val userId: Int,
    val intolerances: List<Intolerance>,
    val diets: List<Diet>,
    val lastRecipeId: Int?,
    val limit: Int
)
