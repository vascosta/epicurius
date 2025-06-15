package epicurius.domain.exceptions

class MissingUserToken : RuntimeException("Missing user token")
class AuthenticatedUserNotFound : RuntimeException("Authenticated user not found")

class UserAlreadyExists : RuntimeException("User already exists")
class UserNotFound(name: String?) : RuntimeException("User $name not found")
class UserAlreadyLoggedIn : RuntimeException("User is already logged in")
class UserAlreadyBeingFollowed(name: String) : RuntimeException("User $name is already being followed by you")
class UserNotFollowed(name: String) : RuntimeException("User $name is not being followed by you")

class FollowRequestAlreadyBeenSent(name: String) : RuntimeException("Follow request to user $name already been sent")
class FollowRequestNotFound(name: String) : RuntimeException("Follow request to user $name not found")

class IncorrectPassword : RuntimeException("Incorrect password")

class PictureNotFound : RuntimeException("Picture not found")

class InvalidToken : RuntimeException("Invalid token")
class InvalidCountry : RuntimeException("Invalid country")
class InvalidIntolerancesIdx : RuntimeException("Invalid intolerance index")
class InvalidDietIdx : RuntimeException("Invalid diet index")
class InvalidFollowRequestType : RuntimeException("Invalid follow request type")
class InvalidSelfFollow : RuntimeException("You cannot follow yourself")
class InvalidSelfUnfollow : RuntimeException("You cannot unfollow yourself")
class InvalidSelfCancelFollowRequest : RuntimeException("You cannot cancel a follow request to yourself")
class InvalidSelfAcceptFollowRequest : RuntimeException("You cannot accept a follow request from yourself")
class InvalidSelfRejectFollowRequest : RuntimeException("You cannot reject a follow request from yourself")
