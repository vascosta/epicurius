package epicurius.domain.exceptions

class UnauthorizedException(msg: String) : Exception(msg)
class UserAlreadyExits : Exception("User already exists")
class UserNotFound : Exception("User not found")
class UserAlreadyLoggedIn : Exception("User is already logged in")
class InvalidCountry : Exception("Invalid country")
class InvalidPassword : Exception("Invalid password")
class PasswordsDoNotMatch : Exception("Passwords don't match")
