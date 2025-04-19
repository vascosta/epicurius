package epicurius.http.user.models.output

import epicurius.domain.user.SearchUser

data class SearchUsersOutputModel(
    val users: List<SearchUser>
)

typealias GetFollowersOutputModel = SearchUsersOutputModel

typealias GetFollowingOutputModel = SearchUsersOutputModel

typealias GetFollowRequestsOutputModel = SearchUsersOutputModel
