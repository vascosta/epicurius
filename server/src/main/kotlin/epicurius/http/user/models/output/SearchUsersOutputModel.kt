package epicurius.http.user.models.output

import epicurius.domain.user.SearchUser

data class SearchUsersOutputModel(val users: List<SearchUser>)

typealias GetUserFollowersOutputModel = SearchUsersOutputModel

typealias GetUserFollowingOutputModel = SearchUsersOutputModel

typealias GetUserFollowRequestsOutputModel = SearchUsersOutputModel
