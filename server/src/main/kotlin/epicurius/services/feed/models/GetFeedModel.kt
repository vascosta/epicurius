package epicurius.services.feed.models

import epicurius.domain.Diet
import epicurius.domain.Intolerance
import epicurius.domain.PagingParams

data class GetFeedModel (
    val userId: Int,
    val intolerances: List<Intolerance>,
    val diets: List<Diet>,
    val pagingParams: PagingParams
)