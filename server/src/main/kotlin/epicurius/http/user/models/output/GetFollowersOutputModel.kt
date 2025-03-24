package epicurius.http.user.models.output

import epicurius.domain.user.SocialUser

data class GetFollowersOutputModel(
    val followers: List<SocialUser>
)

typealias GetFollowingOutputModel = GetFollowersOutputModel

typealias GetFollowRequestsOutputModel = GetFollowersOutputModel