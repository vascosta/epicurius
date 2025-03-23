package epicurius.domain.exceptions

class UnauthorizedException(msg: String) : Exception(msg)
class UserAlreadyExists : Exception("User already exists")
class UserNotFound(username: String?) : Exception("User $username not found")
class UserAlreadyLoggedIn : Exception("User is already logged in")
class InvalidCountry : Exception("Invalid country")
class IncorrectPassword : Exception("Incorrect password")
class PasswordsDoNotMatch : Exception("Passwords don't match")
class InvalidIntolerancesIdx : Exception("Invalid intolerance index")
class InvalidIntolerance : Exception("Invalid intolerance")
class InvalidDietIdx : Exception("Invalid diet index")
class InvalidDiet : Exception("Invalid diet")
