package epicurius.domain.exceptions

class UnauthorizedException(msg: String) : Exception(msg)

class UserAlreadyExists : Exception("User already exists")
class UserNotFound(name: String?) : Exception("User $name not found")
class UserAlreadyLoggedIn : Exception("User is already logged in")
class UserAlreadyBeingFollowed(name: String) : Exception("User $name is already being followed by you")
class UserNotFollowed(name: String) : Exception("User $name is not being followed by you")

class FollowRequestAlreadyBeenSent(name: String) : Exception("Follow request to user $name already been sent")
class FollowRequestNotFound(name: String) : Exception("Follow request to user $name not found")

class ProfilePictureNotFound : Exception("Profile picture not found")

class IncorrectPassword : Exception("Incorrect password")
class PasswordsDoNotMatch : Exception("Passwords don't match")

class InvalidToken : Exception("Invalid token")
class InvalidCountry : Exception("Invalid country")
class InvalidIntolerancesIdx : Exception("Invalid intolerance index")
class InvalidIntolerance : Exception("Invalid intolerance")
class InvalidDietIdx : Exception("Invalid diet index")
class InvalidDiet : Exception("Invalid diet")
class InvalidFollowRequestType : Exception("Invalid follow request type")
