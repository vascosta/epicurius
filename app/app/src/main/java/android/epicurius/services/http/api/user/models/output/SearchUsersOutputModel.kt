package android.epicurius.services.http.api.user.models.output

import android.epicurius.domain.user.SearchUser

data class SearchUsersOutputModel(val users: List<SearchUser>)

typealias GetUserFollowersOutputModel = SearchUsersOutputModel

typealias GetUserFollowingOutputModel = SearchUsersOutputModel

typealias GetUserFollowRequestsOutputModel = SearchUsersOutputModel
