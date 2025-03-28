package epicurius.http.user.models.input

data class FollowInputModel(val username: String)

typealias UnfollowInputModel = FollowInputModel

typealias CancelFollowRequestInputModel = FollowInputModel
