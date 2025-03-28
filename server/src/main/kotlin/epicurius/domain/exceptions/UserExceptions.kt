package epicurius.domain.exceptions

class UnauthorizedException(msg: String) : Exception(msg)

class UserAlreadyExists : Exception("User already exists")
class UserNotFound(username: String?) : Exception("User $username not found")
class UserAlreadyLoggedIn : Exception("User is already logged in")
class UserAlreadyBeingFollowed(username: String) : Exception("User $username is already being followed by you")
class UserNotFollowed(username: String) : Exception("User $username is not being followed by you")

class FollowRequestAlreadyBeenSent(username: String) : Exception("Follow request to user $username already been sent")
class FollowRequestNotFound(username: String) : Exception("Follow request to user $username not found")

class ProfilePictureNotFound : Exception("Profile picture not found")
class InvalidCountry : Exception("Invalid country")
class IncorrectPassword : Exception("Incorrect password")
class PasswordsDoNotMatch : Exception("Passwords don't match")
class InvalidIntolerancesIdx : Exception("Invalid intolerance index")
class InvalidIntolerance : Exception("Invalid intolerance")
class InvalidDietIdx : Exception("Invalid diet index")
class InvalidDiet : Exception("Invalid diet")
