package epicurius.http.user.models.output

import epicurius.domain.user.FollowUser

data class GetFollowersOutputModel(
    val followers: List<FollowUser>
)

typealias GetFollowingOutputModel = GetFollowersOutputModel

typealias GetFollowRequestsOutputModel = GetFollowersOutputModel