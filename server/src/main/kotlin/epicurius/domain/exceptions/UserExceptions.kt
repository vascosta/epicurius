package epicurius.domain.exceptions

class UnauthorizedException(msg: String) : RuntimeException(msg)

class UserAlreadyExists : RuntimeException("User already exists")
class UserNotFound(name: String?) : RuntimeException("User $name not found")
class UserAlreadyLoggedIn : RuntimeException("User is already logged in")
class UserAlreadyBeingFollowed(name: String) : RuntimeException("User $name is already being followed by you")
class UserNotFollowed(name: String) : RuntimeException("User $name is not being followed by you")

class FollowRequestAlreadyBeenSent(name: String) : RuntimeException("Follow request to user $name already been sent")
class FollowRequestNotFound(name: String) : RuntimeException("Follow request to user $name not found")

class ProfilePictureNotFound : RuntimeException("Profile picture not found")

class IncorrectPassword : RuntimeException("Incorrect password")
class PasswordsDoNotMatch : RuntimeException("Passwords don't match")

class InvalidToken : RuntimeException("Invalid token")
class InvalidCountry : RuntimeException("Invalid country")
class InvalidIntolerancesIdx : RuntimeException("Invalid intolerance index")
class InvalidIntolerance : RuntimeException("Invalid intolerance")
class InvalidDietIdx : RuntimeException("Invalid diet index")
class InvalidDiet : RuntimeException("Invalid diet")
class InvalidFollowRequestType : RuntimeException("Invalid follow request type")
