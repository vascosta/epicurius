package epicurius.domain.exceptions

class UnauthorizedException(msg: String) : Exception(msg)
class UserAlreadyExits : Exception("epicurius.domain.user.User already exists")
class UserNotFound(username: String?) : Exception("epicurius.domain.user.User $username not found")
class UserAlreadyLoggedIn : Exception("epicurius.domain.user.User is already logged in")
class InvalidCountry : Exception("Invalid country")
class IncorrectPassword : Exception("Incorrect password")
class PasswordsDoNotMatch : Exception("Passwords don't match")
