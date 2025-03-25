package epicurius.http.user.models.output

import epicurius.domain.user.SearchUser

data class GetUsersOutputModel(
    val followers: List<SearchUser>
)

typealias GetFollowersOutputModel = GetUsersOutputModel

typealias GetFollowingOutputModel = GetUsersOutputModel

typealias GetFollowRequestsOutputModel = GetUsersOutputModel